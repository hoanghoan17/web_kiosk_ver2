package vn.fastpos.admin.service.Admin;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import vn.fastpos.admin.config.Admin.Realtime;
import vn.fastpos.admin.dto.Admin.CatalogRequests.TaiKhoanRequest;
import vn.fastpos.admin.dto.Admin.ViewMapper;
import vn.fastpos.admin.entity.*;
import vn.fastpos.admin.exception.Admin.ApiException;
import vn.fastpos.admin.repository.Admin.TaiKhoanRepository;
import vn.fastpos.admin.repository.Admin.VaiTroRepository;

@Service
@Transactional(readOnly = true)
public class TaiKhoanService {
  private final TaiKhoanRepository repo;
  private final VaiTroRepository roles;
  private final PasswordEncoder passwords;
  private final NhatKyService log;
  private final Realtime.Updates updates;

  public TaiKhoanService(
      TaiKhoanRepository repo,
      VaiTroRepository roles,
      PasswordEncoder passwords,
      NhatKyService log,
      Realtime.Updates updates) {
    this.repo = repo;
    this.roles = roles;
    this.passwords = passwords;
    this.log = log;
    this.updates = updates;
  }

  public Object danhSach() {
    return repo.findAll(org.springframework.data.domain.Sort.by("maTaiKhoan").descending()).stream()
        .map(
            t -> {
              var d = ViewMapper.of(t);
              d.put("TenVaiTro", roles.findById(t.getMaVaiTro()).orElseThrow().getTenVaiTro());
              return d;
            })
        .toList();
  }

  public Object vaiTro() {
    return roles.findAll().stream().map(ViewMapper::of).toList();
  }

  private TaiKhoan tim(int id) {
    return repo.findById(id).orElseThrow(() -> ApiException.bad("Tài khoản không tồn tại"));
  }

  private void baoVe(TaiKhoan old, int role, boolean enabled) {
    var a = SecurityContextHolder.getContext().getAuthentication();
    var current = a == null ? "SYSTEM" : a.getName();
    String oldRole = roles.findById(old.getMaVaiTro()).orElseThrow().getTenVaiTro(),
        newRole =
            roles
                .findById(role)
                .orElseThrow(() -> ApiException.bad("Vai trò không tồn tại"))
                .getTenVaiTro();
    if (old.getTenDangNhap().equals(current) && (!enabled || !newRole.equals("ADMIN")))
      throw ApiException.bad("Không tự khóa hoặc gỡ quyền Admin của phiên hiện tại");
    if (oldRole.equals("ADMIN")
        && Boolean.TRUE.equals(old.getTrangThai())
        && (!enabled || !newRole.equals("ADMIN"))
        && repo.countByMaVaiTroAndTrangThai(old.getMaVaiTro(), true) <= 1)
      throw ApiException.conflict("Phải giữ ít nhất một Admin đang hoạt động");
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public Object luu(Integer id, TaiKhoanRequest r) {
    roles.findById(r.maVaiTro()).orElseThrow(() -> ApiException.bad("Vai trò không tồn tại"));
    var e = id == null ? new TaiKhoan() : tim(id);
    if (id != null) {
      if (!e.getTenDangNhap().equals(r.tenDangNhap()))
        throw ApiException.bad("Không đổi tên đăng nhập tài khoản hiện có");
      baoVe(e, r.maVaiTro(), r.trangThai());
    }
    String password = r.matKhau() == null ? "" : r.matKhau();
    if (id == null && password.isBlank()) throw ApiException.bad("Cần nhập mật khẩu");
    if (!password.isEmpty()) {
      if (password.length() < 8
          || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
        throw ApiException.bad("Mật khẩu tối thiểu 8 ký tự, tối đa 72 byte");
      e.setMatKhauHash(passwords.encode(password));
    }
    e.setMaVaiTro(r.maVaiTro());
    e.setTenDangNhap(r.tenDangNhap());
    e.setHoTen(r.hoTen().trim());
    e.setEmail(r.email());
    e.setSoDienThoai(r.soDienThoai());
    e.setTrangThai(r.trangThai());
    e.setNgayCapNhat(LocalDateTime.now());
    repo.saveAndFlush(e);
    log.ghi("TAI_KHOAN", "users#" + e.getMaTaiKhoan(), "Cập nhật tài khoản và quyền");
    updates.changed();
    return Map.of("id", e.getMaTaiKhoan());
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void khoa(int id) {
    var e = tim(id);
    baoVe(e, e.getMaVaiTro(), false);
    e.setTrangThai(false);
    log.ghi("KHOA_TAI_KHOAN", "users#" + id, "Khóa tài khoản");
    updates.changed();
  }

  public void xacThucLai(String username, String password) {
    var e =
        repo.findByTenDangNhap(username)
            .orElseThrow(() -> ApiException.bad("Tài khoản không tồn tại"));
    if (!Boolean.TRUE.equals(e.getTrangThai())
        || !e.getVaiTro().getTenVaiTro().equals("ADMIN")
        || password == null
        || !passwords.matches(password, e.getMatKhauHash()))
      throw ApiException.bad("Mật khẩu xác nhận không đúng hoặc tài khoản không có quyền Admin");
  }
}
