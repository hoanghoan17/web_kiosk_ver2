package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "DonHang")
public class DonHang {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaDonHang", nullable = false)
  private Long maDonHang;

  public Long getMaDonHang() {
    return maDonHang;
  }

  public void setMaDonHang(Long value) {
    this.maDonHang = value;
  }

  @Column(name = "MaKhachHang")
  private Integer maKhachHang;

  public Integer getMaKhachHang() {
    return maKhachHang;
  }

  public void setMaKhachHang(Integer value) {
    this.maKhachHang = value;
  }

  @Column(name = "MaPhienKiosk")
  private Long maPhienKiosk;

  public Long getMaPhienKiosk() {
    return maPhienKiosk;
  }

  public void setMaPhienKiosk(Long value) {
    this.maPhienKiosk = value;
  }

  @Column(name = "MaTaiKhoan")
  private Integer maTaiKhoan;

  public Integer getMaTaiKhoan() {
    return maTaiKhoan;
  }

  public void setMaTaiKhoan(Integer value) {
    this.maTaiKhoan = value;
  }

  @Column(name = "MaKhuyenMai")
  private Integer maKhuyenMai;

  public Integer getMaKhuyenMai() {
    return maKhuyenMai;
  }

  public void setMaKhuyenMai(Integer value) {
    this.maKhuyenMai = value;
  }

  @Column(name = "NguonDon", nullable = false, length = 20)
  private String nguonDon;

  public String getNguonDon() {
    return nguonDon;
  }

  public void setNguonDon(String value) {
    this.nguonDon = value;
  }

  @Column(name = "TrangThaiDon", nullable = false, length = 30)
  private String trangThaiDon;

  public String getTrangThaiDon() {
    return trangThaiDon;
  }

  public void setTrangThaiDon(String value) {
    this.trangThaiDon = value;
  }

  @Column(name = "ThoiGianDat", nullable = false)
  private LocalDateTime thoiGianDat = LocalDateTime.now();

  public LocalDateTime getThoiGianDat() {
    return thoiGianDat;
  }

  public void setThoiGianDat(LocalDateTime value) {
    this.thoiGianDat = value;
  }

  @Column(name = "ThoiGianThanhToan")
  private LocalDateTime thoiGianThanhToan;

  public LocalDateTime getThoiGianThanhToan() {
    return thoiGianThanhToan;
  }

  public void setThoiGianThanhToan(LocalDateTime value) {
    this.thoiGianThanhToan = value;
  }

  @Column(name = "ThoiGianHoanThanh")
  private LocalDateTime thoiGianHoanThanh;

  public LocalDateTime getThoiGianHoanThanh() {
    return thoiGianHoanThanh;
  }

  public void setThoiGianHoanThanh(LocalDateTime value) {
    this.thoiGianHoanThanh = value;
  }

  @Column(name = "TongTien", nullable = false, precision = 18, scale = 2)
  private BigDecimal tongTien = BigDecimal.ZERO;

  public BigDecimal getTongTien() {
    return tongTien;
  }

  public void setTongTien(BigDecimal value) {
    this.tongTien = value;
  }

  @Column(name = "TienGiam", nullable = false, precision = 18, scale = 2)
  private BigDecimal tienGiam = BigDecimal.ZERO;

  public BigDecimal getTienGiam() {
    return tienGiam;
  }

  public void setTienGiam(BigDecimal value) {
    this.tienGiam = value;
  }

  @Column(name = "ThanhTien", nullable = false, precision = 18, scale = 2)
  private BigDecimal thanhTien = BigDecimal.ZERO;

  public BigDecimal getThanhTien() {
    return thanhTien;
  }

  public void setThanhTien(BigDecimal value) {
    this.thanhTien = value;
  }

  @Nationalized
  @Column(name = "GhiChu", length = 500)
  private String ghiChu;

  public String getGhiChu() {
    return ghiChu;
  }

  public void setGhiChu(String value) {
    this.ghiChu = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaKhachHang", insertable = false, updatable = false)
  private KhachHang khachHang;

  public KhachHang getKhachHang() {
    return khachHang;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaPhienKiosk", insertable = false, updatable = false)
  private PhienDangNhapKiosk phienDangNhapKiosk;

  public PhienDangNhapKiosk getPhienDangNhapKiosk() {
    return phienDangNhapKiosk;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaTaiKhoan", insertable = false, updatable = false)
  private TaiKhoan taiKhoan;

  public TaiKhoan getTaiKhoan() {
    return taiKhoan;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaKhuyenMai", insertable = false, updatable = false)
  private KhuyenMai khuyenMai;

  public KhuyenMai getKhuyenMai() {
    return khuyenMai;
  }
}
