package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhienDangNhapKiosk")
public class PhienDangNhapKiosk {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaPhien", nullable = false)
  private Long maPhien;

  public Long getMaPhien() {
    return maPhien;
  }

  public void setMaPhien(Long value) {
    this.maPhien = value;
  }

  @Column(name = "MaKhachHang")
  private Integer maKhachHang;

  public Integer getMaKhachHang() {
    return maKhachHang;
  }

  public void setMaKhachHang(Integer value) {
    this.maKhachHang = value;
  }

  @Column(name = "MaKiosk", nullable = false, length = 50)
  private String maKiosk;

  public String getMaKiosk() {
    return maKiosk;
  }

  public void setMaKiosk(String value) {
    this.maKiosk = value;
  }

  @Column(name = "ThoiGianBatDau", nullable = false)
  private LocalDateTime thoiGianBatDau = LocalDateTime.now();

  public LocalDateTime getThoiGianBatDau() {
    return thoiGianBatDau;
  }

  public void setThoiGianBatDau(LocalDateTime value) {
    this.thoiGianBatDau = value;
  }

  @Column(name = "ThoiGianKetThuc")
  private LocalDateTime thoiGianKetThuc;

  public LocalDateTime getThoiGianKetThuc() {
    return thoiGianKetThuc;
  }

  public void setThoiGianKetThuc(LocalDateTime value) {
    this.thoiGianKetThuc = value;
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
  @JoinColumn(name = "MaKhachHang", insertable = false, updatable = false)
  private KhachHang khachHang;

  public KhachHang getKhachHang() {
    return khachHang;
  }
}
