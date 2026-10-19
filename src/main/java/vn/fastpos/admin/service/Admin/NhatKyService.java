package vn.fastpos.admin.service.Admin;

import java.time.LocalDateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.fastpos.admin.dto.Admin.ViewMapper;
import vn.fastpos.admin.entity.NhatKyAdmin;
import vn.fastpos.admin.repository.Admin.NhatKyAdminRepository;

@Service
public class NhatKyService {
  private final NhatKyAdminRepository repo;

  public NhatKyService(NhatKyAdminRepository repo) {
    this.repo = repo;
  }

  public void ghi(String action, String target, String detail) {
    var a = SecurityContextHolder.getContext().getAuthentication();
    NhatKyAdmin log = new NhatKyAdmin();
    log.setTenDangNhap(a == null ? "SYSTEM" : a.getName());
    log.setHanhDong(action);
    log.setDoiTuong(target);
    log.setNoiDung(detail);
    log.setThoiGian(LocalDateTime.now());
    repo.save(log);
  }

  public Object danhSach() {
    return repo.findTop200ByOrderByMaNhatKyDesc().stream().map(ViewMapper::of).toList();
  }
}
