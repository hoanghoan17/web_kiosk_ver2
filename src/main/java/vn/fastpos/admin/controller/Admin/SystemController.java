package vn.fastpos.admin.controller.Admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastpos.admin.service.Admin.SystemService;

@RestController
@RequestMapping("/api/admin/system")
public class SystemController {
  private final SystemService service;

  public SystemController(SystemService service) {
    this.service = service;
  }

  public record MaintenanceRequest(
      @NotNull Boolean enabled,
      @NotBlank @Size(max = 300) String message,
      @NotBlank @Size(max = 72) String password) {}

  public record JobRequest(
      @NotBlank String kind,
      String backupId,
      String confirmation,
      @NotBlank @Size(max = 72) String password) {}

  @GetMapping({"/status", "/jobs"})
  public Object status() {
    return service.status();
  }

  @PostMapping("/maintenance")
  public Object maintenance(@Valid @RequestBody MaintenanceRequest r, Principal p) {
    service.maintenance(r.enabled(), r.message(), p.getName(), r.password());
    return Map.of("message", "Đã cập nhật chế độ bảo trì");
  }

  @PostMapping("/jobs")
  public ResponseEntity<?> start(@Valid @RequestBody JobRequest r, Principal p) {
    return ResponseEntity.accepted()
        .body(service.start(r.kind(), r.backupId(), r.confirmation(), p.getName(), r.password()));
  }
}
