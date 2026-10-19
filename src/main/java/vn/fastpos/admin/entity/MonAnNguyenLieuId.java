package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MonAnNguyenLieuId implements Serializable {
  public MonAnNguyenLieuId() {}

  @Column(name = "MaMonAn")
  private Integer maMonAn;

  public Integer getMaMonAn() {
    return maMonAn;
  }

  public void setMaMonAn(Integer value) {
    this.maMonAn = value;
  }

  @Column(name = "MaNguyenLieu")
  private Integer maNguyenLieu;

  public Integer getMaNguyenLieu() {
    return maNguyenLieu;
  }

  public void setMaNguyenLieu(Integer value) {
    this.maNguyenLieu = value;
  }

  public MonAnNguyenLieuId(Integer maMonAn, Integer maNguyenLieu) {
    this.maMonAn = maMonAn;
    this.maNguyenLieu = maNguyenLieu;
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof MonAnNguyenLieuId x
        && Objects.equals(maMonAn, x.maMonAn)
        && Objects.equals(maNguyenLieu, x.maNguyenLieu);
  }

  @Override
  public int hashCode() {
    return Objects.hash(maMonAn, maNguyenLieu);
  }
}
