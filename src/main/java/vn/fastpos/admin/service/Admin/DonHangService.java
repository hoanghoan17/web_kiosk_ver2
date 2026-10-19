package vn.fastpos.admin.service.Admin;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fastpos.admin.config.Admin.Realtime;
import vn.fastpos.admin.dto.Admin.CatalogRequests.TrangThaiRequest;
import vn.fastpos.admin.dto.Admin.ViewMapper;
import vn.fastpos.admin.entity.*;
import vn.fastpos.admin.exception.Admin.ApiException;
import vn.fastpos.admin.repository.Admin.*;

@Service
@Transactional(readOnly = true)
public class DonHangService {
  private final DonHangRepository don;
  private final ThanhToanRepository payments;
  private final ChiTietDonHangRepository lines;
  private final ChiTietDonHangComboRepository combos;
  private final MonAnRepository mon;
  private final ComboRepository combo;
  private final NhatKyService log;
  private final Realtime.Updates updates;

  public DonHangService(
      DonHangRepository don,
      ThanhToanRepository payments,
      ChiTietDonHangRepository lines,
      ChiTietDonHangComboRepository combos,
      MonAnRepository mon,
      ComboRepository combo,
      NhatKyService log,
      Realtime.Updates updates) {
    this.don = don;
    this.payments = payments;
    this.lines = lines;
    this.combos = combos;
    this.mon = mon;
    this.combo = combo;
    this.log = log;
    this.updates = updates;
  }

  private Map<String, Object> view(DonHang d) {
    var out = ViewMapper.of(d);
    var p = payments.findByMaDonHang(d.getMaDonHang()).stream().findFirst();
    out.put("TrangThaiThanhToan", p.map(ThanhToan::getTrangThai).orElse(null));
    out.put("PhuongThucThanhToan", p.map(ThanhToan::getPhuongThucThanhToan).orElse(null));
    out.put("MaGiaoDich", p.map(ThanhToan::getMaGiaoDich).orElse(null));
    return out;
  }

  public Object danhSach() {
    return don.findTop500ByOrderByMaDonHangDesc().stream().map(this::view).toList();
  }

  public Object bep() {
    return don
        .findByTrangThaiDonInOrderByThoiGianThanhToanAsc(
            List.of("DA_THANH_TOAN", "CHO_CHE_BIEN", "DANG_CHE_BIEN", "HOAN_THANH"))
        .stream()
        .filter(
            d ->
                payments.findByMaDonHang(d.getMaDonHang()).stream()
                    .anyMatch(p -> p.getTrangThai().equals("THANH_CONG")))
        .map(this::view)
        .toList();
  }

  public Object chiTiet(long id) {
    var d =
        don.findById(id)
            .orElseThrow(
                () ->
                    new ApiException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Không có đơn hàng"));
    var out = view(d);
    List<Map<String, Object>> items = new ArrayList<>();
    for (var c : lines.findByMaDonHang(id)) {
      var line = ViewMapper.of(c);
      line.put("TenSanPham", mon.findById(c.getMaMonAn()).orElseThrow().getTenMon());
      line.put("Loai", "MON");
      items.add(line);
    }
    for (var c : combos.findByMaDonHang(id)) {
      var line = ViewMapper.of(c);
      line.put("TenSanPham", combo.findById(c.getMaCombo()).orElseThrow().getTenCombo());
      line.put("Loai", "COMBO");
      items.add(line);
    }
    out.put("items", items);
    return out;
  }

  @Transactional
  public void chuyenTrangThai(long id, TrangThaiRequest r) {
    var allowed =
        Map.of(
            "CHO_THANH_TOAN",
            Set.of("DA_HUY"),
            "DA_THANH_TOAN",
            Set.of("CHO_CHE_BIEN"),
            "CHO_CHE_BIEN",
            Set.of("DANG_CHE_BIEN"),
            "DANG_CHE_BIEN",
            Set.of("HOAN_THANH"),
            "HOAN_THANH",
            Set.of("DA_GIAO"));
    if (!allowed.getOrDefault(r.from(), Set.of()).contains(r.to()))
      throw ApiException.bad("Không cho phép chuyển trạng thái này");
    var e = don.lockById(id).orElseThrow(() -> ApiException.bad("Đơn không tồn tại"));
    if (!e.getTrangThaiDon().equals(r.from()))
      throw ApiException.conflict("Đơn đã thay đổi. Tải lại danh sách.");
    boolean success =
        payments.findByMaDonHang(id).stream()
            .anyMatch(p -> Set.of("THANH_CONG", "HOAN_TIEN").contains(p.getTrangThai()));
    if (r.to().equals("DA_HUY")) {
      if (r.reason() == null || r.reason().isBlank()) throw ApiException.bad("Cần nhập lý do hủy");
      if (success) throw ApiException.conflict("Đơn đã trả tiền cần quy trình hoàn tiền");
    } else if (payments.findByMaDonHang(id).stream()
        .noneMatch(p -> p.getTrangThai().equals("THANH_CONG")))
      throw ApiException.conflict("Không thể chế biến hoặc giao đơn chưa thanh toán");
    e.setTrangThaiDon(r.to());
    if (r.to().equals("HOAN_THANH")) e.setThoiGianHoanThanh(LocalDateTime.now());
    log.ghi(
        "DOI_TRANG_THAI",
        "DonHang#" + id,
        r.from() + " → " + r.to() + "; " + Objects.toString(r.reason(), ""));
    updates.changed();
  }
}
