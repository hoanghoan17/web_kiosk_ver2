package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MonAnThuocTinhId implements Serializable {
  public MonAnThuocTinhId() {}

  @Column(name = "MaMonAn")
  private Integer maMonAn;

  public Integer getMaMonAn() {
    return maMonAn;
  }

  public void setMaMonAn(Integer value) {
    this.maMonAn = value;
  }

  @Column(name = "MaThuocTinh")
  private Integer maThuocTinh;

  public Integer getMaThuocTinh() {
    return maThuocTinh;
  }

  public void setMaThuocTinh(Integer value) {
    this.maThuocTinh = value;
  }

  public MonAnThuocTinhId(Integer maMonAn, Integer maThuocTinh) {
    this.maMonAn = maMonAn;
    this.maThuocTinh = maThuocTinh;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof MonAnThuocTinhId x
        && Objects.equals(maMonAn, x.maMonAn)
        && Objects.equals(maThuocTinh, x.maThuocTinh);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maMonAn, maThuocTinh);
  }
}
