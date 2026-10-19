package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "NguyenLieu")
public class NguyenLieu {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaNguyenLieu", nullable = false)
  private Integer maNguyenLieu;

  public Integer getMaNguyenLieu() {
    return maNguyenLieu;
  }

  public void setMaNguyenLieu(Integer value) {
    this.maNguyenLieu = value;
  }

  @Column(name = "MaCode", nullable = false, length = 30)
  private String maCode;

  public String getMaCode() {
    return maCode;
  }

  public void setMaCode(String value) {
    this.maCode = value;
  }

  @Nationalized
  @Column(name = "TenNguyenLieu", nullable = false, length = 150)
  private String tenNguyenLieu;

  public String getTenNguyenLieu() {
    return tenNguyenLieu;
  }

  public void setTenNguyenLieu(String value) {
    this.tenNguyenLieu = value;
  }

  @Nationalized
  @Column(name = "LoaiNguyenLieu", nullable = false, length = 50)
  private String loaiNguyenLieu;

  public String getLoaiNguyenLieu() {
    return loaiNguyenLieu;
  }

  public void setLoaiNguyenLieu(String value) {
    this.loaiNguyenLieu = value;
  }

  @Nationalized
  @Column(name = "DonViTinh", nullable = false, length = 20)
  private String donViTinh;

  public String getDonViTinh() {
    return donViTinh;
  }

  public void setDonViTinh(String value) {
    this.donViTinh = value;
  }

  @Column(name = "GiaNhap", nullable = false, precision = 18, scale = 2)
  private BigDecimal giaNhap = BigDecimal.ZERO;

  public BigDecimal getGiaNhap() {
    return giaNhap;
  }

  public void setGiaNhap(BigDecimal value) {
    this.giaNhap = value;
  }

  @Column(name = "TonKhoHienTai", nullable = false, precision = 18, scale = 3)
  private BigDecimal tonKhoHienTai = BigDecimal.ZERO;

  public BigDecimal getTonKhoHienTai() {
    return tonKhoHienTai;
  }

  public void setTonKhoHienTai(BigDecimal value) {
    this.tonKhoHienTai = value;
  }

  @Column(name = "NguongCanhBao", nullable = false, precision = 18, scale = 3)
  private BigDecimal nguongCanhBao = BigDecimal.ZERO;

  public BigDecimal getNguongCanhBao() {
    return nguongCanhBao;
  }

  public void setNguongCanhBao(BigDecimal value) {
    this.nguongCanhBao = value;
  }

  @Nationalized
  @Column(name = "DiUng", length = 150)
  private String diUng;

  public String getDiUng() {
    return diUng;
  }

  public void setDiUng(String value) {
    this.diUng = value;
  }

  @Column(name = "TrangThai", nullable = false)
  private Boolean trangThai = true;

  public Boolean getTrangThai() {
    return trangThai;
  }

  public void setTrangThai(Boolean value) {
    this.trangThai = value;
  }

  @Column(name = "NgayTao", nullable = false)
  private LocalDateTime ngayTao = LocalDateTime.now();

  public LocalDateTime getNgayTao() {
    return ngayTao;
  }

  public void setNgayTao(LocalDateTime value) {
    this.ngayTao = value;
  }

  @Column(name = "NgayCapNhat")
  private LocalDateTime ngayCapNhat;

  public LocalDateTime getNgayCapNhat() {
    return ngayCapNhat;
  }

  public void setNgayCapNhat(LocalDateTime value) {
    this.ngayCapNhat = value;
  }
}
