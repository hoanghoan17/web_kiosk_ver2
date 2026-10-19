package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "KhachHang")
public class KhachHang {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaKhachHang", nullable = false)
  private Integer maKhachHang;

  public Integer getMaKhachHang() {
    return maKhachHang;
  }

  public void setMaKhachHang(Integer value) {
    this.maKhachHang = value;
  }

  @Nationalized
  @Column(name = "HoTen", length = 100)
  private String hoTen;

  public String getHoTen() {
    return hoTen;
  }

  public void setHoTen(String value) {
    this.hoTen = value;
  }

  @Column(name = "SoDienThoai", length = 20)
  private String soDienThoai;

  public String getSoDienThoai() {
    return soDienThoai;
  }

  public void setSoDienThoai(String value) {
    this.soDienThoai = value;
  }

  @Column(name = "Email", length = 150)
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  @Column(name = "NgayTao", nullable = false)
  private LocalDateTime ngayTao = LocalDateTime.now();

  public LocalDateTime getNgayTao() {
    return ngayTao;
  }

  public void setNgayTao(LocalDateTime value) {
    this.ngayTao = value;
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
