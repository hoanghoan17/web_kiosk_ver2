package vn.fastpos.admin.repository.Admin;

import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.fastpos.admin.exception.Admin.ApiException;

@Repository
public class BaoCaoRepository {
  private final JdbcTemplate db;
  private final jakarta.persistence.EntityManager em;

  public BaoCaoRepository(JdbcTemplate db, jakarta.persistence.EntityManager em) {
    this.db = db;
    this.em = em;
  }

  public Map<String, Object> aggregate(LocalDate from, LocalDate to) {
    if (from == null || to == null || to.isBefore(from) || to.isAfter(from.plusDays(365)))
      throw ApiException.bad(
          "Khoảng báo cáo tối đa 366 ngày, ngày kết thúc từ ngày bắt đầu trở đi");
    var start = from.atStartOfDay();
    var end = to.plusDays(1).atStartOfDay();
    String filter =
        " FROM ThanhToan t JOIN DonHang d ON d.MaDonHang=t.MaDonHang WHERE t.TrangThai='THANH_CONG'"
            + " AND d.TrangThaiDon<>'DA_HUY' AND t.ThoiGianThanhToan>=? AND t.ThoiGianThanhToan<?";
    var summary =
        db.queryForMap(
            "SELECT COUNT(*) AS SoDon,COALESCE(SUM(t.SoTien),0) AS"
                + " DoanhThu,COALESCE(SUM(d.TienGiam),0) AS TienGiam"
                + filter,
            start,
            end);
    var daily =
        db.queryForList(
            "SELECT CAST(t.ThoiGianThanhToan AS DATE) AS Ngay,COUNT(*) AS SoDon, SUM(t.SoTien) AS"
                + " DoanhThu"
                + filter
                + " GROUP BY CAST(t.ThoiGianThanhToan AS DATE) ORDER BY Ngay",
            start,
            end);
    var sources =
        db.queryForList(
            "SELECT d.NguonDon,COUNT(*) AS SoDon,SUM(t.SoTien) AS DoanhThu"
                + filter
                + " GROUP BY d.NguonDon",
            start,
            end);
    String success =
        " JOIN ThanhToan t ON t.MaDonHang=c.MaDonHang JOIN DonHang d ON d.MaDonHang=c.MaDonHang"
            + " WHERE t.TrangThai='THANH_CONG' AND d.TrangThaiDon<>'DA_HUY' AND"
            + " t.ThoiGianThanhToan>=? AND t.ThoiGianThanhToan<?";
    var top =
        db.queryForList(
            "SELECT TOP 10 * FROM (SELECT 'MON' AS Loai,m.MaMonAn AS MaSanPham,m.TenMon AS"
                + " TenSanPham,SUM(c.SoLuong) AS SoLuong,SUM(c.ThanhTien) AS TienTruocGiam FROM"
                + " ChiTietDonHang c JOIN MonAn m ON m.MaMonAn=c.MaMonAn"
                + success
                + " GROUP BY m.MaMonAn,m.TenMon UNION ALL SELECT"
                + " 'COMBO',b.MaCombo,b.TenCombo,SUM(c.SoLuong),SUM(c.ThanhTien) FROM"
                + " ChiTietDonHangCombo c JOIN Combo b ON b.MaCombo=c.MaCombo"
                + success
                + " GROUP BY b.MaCombo,b.TenCombo) x ORDER BY SoLuong DESC,Loai,MaSanPham",
            start,
            end,
            start,
            end);
    return Map.of("summary", summary, "daily", daily, "sources", sources, "top", top);
  }

  public List<Map<String, Object>> hourly(LocalDate date) {
    var rows =
        em.createQuery(
                "select hour(t.thoiGianThanhToan),sum(t.soTien) from ThanhToan t, DonHang d where"
                    + " d.maDonHang=t.maDonHang and t.trangThai='THANH_CONG' and"
                    + " d.trangThaiDon<>'DA_HUY' and t.thoiGianThanhToan>=:start and"
                    + " t.thoiGianThanhToan<:end group by hour(t.thoiGianThanhToan) order by"
                    + " hour(t.thoiGianThanhToan)",
                Object[].class)
            .setParameter("start", date.atStartOfDay())
            .setParameter("end", date.plusDays(1).atStartOfDay())
            .getResultList();
    return rows.stream()
        .map(
            r -> {
              Map<String, Object> item = new java.util.LinkedHashMap<>();
              item.put("Gio", r[0]);
              item.put("DoanhThu", r[1]);
              return item;
            })
        .toList();
  }
}
