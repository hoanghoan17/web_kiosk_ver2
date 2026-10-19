package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaKhuyenMai", nullable = false)
  private Integer maKhuyenMai;

  public Integer getMaKhuyenMai() {
    return maKhuyenMai;
  }

  public void setMaKhuyenMai(Integer value) {
    this.maKhuyenMai = value;
  }

  @Column(name = "MaCode", nullable = false, length = 50)
  private String maCode;

  public String getMaCode() {
    return maCode;
  }

  public void setMaCode(String value) {
    this.maCode = value;
  }

  @Nationalized
  @Column(name = "TenKhuyenMai", nullable = false, length = 150)
  private String tenKhuyenMai;

  public String getTenKhuyenMai() {
    return tenKhuyenMai;
  }

  public void setTenKhuyenMai(String value) {
    this.tenKhuyenMai = value;
  }

  @Nationalized
  @Column(name = "MoTa", length = 500)
  private String moTa;

  public String getMoTa() {
    return moTa;
  }

  public void setMoTa(String value) {
    this.moTa = value;
  }

  @Column(name = "LoaiGiam", nullable = false, length = 20)
  private String loaiGiam;

  public String getLoaiGiam() {
    return loaiGiam;
  }

  public void setLoaiGiam(String value) {
    this.loaiGiam = value;
  }

  @Column(name = "GiaTriGiam", nullable = false, precision = 18, scale = 2)
  private BigDecimal giaTriGiam;

  public BigDecimal getGiaTriGiam() {
    return giaTriGiam;
  }

  public void setGiaTriGiam(BigDecimal value) {
    this.giaTriGiam = value;
  }

  @Column(name = "GiaTriDonToiThieu", precision = 18, scale = 2)
  private BigDecimal giaTriDonToiThieu;

  public BigDecimal getGiaTriDonToiThieu() {
    return giaTriDonToiThieu;
  }

  public void setGiaTriDonToiThieu(BigDecimal value) {
    this.giaTriDonToiThieu = value;
  }

  @Column(name = "GiamToiDa", precision = 18, scale = 2)
  private BigDecimal giamToiDa;

  public BigDecimal getGiamToiDa() {
    return giamToiDa;
  }

  public void setGiamToiDa(BigDecimal value) {
    this.giamToiDa = value;
  }

  @Column(name = "NgayBatDau", nullable = false)
  private LocalDateTime ngayBatDau;

  public LocalDateTime getNgayBatDau() {
    return ngayBatDau;
  }

  public void setNgayBatDau(LocalDateTime value) {
    this.ngayBatDau = value;
  }

  @Column(name = "NgayKetThuc", nullable = false)
  private LocalDateTime ngayKetThuc;

  public LocalDateTime getNgayKetThuc() {
    return ngayKetThuc;
  }

  public void setNgayKetThuc(LocalDateTime value) {
    this.ngayKetThuc = value;
  }

  @Column(name = "TrangThai", nullable = false)
  private Boolean trangThai = true;

  public Boolean getTrangThai() {
    return trangThai;
  }

  public void setTrangThai(Boolean value) {
    this.trangThai = value;
  }
}
