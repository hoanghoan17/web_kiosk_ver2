package vn.fastpos.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ChiTietCombo")
public class ChiTietCombo {
  @EmbeddedId private ChiTietComboId id;

  public ChiTietComboId getId() {
    return id;
  }

  public void setId(ChiTietComboId id) {
    this.id = id;
  }

  @Column(name = "SoLuong", nullable = false)
  private Integer soLuong;

  public Integer getSoLuong() {
    return soLuong;
  }

  public void setSoLuong(Integer value) {
    this.soLuong = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaCombo", insertable = false, updatable = false)
  private Combo combo;

  public Combo getCombo() {
    return combo;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaMonAn", insertable = false, updatable = false)
  private MonAn monAn;

  public MonAn getMonAn() {
    return monAn;
  }
}
