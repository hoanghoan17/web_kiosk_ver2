package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface NhatKyAdminRepository extends JpaRepository<NhatKyAdmin, Long> {
  java.util.List<NhatKyAdmin> findTop200ByOrderByMaNhatKyDesc();
}
