package vn.fastpos.admin.repository.Admin;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fastpos.admin.entity.*;

public interface DonHangRepository extends JpaRepository<DonHang, Long> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select d from DonHang d where d.maDonHang=:id")
  java.util.Optional<DonHang> lockById(
      @org.springframework.data.repository.query.Param("id") Long id);

  java.util.List<DonHang> findTop500ByOrderByMaDonHangDesc();

  java.util.List<DonHang> findByTrangThaiDonInOrderByThoiGianThanhToanAsc(
      java.util.Collection<String> states);

  long countByTrangThaiDonIn(java.util.Collection<String> states);

  @org.springframework.data.jpa.repository.Query(
      "select count(d) from DonHang d where d.trangThaiDon in :states and exists (select t from"
          + " ThanhToan t where t.maDonHang=d.maDonHang and t.trangThai='THANH_CONG')")
  long countPaidByStates(
      @org.springframework.data.repository.query.Param("states")
          java.util.Collection<String> states);

  @org.springframework.data.jpa.repository.Query(
      "select count(d) from DonHang d where d.trangThaiDon in ('HOAN_THANH','DA_GIAO') and"
          + " d.thoiGianThanhToan>=:start and d.thoiGianThanhToan<:end and exists (select t from"
          + " ThanhToan t where t.maDonHang=d.maDonHang and t.trangThai='THANH_CONG')")
  long countCompletedInDay(
      @org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start,
      @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);
}
