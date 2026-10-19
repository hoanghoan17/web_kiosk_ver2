package vn.fastpos.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "MonAn")
public class MonAn {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaMonAn", nullable = false)
  private Integer maMonAn;

  public Integer getMaMonAn() {
    return maMonAn;
  }

  public void setMaMonAn(Integer value) {
    this.maMonAn = value;
  }

  @Column(name = "MaDanhMuc", nullable = false)
  private Integer maDanhMuc;

  public Integer getMaDanhMuc() {
    return maDanhMuc;
  }

  public void setMaDanhMuc(Integer value) {
    this.maDanhMuc = value;
  }

  @Nationalized
  @Column(name = "TenMon", nullable = false, length = 150)
  private String tenMon;

  public String getTenMon() {
    return tenMon;
  }

  public void setTenMon(String value) {
    this.tenMon = value;
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

  @Column(name = "GiaBan", nullable = false, precision = 18, scale = 2)
  private BigDecimal giaBan;

  public BigDecimal getGiaBan() {
    return giaBan;
  }

  public void setGiaBan(BigDecimal value) {
    this.giaBan = value;
  }

  @Column(name = "HinhAnh", length = 500)
  private String hinhAnh;

  public String getHinhAnh() {
    return hinhAnh;
  }

  public void setHinhAnh(String value) {
    this.hinhAnh = value;
  }

  @Column(name = "TrangThai", nullable = false)
  private Boolean trangThai = true;

  public Boolean getTrangThai() {
    return trangThai;
  }

  public void setTrangThai(Boolean value) {
    this.trangThai = value;
  }

  @Column(name = "NgayTao", nullable = false)
  private LocalDateTime ngayTao = LocalDateTime.now();

  public LocalDateTime getNgayTao() {
    return ngayTao;
  }

  public void setNgayTao(LocalDateTime value) {
    this.ngayTao = value;
  }

  @Column(name = "NgayCapNhat")
  private LocalDateTime ngayCapNhat;

  public LocalDateTime getNgayCapNhat() {
    return ngayCapNhat;
  }

  public void setNgayCapNhat(LocalDateTime value) {
    this.ngayCapNhat = value;
  }

  @Column(name = "ConHang", nullable = false)
  private Boolean conHang = true;

  public Boolean getConHang() {
    return conHang;
  }

  public void setConHang(Boolean value) {
    this.conHang = value;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MaDanhMuc", insertable = false, updatable = false)
  private DanhMucMonAn danhMucMonAn;

  public DanhMucMonAn getDanhMucMonAn() {
    return danhMucMonAn;
  }
}
