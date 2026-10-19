package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
  java.util.List<ChiTietDonHang> findByMaDonHang(Long id);
}
