package vn.fastpos.admin.controller.Admin;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import vn.fastpos.admin.dto.Admin.CatalogRequests.TrangThaiRequest;
import vn.fastpos.admin.service.Admin.DonHangService;

@RestController
@RequestMapping("/api/admin")
public class DonHangController {
  private final DonHangService service;

  public DonHangController(DonHangService service) {
    this.service = service;
  }

  @GetMapping("/orders")
  public Object list() {
    return service.danhSach();
  }

  @GetMapping("/kitchen")
  public Object kitchen() {
    return service.bep();
  }

  @GetMapping("/orders/{id}")
  public Object detail(@PathVariable long id) {
    return service.chiTiet(id);
  }

  @PostMapping("/orders/{id}/status")
  public Object status(@PathVariable long id, @Valid @RequestBody TrangThaiRequest request) {
    service.chuyenTrangThai(id, request);
    return Map.of("message", "Đã cập nhật trạng thái");
  }
}
