package vn.fastpos.admin.system.Admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.fastpos.admin.exception.Admin.ApiException;

@Component
public class SystemState {
  public record Maintenance(boolean enabled, String message) {}

  public record Job(
      String id,
      String kind,
      String status,
      String actor,
      String filename,
      String message,
      String started,
      String finished) {}

  private final Path dir;
  private final ObjectMapper json;
  private Maintenance maintenance;
  private List<Job> jobs;

  public SystemState(
      @Value("${fastpos.system.state-dir:./ops}") String directory, ObjectMapper json)
      throws Exception {
    dir = Path.of(directory).toAbsolutePath();
    this.json = json;
    Files.createDirectories(dir);
    maintenance =
        read(
            "maintenance.json",
            Maintenance.class,
            new Maintenance(false, "Hệ thống đang bảo trì. Vui lòng quay lại sau."));
    jobs = new ArrayList<>(Arrays.asList(read("jobs.json", Job[].class, new Job[0])));
    boolean interrupted =
        jobs.stream().anyMatch(j -> j.status().equals("RUNNING") || j.status().equals("QUEUED"));
    if (interrupted) {
      jobs.replaceAll(
          j ->
              Set.of("RUNNING", "QUEUED").contains(j.status())
                  ? new Job(
                      j.id(),
                      j.kind(),
                      "INTERRUPTED",
                      j.actor(),
                      j.filename(),
                      "Tiến trình bị gián đoạn. Kiểm tra SQL Server trước khi tiếp tục.",
                      j.started(),
                      java.time.LocalDateTime.now().toString())
                  : j);
      maintenance = new Maintenance(true, "Đang kiểm tra hệ thống sau gián đoạn.");
      write("jobs.json", jobs);
      write("maintenance.json", maintenance);
    }
  }

  private <T> T read(String name, Class<T> type, T fallback) throws Exception {
    var file = dir.resolve(name);
    return Files.exists(file) ? json.readValue(file.toFile(), type) : fallback;
  }

  private void write(String name, Object value) {
    try {
      Path temp = Files.createTempFile(dir, "state-", ".tmp");
      json.writeValue(temp.toFile(), value);
      try {
        Files.move(
            temp,
            dir.resolve(name),
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(temp, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Không lưu được trạng thái vận hành", e);
    }
  }

  public synchronized Maintenance maintenance() {
    return maintenance;
  }

  public synchronized void maintenance(boolean value, String message) {
    if (message == null || message.isBlank() || message.length() > 300)
      throw ApiException.bad("Thông báo bảo trì cần 1–300 ký tự");
    maintenance = new Maintenance(value, message.trim());
    write("maintenance.json", maintenance);
  }

  public synchronized List<Job> jobs() {
    return List.copyOf(jobs);
  }

  public synchronized void job(Job job) {
    jobs.removeIf(j -> j.id().equals(job.id()));
    jobs.add(0, job);
    write("jobs.json", jobs);
  }
}
