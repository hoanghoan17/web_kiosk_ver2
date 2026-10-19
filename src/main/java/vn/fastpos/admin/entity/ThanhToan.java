package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
public class ThanhToan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaThanhToan", nullable = false)
  private Long maThanhToan;

  public Long getMaThanhToan() {
    return maThanhToan;
  }

  public void setMaThanhToan(Long value) {
    this.maThanhToan = value;
  }

  @Column(name = "MaDonHang", nullable = false)
  private Long maDonHang;

  public Long getMaDonHang() {
    return maDonHang;
  }

  public void setMaDonHang(Long value) {
    this.maDonHang = value;
  }

  @Column(name = "PhuongThucThanhToan", nullable = false, length = 30)
  private String phuongThucThanhToan;

  public String getPhuongThucThanhToan() {
    return phuongThucThanhToan;
  }

  public void setPhuongThucThanhToan(String value) {
    this.phuongThucThanhToan = value;
  }

  @Column(name = "SoTien", nullable = false, precision = 18, scale = 2)
  private BigDecimal soTien;

  public BigDecimal getSoTien() {
    return soTien;
  }

  public void setSoTien(BigDecimal value) {
    this.soTien = value;
  }

  @Column(name = "MaGiaoDich", length = 150)
  private String maGiaoDich;

  public String getMaGiaoDich() {
    return maGiaoDich;
  }

  public void setMaGiaoDich(String value) {
    this.maGiaoDich = value;
  }

  @Column(name = "ThoiGianThanhToan", nullable = false)
  private LocalDateTime thoiGianThanhToan;

  public LocalDateTime getThoiGianThanhToan() {
    return thoiGianThanhToan;
  }

  public void setThoiGianThanhToan(LocalDateTime value) {
    this.thoiGianThanhToan = value;
  }

  @Column(name = "TrangThai", nullable = false, length = 30)
  private String trangThai;

  public String getTrangThai() {
    return trangThai;
  }

  public void setTrangThai(String value) {
    this.trangThai = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaDonHang", insertable = false, updatable = false)
  private DonHang donHang;

  public DonHang getDonHang() {
    return donHang;
  }
}
