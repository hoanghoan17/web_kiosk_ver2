package vn.fastpos.admin.service.Admin;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fastpos.admin.config.Admin.Realtime;
import vn.fastpos.admin.dto.Admin.CatalogRequests.*;
import vn.fastpos.admin.dto.Admin.ViewMapper;
import vn.fastpos.admin.entity.*;
import vn.fastpos.admin.exception.Admin.ApiException;
import vn.fastpos.admin.repository.Admin.*;

@Service
@Transactional(readOnly = true)
public class ThucDonService {
  private final DanhMucMonAnRepository danhMuc;
  private final MonAnRepository monAn;
  private final ComboRepository combo;
  private final ChiTietComboRepository chiTiet;
  private final KhuyenMaiRepository khuyenMai;
  private final NhatKyService log;
  private final Realtime.Updates updates;

  public ThucDonService(
      DanhMucMonAnRepository d,
      MonAnRepository m,
      ComboRepository c,
      ChiTietComboRepository ct,
      KhuyenMaiRepository k,
      NhatKyService log,
      Realtime.Updates updates) {
    danhMuc = d;
    monAn = m;
    combo = c;
    chiTiet = ct;
    khuyenMai = k;
    this.log = log;
    this.updates = updates;
  }

  private <T> T tim(
      org.springframework.data.jpa.repository.JpaRepository<T, Integer> repo, Integer id) {
    return repo.findById(id)
        .orElseThrow(
            () ->
                new ApiException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi"));
  }

  private void anh(String url) {
    if (url != null && !url.isBlank() && !url.matches("https://[^\\s]+"))
      throw ApiException.bad("Hình ảnh phải là URL HTTPS");
  }

  private boolean banDuoc(MonAn m) {
    return Boolean.TRUE.equals(m.getTrangThai())
        && Boolean.TRUE.equals(m.getConHang())
        && Boolean.TRUE.equals(tim(danhMuc, m.getMaDanhMuc()).getTrangThai());
  }

  public Object danhMuc() {
    return danhMuc
        .findAll(org.springframework.data.domain.Sort.by("maDanhMuc").descending())
        .stream()
        .map(ViewMapper::of)
        .toList();
  }

  public Object monAn() {
    return monAn.findAll(org.springframework.data.domain.Sort.by("maMonAn").descending()).stream()
        .map(
            m -> {
              var out = ViewMapper.of(m);
              out.put("CoTheBan", banDuoc(m));
              return out;
            })
        .toList();
  }

  public Object combos() {
    return combo.findAll(org.springframework.data.domain.Sort.by("maCombo").descending()).stream()
        .map(
            c -> {
              var out = ViewMapper.of(c);
              var items = chiTiet.findByIdMaCombo(c.getMaCombo());
              out.put(
                  "items",
                  items.stream()
                      .map(
                          i -> {
                            var m = tim(monAn, i.getId().getMaMonAn());
                            var line = ViewMapper.of(i);
                            line.put("TenMon", m.getTenMon());
                            line.put("TrangThai", m.getTrangThai());
                            return line;
                          })
                      .toList());
              out.put(
                  "CoTheBan",
                  Boolean.TRUE.equals(c.getTrangThai())
                      && !items.isEmpty()
                      && items.stream().allMatch(i -> banDuoc(tim(monAn, i.getId().getMaMonAn()))));
              return out;
            })
        .toList();
  }

  public Object khuyenMai() {
    return khuyenMai
        .findAll(org.springframework.data.domain.Sort.by("maKhuyenMai").descending())
        .stream()
        .map(ViewMapper::of)
        .toList();
  }

  @Transactional
  public Object luuDanhMuc(Integer id, DanhMucRequest r) {
    var e = id == null ? new DanhMucMonAn() : tim(danhMuc, id);
    e.setTenDanhMuc(r.tenDanhMuc().trim());
    e.setMoTa(r.moTa());
    e.setTrangThai(r.trangThai());
    danhMuc.saveAndFlush(e);
    doi("categories", e.getMaDanhMuc());
    return Map.of("id", e.getMaDanhMuc());
  }

  @Transactional
  public Object luuMonAn(Integer id, MonAnRequest r) {
    tim(danhMuc, r.maDanhMuc());
    anh(r.hinhAnh());
    var e = id == null ? new MonAn() : tim(monAn, id);
    e.setMaDanhMuc(r.maDanhMuc());
    e.setTenMon(r.tenMon().trim());
    e.setMoTa(r.moTa());
    e.setGiaBan(r.giaBan());
    e.setHinhAnh(r.hinhAnh());
    e.setTrangThai(r.trangThai());
    e.setConHang(r.conHang());
    e.setNgayCapNhat(LocalDateTime.now());
    monAn.saveAndFlush(e);
    doi("dishes", e.getMaMonAn());
    return Map.of("id", e.getMaMonAn());
  }

  @Transactional
  public Object luuCombo(Integer id, ComboRequest r) {
    anh(r.hinhAnh());
    Set<Integer> seen = new HashSet<>();
    for (var item : r.items()) {
      tim(monAn, item.maMonAn());
      if (!seen.add(item.maMonAn())) throw ApiException.bad("Món thành phần không được trùng");
    }
    var e = id == null ? new Combo() : tim(combo, id);
    e.setTenCombo(r.tenCombo().trim());
    e.setMoTa(r.moTa());
    e.setGiaBan(r.giaBan());
    e.setHinhAnh(r.hinhAnh());
    e.setTrangThai(r.trangThai());
    e.setNgayCapNhat(LocalDateTime.now());
    combo.saveAndFlush(e);
    chiTiet.deleteByIdMaCombo(e.getMaCombo());
    chiTiet.flush();
    for (var item : r.items()) {
      var ct = new ChiTietCombo();
      ct.setId(new ChiTietComboId(e.getMaCombo(), item.maMonAn()));
      ct.setSoLuong(item.soLuong());
      chiTiet.save(ct);
    }
    doi("combos", e.getMaCombo());
    return Map.of("id", e.getMaCombo());
  }

  @Transactional
  public Object luuKhuyenMai(Integer id, KhuyenMaiRequest r) {
    if (r.ngayKetThuc().isBefore(r.ngayBatDau()))
      throw ApiException.bad("Ngày kết thúc phải từ ngày bắt đầu trở đi");
    if (r.loaiGiam().equals("PHAN_TRAM")
        && r.giaTriGiam().compareTo(java.math.BigDecimal.valueOf(100)) > 0)
      throw ApiException.bad("Phần trăm không vượt quá 100");
    var e = id == null ? new KhuyenMai() : tim(khuyenMai, id);
    e.setMaCode(r.maCode().toUpperCase(Locale.ROOT));
    e.setTenKhuyenMai(r.tenKhuyenMai().trim());
    e.setMoTa(r.moTa());
    e.setLoaiGiam(r.loaiGiam());
    e.setGiaTriGiam(r.giaTriGiam());
    e.setGiaTriDonToiThieu(r.giaTriDonToiThieu());
    e.setGiamToiDa(r.giamToiDa());
    e.setNgayBatDau(r.ngayBatDau());
    e.setNgayKetThuc(r.ngayKetThuc());
    e.setTrangThai(r.trangThai());
    khuyenMai.saveAndFlush(e);
    doi("promotions", e.getMaKhuyenMai());
    return Map.of("id", e.getMaKhuyenMai());
  }

  @Transactional
  public void ngung(String type, Integer id) {
    switch (type) {
      case "categories" -> tim(danhMuc, id).setTrangThai(false);
      case "dishes" -> tim(monAn, id).setTrangThai(false);
      case "combos" -> tim(combo, id).setTrangThai(false);
      case "promotions" -> tim(khuyenMai, id).setTrangThai(false);
      default -> throw ApiException.bad("Không có tài nguyên");
    }
    doi(type, id);
  }

  private void doi(String type, Integer id) {
    log.ghi("CAP_NHAT", type + "#" + id, "Cập nhật thực đơn/trạng thái; giữ lịch sử");
    updates.changed();
  }
}
