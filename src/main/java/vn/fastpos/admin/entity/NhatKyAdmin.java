package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "NhatKyAdmin")
public class NhatKyAdmin {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaNhatKy", nullable = false)
  private Long maNhatKy;

  public Long getMaNhatKy() {
    return maNhatKy;
  }

  public void setMaNhatKy(Long value) {
    this.maNhatKy = value;
  }

  @Column(name = "TenDangNhap", nullable = false, length = 100)
  private String tenDangNhap;

  public String getTenDangNhap() {
    return tenDangNhap;
  }

  public void setTenDangNhap(String value) {
    this.tenDangNhap = value;
  }

  @Nationalized
  @Column(name = "HanhDong", nullable = false, length = 80)
  private String hanhDong;

  public String getHanhDong() {
    return hanhDong;
  }

  public void setHanhDong(String value) {
    this.hanhDong = value;
  }

  @Nationalized
  @Column(name = "DoiTuong", nullable = false, length = 120)
  private String doiTuong;

  public String getDoiTuong() {
    return doiTuong;
  }

  public void setDoiTuong(String value) {
    this.doiTuong = value;
  }

  @Nationalized
  @Column(name = "NoiDung", nullable = false, length = 1000)
  private String noiDung;

  public String getNoiDung() {
    return noiDung;
  }

  public void setNoiDung(String value) {
    this.noiDung = value;
  }

  @Column(name = "ThoiGian", nullable = false)
  private LocalDateTime thoiGian;

  public LocalDateTime getThoiGian() {
    return thoiGian;
  }

  public void setThoiGian(LocalDateTime value) {
    this.thoiGian = value;
  }
}
