package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietDonHangCombo")
public class ChiTietDonHangCombo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaChiTietDonHangCombo", nullable = false)
  private Long maChiTietDonHangCombo;

  public Long getMaChiTietDonHangCombo() {
    return maChiTietDonHangCombo;
  }

  public void setMaChiTietDonHangCombo(Long value) {
    this.maChiTietDonHangCombo = value;
  }

  @Column(name = "MaDonHang", nullable = false)
  private Long maDonHang;

  public Long getMaDonHang() {
    return maDonHang;
  }

  public void setMaDonHang(Long value) {
    this.maDonHang = value;
  }

  @Column(name = "MaCombo", nullable = false)
  private Integer maCombo;

  public Integer getMaCombo() {
    return maCombo;
  }

  public void setMaCombo(Integer value) {
    this.maCombo = value;
  }

  @Column(name = "SoLuong", nullable = false)
  private Integer soLuong;

  public Integer getSoLuong() {
    return soLuong;
  }

  public void setSoLuong(Integer value) {
    this.soLuong = value;
  }

  @Column(name = "DonGia", nullable = false, precision = 18, scale = 2)
  private BigDecimal donGia;

  public BigDecimal getDonGia() {
    return donGia;
  }

  public void setDonGia(BigDecimal value) {
    this.donGia = value;
  }

  @Column(name = "ThanhTien", nullable = false, precision = 18, scale = 2)
  private BigDecimal thanhTien;

  public BigDecimal getThanhTien() {
    return thanhTien;
  }

  public void setThanhTien(BigDecimal value) {
    this.thanhTien = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaDonHang", insertable = false, updatable = false)
  private DonHang donHang;

  public DonHang getDonHang() {
    return donHang;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaCombo", insertable = false, updatable = false)
  private Combo combo;

  public Combo getCombo() {
    return combo;
  }
}
