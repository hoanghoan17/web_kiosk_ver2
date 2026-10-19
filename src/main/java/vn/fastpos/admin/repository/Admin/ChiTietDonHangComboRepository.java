package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface ChiTietDonHangComboRepository extends JpaRepository<ChiTietDonHangCombo, Long> {
  java.util.List<ChiTietDonHangCombo> findByMaDonHang(Long id);
}
