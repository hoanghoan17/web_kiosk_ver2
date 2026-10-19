package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaTaiKhoan", nullable = false)
  private Integer maTaiKhoan;

  public Integer getMaTaiKhoan() {
    return maTaiKhoan;
  }

  public void setMaTaiKhoan(Integer value) {
    this.maTaiKhoan = value;
  }

  @Column(name = "MaVaiTro", nullable = false)
  private Integer maVaiTro;

  public Integer getMaVaiTro() {
    return maVaiTro;
  }

  public void setMaVaiTro(Integer value) {
    this.maVaiTro = value;
  }

  @Column(name = "TenDangNhap", nullable = false, length = 100)
  private String tenDangNhap;

  public String getTenDangNhap() {
    return tenDangNhap;
  }

  public void setTenDangNhap(String value) {
    this.tenDangNhap = value;
  }

  @Column(name = "MatKhauHash", nullable = false, length = 255)
  private String matKhauHash;

  public String getMatKhauHash() {
    return matKhauHash;
  }

  public void setMatKhauHash(String value) {
    this.matKhauHash = value;
  }

  @Nationalized
  @Column(name = "HoTen", nullable = false, length = 100)
  private String hoTen;

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String value) {
    this.hoTen = value;
  }

  @Column(name = "Email", length = 150)
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  @Column(name = "SoDienThoai", length = 20)
  private String soDienThoai;

  public String getSoDienThoai() {
    return soDienThoai;
  }

  public void setSoDienThoai(String value) {
    this.soDienThoai = value;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaVaiTro", insertable = false, updatable = false)
  private VaiTro vaiTro;

  public VaiTro getVaiTro() {
    return vaiTro;
  }
}
