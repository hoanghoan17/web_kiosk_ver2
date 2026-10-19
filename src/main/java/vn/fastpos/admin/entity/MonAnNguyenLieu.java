package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "MonAn_NguyenLieu")
public class MonAnNguyenLieu {
  @EmbeddedId private MonAnNguyenLieuId id;

  public MonAnNguyenLieuId getId() {
    return id;
  }

  public void setId(MonAnNguyenLieuId id) {
    this.id = id;
  }

  @Column(name = "SoLuongCan", nullable = false, precision = 18, scale = 3)
  private BigDecimal soLuongCan;

  public BigDecimal getSoLuongCan() {
    return soLuongCan;
  }

  public void setSoLuongCan(BigDecimal value) {
    this.soLuongCan = value;
  }

  @Column(name = "LaTuyChon", nullable = false)
  private Boolean laTuyChon = false;

  public Boolean getLaTuyChon() {
    return laTuyChon;
  }

  public void setLaTuyChon(Boolean value) {
    this.laTuyChon = value;
  }

  @Nationalized
  @Column(name = "GhiChu", length = 255)
  private String ghiChu;

  public String getGhiChu() {
    return ghiChu;
  }

  public void setGhiChu(String value) {
    this.ghiChu = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaMonAn", insertable = false, updatable = false)
  private MonAn monAn;

  public MonAn getMonAn() {
    return monAn;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaNguyenLieu", insertable = false, updatable = false)
  private NguyenLieu nguyenLieu;

  public NguyenLieu getNguyenLieu() {
    return nguyenLieu;
  }
}
