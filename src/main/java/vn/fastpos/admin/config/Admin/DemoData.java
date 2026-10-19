package vn.fastpos.admin.config.Admin;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DemoData implements CommandLineRunner {
  private final JdbcTemplate db;
  private final PasswordEncoder passwords;

  @Value("${fastpos.demo:false}")
  boolean demo;

  @Value("${FASTPOS_BOOTSTRAP_PASSWORD:}")
  String bootstrap;

  public DemoData(JdbcTemplate db, PasswordEncoder passwords) {
    this.db = db;
    this.passwords = passwords;
  }

  @Transactional
  public void run(String... args) throws Exception {
    if (!demo) {
      if (!bootstrap.isBlank()) {
        if (bootstrap.length() < 12
            || bootstrap.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
          throw new IllegalArgumentException(
              "Bootstrap password requires 12 characters, max 72 bytes");
        db.update(
            "UPDATE TaiKhoan SET MatKhauHash=? WHERE TenDangNhap='admin' AND MatKhauHash LIKE"
                + " '%HASHADMIN000%'",
            passwords.encode(bootstrap));
      }
      return;
    }
    if (db.queryForObject("SELECT COUNT(*) FROM VaiTro", Integer.class) > 0) return;
    try (var c = db.getDataSource().getConnection()) {
      ScriptUtils.executeSqlScript(c, new ClassPathResource("data-demo.sql"));
    }
    for (String[] user :
        new String[][] {
          {"admin", "1", "Quản trị viên"},
          {"nv001", "2", "Nhân viên thu ngân"},
          {"bep001", "3", "Nhân viên bếp"}
        })
      db.update(
          "INSERT INTO TaiKhoan(MaVaiTro,TenDangNhap,MatKhauHash,HoTen) VALUES (?,?,?,?)",
          Integer.parseInt(user[1]),
          user[0],
          passwords.encode("Admin@123"),
          user[2]);
    for (String[] pair :
        new String[][] {
          {"VaiTro", "MaVaiTro"},
          {"DanhMucMonAn", "MaDanhMuc"},
          {"MonAn", "MaMonAn"},
          {"Combo", "MaCombo"},
          {"KhuyenMai", "MaKhuyenMai"},
          {"NguyenLieu", "MaNguyenLieu"},
          {"ThuocTinh", "MaThuocTinh"}
        }) {
      long max = db.queryForObject("SELECT MAX(" + pair[1] + ") FROM " + pair[0], Long.class);
      db.execute(
          "ALTER TABLE " + pair[0] + " ALTER COLUMN " + pair[1] + " RESTART WITH " + (max + 1));
    }
    db.update(
        "UPDATE KhuyenMai SET TenKhuyenMai='Giảm 20% theo mùa',MoTa='Giảm theo điều kiện giá trị"
            + " đơn, không giới hạn giờ' WHERE MaCode='LUNCH20'");
    db.update(
        "UPDATE KhuyenMai SET TenKhuyenMai='Giảm 30k đơn từ 200k',MoTa='Không phân biệt khách mới'"
            + " WHERE MaCode='NEWCUST'");
    var now = LocalDateTime.now();
    // Demo only: dates relative to first run, with paid / unpaid / failed / cancelled cases.
    db.update(
        "UPDATE KhuyenMai SET NgayBatDau=?,NgayKetThuc=? WHERE MaKhuyenMai=1",
        now.minusDays(1),
        now.plusDays(30));
    String[] statuses = {
      "CHO_CHE_BIEN",
      "DANG_CHE_BIEN",
      "HOAN_THANH",
      "DA_GIAO",
      "CHO_THANH_TOAN",
      "CHO_THANH_TOAN",
      "DA_HUY",
      "DA_THANH_TOAN"
    };
    for (int i = 0; i < statuses.length; i++) {
      long id = i + 1;
      boolean paid = i < 4 || i == 7;
      var time = now.minusMinutes((i + 1) * 2L);
      db.update(
          "INSERT INTO"
              + " DonHang(MaDonHang,NguonDon,TrangThaiDon,ThoiGianDat,ThoiGianThanhToan,TongTien,TienGiam,ThanhTien,GhiChu)"
              + " VALUES (?,?,?,?,?,?,?,?,?)",
          id,
          i % 2 == 0 ? "KIOSK" : "COUNTER",
          statuses[i],
          time,
          paid ? time : null,
          87000,
          0,
          87000,
          i == 2 ? "Ít đá, mang đi" : "Ăn tại chỗ");
      db.update(
          "INSERT INTO ChiTietDonHang(MaDonHang,MaMonAn,SoLuong,DonGia,ThanhTien) VALUES"
              + " (?,?,?,?,?)",
          id,
          1,
          1,
          65000,
          65000);
      db.update(
          "INSERT INTO ChiTietDonHang(MaDonHang,MaMonAn,SoLuong,DonGia,ThanhTien) VALUES"
              + " (?,?,?,?,?)",
          id,
          24,
          1,
          22000,
          22000);
      db.update(
          "INSERT INTO"
              + " ThanhToan(MaDonHang,PhuongThucThanhToan,SoTien,MaGiaoDich,ThoiGianThanhToan,TrangThai)"
              + " VALUES (?,?,?,?,?,?)",
          id,
          i % 2 == 0 ? "VIETQR" : "CARD",
          87000,
          "DEMO-" + id,
          time,
          paid ? "THANH_CONG" : i == 5 ? "THAT_BAI" : "CHO_THANH_TOAN");
    }
    db.execute("ALTER TABLE DonHang ALTER COLUMN MaDonHang RESTART WITH 9");
    db.update(
        "INSERT INTO NhatKyAdmin(TenDangNhap,HanhDong,DoiTuong,NoiDung) VALUES"
            + " ('SYSTEM','DEMO','Database','Khởi tạo dữ liệu minh họa; không phải giao dịch"
            + " thực')");
  }
}
