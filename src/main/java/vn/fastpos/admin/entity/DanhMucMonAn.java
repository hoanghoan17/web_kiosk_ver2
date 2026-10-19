package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "DanhMucMonAn")
public class DanhMucMonAn {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaDanhMuc", nullable = false)
  private Integer maDanhMuc;

  public Integer getMaDanhMuc() {
    return maDanhMuc;
  }

  public void setMaDanhMuc(Integer value) {
    this.maDanhMuc = value;
  }

  @Nationalized
  @Column(name = "TenDanhMuc", nullable = false, length = 100)
  private String tenDanhMuc;

  public String getTenDanhMuc() {
    return tenDanhMuc;
  }

  public void setTenDanhMuc(String value) {
    this.tenDanhMuc = value;
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

  @Column(name = "TrangThai", nullable = false)
  private Boolean trangThai = true;

  public Boolean getTrangThai() {
    return trangThai;
  }

  public void setTrangThai(Boolean value) {
    this.trangThai = value;
  }
}
