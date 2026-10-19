package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
  java.util.List<ThanhToan> findByMaDonHang(Long id);
}
