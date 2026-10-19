package vn.fastpos.admin.controller.Admin;

import java.time.LocalDate;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vn.fastpos.admin.service.Admin.BaoCaoService;
import vn.fastpos.admin.service.Admin.NhatKyService;

@RestController
@RequestMapping("/api/admin")
public class BaoCaoController {
  private final BaoCaoService service;
  private final NhatKyService log;

  public BaoCaoController(BaoCaoService service, NhatKyService log) {
    this.service = service;
    this.log = log;
  }

  @GetMapping("/dashboard")
  public Object dashboard(@RequestParam(required = false) LocalDate date) {
    return service.tongQuan(
        date == null ? LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")) : date);
  }

  @GetMapping("/audit")
  public Object audit() {
    return log.danhSach();
  }

  @GetMapping("/reports")
  public Object reports(@RequestParam LocalDate from, @RequestParam LocalDate to) {
    return service.baoCao(from, to);
  }

  @GetMapping(value = "/reports.csv", produces = "text/csv;charset=UTF-8")
  public ResponseEntity<String> csv(@RequestParam LocalDate from, @RequestParam LocalDate to) {
    var result = service.baoCao(from, to);
    var text = new StringBuilder("\uFEFFNgày,Số đơn đã thanh toán,Doanh thu thuần\r\n");
    for (Object x : (List<?>) result.get("daily")) {
      var r = (Map<?, ?>) x;
      text.append(r.get("Ngay"))
          .append(',')
          .append(r.get("SoDon"))
          .append(',')
          .append(r.get("DoanhThu"))
          .append("\r\n");
    }
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=fastpos-revenue.csv")
        .body(text.toString());
  }
}
