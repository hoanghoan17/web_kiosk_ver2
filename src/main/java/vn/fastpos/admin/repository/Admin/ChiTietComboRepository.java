package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface ChiTietComboRepository extends JpaRepository<ChiTietCombo, ChiTietComboId> {
  java.util.List<ChiTietCombo> findByIdMaCombo(Integer id);

  void deleteByIdMaCombo(Integer id);
}
