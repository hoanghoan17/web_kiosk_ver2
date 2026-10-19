package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChiTietComboId implements Serializable {
  public ChiTietComboId() {}

  @Column(name = "MaCombo")
  private Integer maCombo;

  public Integer getMaCombo() {
    return maCombo;
  }

  public void setMaCombo(Integer value) {
    this.maCombo = value;
  }

  @Column(name = "MaMonAn")
  private Integer maMonAn;

  public Integer getMaMonAn() {
    return maMonAn;
  }

  public void setMaMonAn(Integer value) {
    this.maMonAn = value;
  }

  public ChiTietComboId(Integer maCombo, Integer maMonAn) {
    this.maCombo = maCombo;
    this.maMonAn = maMonAn;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof ChiTietComboId x
        && Objects.equals(maCombo, x.maCombo)
        && Objects.equals(maMonAn, x.maMonAn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maCombo, maMonAn);
  }
}
