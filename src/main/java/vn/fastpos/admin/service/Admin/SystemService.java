package vn.fastpos.admin.service.Admin;

import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.stereotype.Service;
import vn.fastpos.admin.exception.Admin.ApiException;
import vn.fastpos.admin.repository.Admin.DonHangRepository;
import vn.fastpos.admin.system.Admin.BackupEngine;
import vn.fastpos.admin.system.Admin.OperationCoordinator;
import vn.fastpos.admin.system.Admin.SystemState;
import vn.fastpos.admin.system.Admin.SystemState.Job;

@Service
public class SystemService {
  private final SystemState state;
  private final OperationCoordinator coordinator;
  private final BackupEngine engine;
  private final TaiKhoanService users;
  private final DonHangRepository orders;
  private final ExecutorService worker =
      Executors.newSingleThreadExecutor(
          r -> {
            var t = new Thread(r, "fastpos-system-operations");
            t.setDaemon(false);
            return t;
          });

  public SystemService(
      SystemState state,
      OperationCoordinator coordinator,
      BackupEngine engine,
      TaiKhoanService users,
      DonHangRepository orders) {
    this.state = state;
    this.coordinator = coordinator;
    this.engine = engine;
    this.users = users;
    this.orders = orders;
  }

  public Object status() {
    return Map.of(
        "maintenance",
        state.maintenance(),
        "busy",
        coordinator.busy.get(),
        "restoring",
        coordinator.restoring,
        "backupEnabled",
        engine.enabled(),
        "database",
        engine.databaseName(),
        "jobs",
        state.jobs());
  }

  public synchronized void maintenance(
      boolean enabled, String message, String username, String password) {
    if (coordinator.busy.get()) throw ApiException.conflict("Có tác vụ hệ thống đang chạy");
    users.xacThucLai(username, password);
    state.maintenance(enabled, message);
    record("MAINTENANCE", username, "", enabled ? "Bật bảo trì: " + message : "Tắt bảo trì");
  }

  private void record(String kind, String actor, String filename, String message) {
    String now = LocalDateTime.now().toString();
    state.job(
        new Job(UUID.randomUUID().toString(), kind, "SUCCESS", actor, filename, message, now, now));
  }

  public synchronized Object start(
      String kind, String backupId, String confirmation, String username, String password) {
    if (!Set.of("BACKUP", "VERIFY", "RESTORE", "CHECKDB").contains(kind))
      throw ApiException.bad("Tác vụ không hợp lệ");
    if (!engine.enabled())
      throw ApiException.bad("Cần profile SQL Server và bật cấu hình sao lưu trước khi dùng");
    if (coordinator.busy.get()) throw ApiException.conflict("Có tác vụ hệ thống đang chạy");
    users.xacThucLai(username, password);
    String filename = "";
    if (Set.of("VERIFY", "RESTORE").contains(kind))
      filename =
          state.jobs().stream()
              .filter(
                  j ->
                      j.id().equals(backupId)
                          && Set.of("BACKUP", "SAFETY_BACKUP").contains(j.kind())
                          && j.status().equals("SUCCESS"))
              .findFirst()
              .orElseThrow(
                  () -> ApiException.bad("Chỉ dùng bản sao lưu thành công trong danh sách"))
              .filename();
    if (kind.equals("RESTORE")) {
      if (!state.maintenance().enabled())
        throw ApiException.bad("Cần bật bảo trì trước khi khôi phục");
      if (!("KHOI PHUC " + engine.databaseName()).equals(confirmation))
        throw ApiException.bad("Cụm xác nhận khôi phục không đúng");
      if (orders.countByTrangThaiDonIn(
              List.of(
                  "CHO_THANH_TOAN", "DA_THANH_TOAN", "CHO_CHE_BIEN", "DANG_CHE_BIEN", "HOAN_THANH"))
          > 0)
        throw ApiException.conflict(
            "Còn đơn chưa xử lý xong. Hoàn tất hoặc đối soát các đơn trước khi khôi phục");
    }
    if (kind.equals("CHECKDB") && !state.maintenance().enabled())
      throw ApiException.bad("Bật bảo trì trước khi kiểm tra toàn vẹn");
    if (!coordinator.busy.compareAndSet(false, true))
      throw ApiException.conflict("Có tác vụ khác đang chạy");
    String id = UUID.randomUUID().toString(),
        file = kind.equals("BACKUP") ? newFile() : filename,
        now = LocalDateTime.now().toString();
    Job job = new Job(id, kind, "QUEUED", username, file, "Đang chờ xử lý", now, "");
    state.job(job);
    worker.submit(() -> run(job));
    return job;
  }

  private String newFile() {
    return "FastPOS_"
        + engine.databaseName()
        + "_"
        + LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        + "_"
        + UUID.randomUUID().toString().substring(0, 8)
        + ".bak";
  }

  private void run(Job job) {
    boolean exclusive = job.kind().equals("RESTORE");
    var lock = exclusive ? coordinator.database.writeLock() : coordinator.database.readLock();
    String safety = "";
    if (exclusive) coordinator.restoring = true;
    lock.lock();
    try {
      state.job(
          new Job(
              job.id(),
              job.kind(),
              "RUNNING",
              job.actor(),
              job.filename(),
              "Đang thực hiện",
              job.started(),
              ""));
      switch (job.kind()) {
        case "BACKUP" -> engine.backup(job.filename());
        case "VERIFY" -> engine.verify(job.filename());
        case "CHECKDB" -> engine.check();
        case "RESTORE" -> {
          // Check again under the exclusive gate: queued writes may have finished since request
          // validation.
          if (orders.countByTrangThaiDonIn(
                  List.of(
                      "CHO_THANH_TOAN",
                      "DA_THANH_TOAN",
                      "CHO_CHE_BIEN",
                      "DANG_CHE_BIEN",
                      "HOAN_THANH"))
              > 0) throw new IllegalStateException("Còn đơn đang hoạt động");
          engine.verify(job.filename());
          String candidate = newFile();
          engine.backup(candidate);
          safety = candidate;
          record("SAFETY_BACKUP", job.actor(), safety, "Bản an toàn ngay trước khôi phục");
          engine.restore(job.filename());
          engine.check();
        }
      }
      state.job(
          new Job(
              job.id(),
              job.kind(),
              "SUCCESS",
              job.actor(),
              job.filename(),
              exclusive
                  ? "Khôi phục xong; vẫn giữ bảo trì để quản trị viên kiểm tra và đăng nhập lại"
                  : "Tác vụ hoàn tất",
              job.started(),
              LocalDateTime.now().toString()));
    } catch (Exception e) {
      String note =
          "Tác vụ không thành công. Kiểm tra quyền SQL Server, thư mục sao lưu và log máy chủ.";
      org.slf4j.LoggerFactory.getLogger(SystemService.class)
          .error("System operation {} failed", job.id(), e);
      if (exclusive && !safety.isEmpty()) {
        try {
          engine.restore(safety);
          note += " Đã phục hồi về bản an toàn trước thao tác.";
        } catch (Exception rollback) {
          note += " Khôi phục bản an toàn thất bại; giữ bảo trì và liên hệ DBA.";
        }
      }
      state.job(
          new Job(
              job.id(),
              job.kind(),
              "FAILED",
              job.actor(),
              job.filename(),
              note,
              job.started(),
              LocalDateTime.now().toString()));
    } finally {
      if (exclusive) coordinator.sessionEpoch.incrementAndGet();
      lock.unlock();
      coordinator.restoring = false;
      coordinator.busy.set(false);
    }
  }

  @PreDestroy
  public void stop() {
    worker.shutdown();
  }
}
