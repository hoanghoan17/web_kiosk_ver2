package vn.fastpos.admin.service.Admin;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fastpos.admin.dto.Admin.ViewMapper;
import vn.fastpos.admin.repository.Admin.*;

@Service
@Transactional(readOnly = true)
public class BaoCaoService {
  private final BaoCaoRepository report;
  private final DonHangRepository orders;
  private final MonAnRepository dishes;
  private final DanhMucMonAnRepository categories;
  private final NguyenLieuRepository stock;

  public BaoCaoService(
      BaoCaoRepository r,
      DonHangRepository o,
      MonAnRepository m,
      DanhMucMonAnRepository c,
      NguyenLieuRepository s) {
    report = r;
    orders = o;
    dishes = m;
    categories = c;
    stock = s;
  }

  public Map<String, Object> baoCao(LocalDate from, LocalDate to) {
    return report.aggregate(from, to);
  }

  public Object tongQuan(LocalDate date) {
    var out = new LinkedHashMap<>(baoCao(date, date));
    out.put(
        "queue",
        orders.countPaidByStates(List.of("DA_THANH_TOAN", "CHO_CHE_BIEN", "DANG_CHE_BIEN")));
    out.put("ready", orders.countPaidByStates(List.of("HOAN_THANH")));
    out.put(
        "completed",
        orders.countCompletedInDay(date.atStartOfDay(), date.plusDays(1).atStartOfDay()));
    out.put(
        "activeDishes",
        dishes.findAll().stream()
            .filter(
                m ->
                    Boolean.TRUE.equals(m.getTrangThai())
                        && Boolean.TRUE.equals(m.getConHang())
                        && categories
                            .findById(m.getMaDanhMuc())
                            .map(c -> Boolean.TRUE.equals(c.getTrangThai()))
                            .orElse(false))
            .count());
    out.put("categoryCount", categories.count());
    out.put("hourly", report.hourly(date));
    out.put(
        "stockAlerts",
        stock.findAll().stream()
            .filter(
                n ->
                    Boolean.TRUE.equals(n.getTrangThai())
                        && n.getTonKhoHienTai().compareTo(n.getNguongCanhBao()) <= 0)
            .limit(6)
            .map(ViewMapper::of)
            .toList());
    return out;
  }
}
