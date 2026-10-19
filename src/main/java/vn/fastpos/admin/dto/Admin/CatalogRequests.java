package vn.fastpos.admin.dto.Admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class CatalogRequests {
  public record DanhMucRequest(
      @JsonProperty("TenDanhMuc") @NotBlank @Size(max = 100) String tenDanhMuc,
      @JsonProperty("MoTa") @Size(max = 500) String moTa,
      @JsonProperty("TrangThai") @NotNull Boolean trangThai) {}

  public record MonAnRequest(
      @JsonProperty("MaDanhMuc") @NotNull @Positive Integer maDanhMuc,
      @JsonProperty("TenMon") @NotBlank @Size(max = 150) String tenMon,
      @JsonProperty("MoTa") @Size(max = 500) String moTa,
      @JsonProperty("GiaBan") @NotNull @DecimalMin("0") @Digits(integer = 16, fraction = 2)
          BigDecimal giaBan,
      @JsonProperty("HinhAnh") @Size(max = 500) String hinhAnh,
      @JsonProperty("TrangThai") @NotNull Boolean trangThai,
      @JsonProperty("ConHang") @NotNull Boolean conHang) {}

  public record ThanhPhan(
      @JsonProperty("MaMonAn") @NotNull @Positive Integer maMonAn,
      @JsonProperty("SoLuong") @NotNull @Min(1) @Max(100) Integer soLuong) {}

  public record ComboRequest(
      @JsonProperty("TenCombo") @NotBlank @Size(max = 150) String tenCombo,
      @JsonProperty("MoTa") @Size(max = 500) String moTa,
      @JsonProperty("GiaBan") @NotNull @DecimalMin("0") @Digits(integer = 16, fraction = 2)
          BigDecimal giaBan,
      @JsonProperty("HinhAnh") @Size(max = 500) String hinhAnh,
      @JsonProperty("TrangThai") @NotNull Boolean trangThai,
      @NotEmpty @Size(max = 50) List<@Valid ThanhPhan> items) {}

  public record KhuyenMaiRequest(
      @JsonProperty("MaCode") @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,50}") String maCode,
      @JsonProperty("TenKhuyenMai") @NotBlank @Size(max = 150) String tenKhuyenMai,
      @JsonProperty("MoTa") @Size(max = 500) String moTa,
      @JsonProperty("LoaiGiam") @Pattern(regexp = "PHAN_TRAM|SO_TIEN") @NotNull String loaiGiam,
      @JsonProperty("GiaTriGiam") @NotNull @DecimalMin("0.01") @Digits(integer = 16, fraction = 2)
          BigDecimal giaTriGiam,
      @JsonProperty("GiaTriDonToiThieu") @DecimalMin("0") @Digits(integer = 16, fraction = 2)
          BigDecimal giaTriDonToiThieu,
      @JsonProperty("GiamToiDa") @DecimalMin("0") @Digits(integer = 16, fraction = 2)
          BigDecimal giamToiDa,
      @JsonProperty("NgayBatDau") @NotNull LocalDateTime ngayBatDau,
      @JsonProperty("NgayKetThuc") @NotNull LocalDateTime ngayKetThuc,
      @JsonProperty("TrangThai") @NotNull Boolean trangThai) {}

  public record TaiKhoanRequest(
      @JsonProperty("MaVaiTro") @NotNull @Positive Integer maVaiTro,
      @JsonProperty("TenDangNhap") @NotBlank @Pattern(regexp = "[A-Za-z0-9_.-]{3,100}")
          String tenDangNhap,
      @JsonProperty("HoTen") @NotBlank @Size(max = 100) String hoTen,
      @JsonProperty("Email") @Email @Size(max = 150) String email,
      @JsonProperty("SoDienThoai") @Size(max = 20) String soDienThoai,
      @JsonProperty("MatKhau") @Size(max = 72) String matKhau,
      @JsonProperty("TrangThai") @NotNull Boolean trangThai) {}

  public record TrangThaiRequest(
      @NotBlank String from, @NotBlank String to, @Size(max = 500) String reason) {}
}
