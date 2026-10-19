package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietDonHang")
public class ChiTietDonHang {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaChiTietDonHang", nullable = false)
  private Long maChiTietDonHang;

  public Long getMaChiTietDonHang() {
    return maChiTietDonHang;
  }

  public void setMaChiTietDonHang(Long value) {
    this.maChiTietDonHang = value;
  }

  @Column(name = "MaDonHang", nullable = false)
  private Long maDonHang;

  public Long getMaDonHang() {
    return maDonHang;
  }

  public void setMaDonHang(Long value) {
    this.maDonHang = value;
  }

  @Column(name = "MaMonAn", nullable = false)
  private Integer maMonAn;

  public Integer getMaMonAn() {
    return maMonAn;
  }

  public void setMaMonAn(Integer value) {
    this.maMonAn = value;
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
  @JoinColumn(name = "MaMonAn", insertable = false, updatable = false)
  private MonAn monAn;

  public MonAn getMonAn() {
    return monAn;
  }
}
