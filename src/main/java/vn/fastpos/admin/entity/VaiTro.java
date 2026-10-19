package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "VaiTro")
public class VaiTro {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaVaiTro", nullable = false)
  private Integer maVaiTro;

  public Integer getMaVaiTro() {
    return maVaiTro;
  }

  public void setMaVaiTro(Integer value) {
    this.maVaiTro = value;
  }

  @Nationalized
  @Column(name = "TenVaiTro", nullable = false, length = 50)
  private String tenVaiTro;

  public String getTenVaiTro() {
    return tenVaiTro;
  }

  public void setTenVaiTro(String value) {
    this.tenVaiTro = value;
  }

  @Nationalized
  @Column(name = "MoTa", length = 255)
  private String moTa;

  public String getMoTa() {
    return moTa;
  }

  public void setMoTa(String value) {
    this.moTa = value;
  }
}
