package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "ThuocTinh")
public class ThuocTinh {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaThuocTinh", nullable = false)
  private Integer maThuocTinh;

  public Integer getMaThuocTinh() {
    return maThuocTinh;
  }

  public void setMaThuocTinh(Integer value) {
    this.maThuocTinh = value;
  }

  @Nationalized
  @Column(name = "TenThuocTinh", nullable = false, length = 100)
  private String tenThuocTinh;

  public String getTenThuocTinh() {
    return tenThuocTinh;
  }

  public void setTenThuocTinh(String value) {
    this.tenThuocTinh = value;
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
}
