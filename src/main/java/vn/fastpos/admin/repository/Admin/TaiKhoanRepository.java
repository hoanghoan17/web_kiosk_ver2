package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
  @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "vaiTro")
  java.util.Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

  long countByMaVaiTroAndTrangThai(Integer role, Boolean status);
}
