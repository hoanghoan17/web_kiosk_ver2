package vn.fastpos.admin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Path;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import vn.fastpos.admin.exception.Admin.ApiException;
import vn.fastpos.admin.repository.Admin.DonHangRepository;
import vn.fastpos.admin.service.Admin.SystemService;
import vn.fastpos.admin.service.Admin.TaiKhoanService;
import vn.fastpos.admin.system.Admin.BackupEngine;
import vn.fastpos.admin.system.Admin.OperationCoordinator;
import vn.fastpos.admin.system.Admin.SystemState;
import vn.fastpos.admin.system.Admin.SystemState.Job;

class SystemServiceTest {
  @TempDir Path dir;
  BackupEngine engine;
  TaiKhoanService users;
  DonHangRepository orders;
  SystemState state;
  OperationCoordinator coordinator;
  SystemService service;

  @BeforeEach
  void init() throws Exception {
    engine = mock(BackupEngine.class);
    users = mock(TaiKhoanService.class);
    orders = mock(DonHangRepository.class);
    state = new SystemState(dir.toString(), new ObjectMapper());
    coordinator = new OperationCoordinator();
    when(engine.enabled()).thenReturn(true);
    when(engine.databaseName()).thenReturn("KLCN");
    service = new SystemService(state, coordinator, engine, users, orders);
  }

  @AfterEach
  void close() {
    service.stop();
  }

  private void await() throws Exception {
    long end = System.currentTimeMillis() + 5000;
    while (coordinator.busy.get() && System.currentTimeMillis() < end) Thread.sleep(10);
    assertThat(coordinator.busy.get()).isFalse();
  }

  private Job backup() throws Exception {
    var j = (Job) service.start("BACKUP", null, "", "admin", "secret-password");
    await();
    return state.jobs().stream().filter(x -> x.id().equals(j.id())).findFirst().orElseThrow();
  }

  @Test
  void backupInvokesEngineAndPersistsWithoutPassword() throws Exception {
    var j = backup();
    assertThat(j.status()).isEqualTo("SUCCESS");
    verify(users).xacThucLai("admin", "secret-password");
    verify(engine).backup(j.filename());
    assertThat(java.nio.file.Files.readString(dir.resolve("jobs.json")))
        .doesNotContain("secret-password");
  }

  @Test
  void restoreRequiresMaintenanceAndConfirmation() throws Exception {
    var j = backup();
    assertThatThrownBy(() -> service.start("RESTORE", j.id(), "KHOI PHUC KLCN", "admin", "pw"))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("bảo trì");
    state.maintenance(true, "Bảo trì");
    assertThatThrownBy(() -> service.start("RESTORE", j.id(), "WRONG", "admin", "pw"))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("xác nhận");
  }

  @Test
  void restoreBlocksActiveOrders() throws Exception {
    var j = backup();
    state.maintenance(true, "Bảo trì");
    when(orders.countByTrangThaiDonIn(any())).thenReturn(1L);
    assertThatThrownBy(() -> service.start("RESTORE", j.id(), "KHOI PHUC KLCN", "admin", "pw"))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("đơn");
    verify(engine, never()).restore(any());
  }

  @Test
  void restoreCreatesSafetyBackupBeforeRestore() throws Exception {
    var j = backup();
    state.maintenance(true, "Bảo trì");
    clearInvocations(engine);
    service.start("RESTORE", j.id(), "KHOI PHUC KLCN", "admin", "pw");
    await();
    var order = inOrder(engine);
    order.verify(engine).verify(j.filename());
    order.verify(engine).backup(anyString());
    order.verify(engine).restore(j.filename());
    order.verify(engine).check();
    assertThat(state.maintenance().enabled()).isTrue();
    assertThat(coordinator.sessionEpoch.get()).isEqualTo(1);
    assertThat(state.jobs())
        .anyMatch(x -> x.kind().equals("SAFETY_BACKUP") && x.status().equals("SUCCESS"));
  }

  @Test
  void failedRestoreAttemptsSafetyRollback() throws Exception {
    var j = backup();
    state.maintenance(true, "Bảo trì");
    doThrow(new IllegalStateException("disk error")).when(engine).restore(j.filename());
    service.start("RESTORE", j.id(), "KHOI PHUC KLCN", "admin", "pw");
    await();
    verify(engine, times(2)).restore(anyString());
    assertThat(state.jobs())
        .anyMatch(x -> x.kind().equals("RESTORE") && x.status().equals("FAILED"));
    assertThat(state.maintenance().enabled()).isTrue();
  }

  @Test
  void arbitraryFilenameAndConcurrentJobAreRejected() throws Exception {
    assertThatThrownBy(() -> service.start("RESTORE", "../../other.bak", "", "admin", "pw"))
        .isInstanceOf(ApiException.class);
    coordinator.busy.set(true);
    assertThatThrownBy(() -> service.start("BACKUP", null, "", "admin", "pw"))
        .isInstanceOf(ApiException.class);
    coordinator.busy.set(false);
  }

  @Test
  void interruptedOperationPersistsMaintenance() throws Exception {
    state.job(
        new Job(
            "abc", "RESTORE", "RUNNING", "admin", "file.bak", "running", "2026-10-16T09:00", ""));
    var restored = new SystemState(dir.toString(), new ObjectMapper());
    assertThat(restored.maintenance().enabled()).isTrue();
    assertThat(restored.jobs().get(0).status()).isEqualTo("INTERRUPTED");
  }
}
