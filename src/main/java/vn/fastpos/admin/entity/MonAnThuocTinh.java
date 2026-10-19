package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "MonAn_ThuocTinh")
public class MonAnThuocTinh {
  @EmbeddedId private MonAnThuocTinhId id;

  public MonAnThuocTinhId getId() {
    return id;
  }

  public void setId(MonAnThuocTinhId id) {
    this.id = id;
  }

  @Nationalized
  @Column(name = "GiaTri", nullable = false, length = 100)
  private String giaTri;

  public String getGiaTri() {
    return giaTri;
  }

  public void setGiaTri(String value) {
    this.giaTri = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaMonAn", insertable = false, updatable = false)
  private MonAn monAn;

  public MonAn getMonAn() {
    return monAn;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaThuocTinh", insertable = false, updatable = false)
  private ThuocTinh thuocTinh;

  public ThuocTinh getThuocTinh() {
    return thuocTinh;
  }
}
