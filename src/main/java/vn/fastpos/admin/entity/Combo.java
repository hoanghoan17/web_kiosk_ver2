package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Combo")
public class Combo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaCombo", nullable = false)
  private Integer maCombo;

  public Integer getMaCombo() {
    return maCombo;
  }

  public void setMaCombo(Integer value) {
    this.maCombo = value;
  }

  @Nationalized
  @Column(name = "TenCombo", nullable = false, length = 150)
  private String tenCombo;

  public String getTenCombo() {
    return tenCombo;
  }

  public void setTenCombo(String value) {
    this.tenCombo = value;
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

  @Column(name = "GiaBan", nullable = false, precision = 18, scale = 2)
  private BigDecimal giaBan;

  public BigDecimal getGiaBan() {
    return giaBan;
  }

  public void setGiaBan(BigDecimal value) {
    this.giaBan = value;
  }

  @Column(name = "HinhAnh", length = 500)
  private String hinhAnh;

  public String getHinhAnh() {
    return hinhAnh;
  }

  public void setHinhAnh(String value) {
    this.hinhAnh = value;
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
