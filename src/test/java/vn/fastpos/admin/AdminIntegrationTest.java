package vn.fastpos.admin;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fastpos.admin.exception.Admin.ApiException;
import vn.fastpos.admin.service.Admin.TaiKhoanService;
import vn.fastpos.admin.system.Admin.SystemState;

@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:tests;MODE=MSSQLServer;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
      "fastpos.demo=true",
      "spring.profiles.active=demo",
      "fastpos.system.state-dir=./target/test-ops"
    })
@AutoConfigureMockMvc
class AdminIntegrationTest {
  @Autowired
  SystemState systemState;
  @Autowired jakarta.persistence.EntityManager em;
  @Autowired
  TaiKhoanService accountService;
  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate db;
  @Autowired PasswordEncoder encoder;

  MockHttpSession login(String user) throws Exception {
    var result =
        mvc.perform(
                post("/login").with(csrf()).param("username", user).param("password", "Admin@123"))
            .andExpect(status().isNoContent())
            .andReturn();
    return (MockHttpSession) result.getRequest().getSession(false);
  }

  @Test
  void anonymousCannotReadAdmin() throws Exception {
    mvc.perform(get("/api/admin/orders")).andExpect(status().isUnauthorized());
  }

  @Test
  void realLoginAndPermissions() throws Exception {
    mvc.perform(get("/api/admin/me").session(login("admin")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("admin"));
    mvc.perform(get("/api/admin/orders").session(login("nv001"))).andExpect(status().isForbidden());
  }

  @Test
  void wrongPassword() throws Exception {
    mvc.perform(post("/login").with(csrf()).param("username", "admin").param("password", "wrong"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void csrfRequired() throws Exception {
    mvc.perform(delete("/api/admin/dishes/1").session(login("admin")))
        .andExpect(status().isForbidden());
  }

  @Test
  @Transactional
  void crudAndAudit() throws Exception {
    var session = login("admin");
    mvc.perform(
            post("/api/admin/categories")
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"TenDanhMuc\":\"Món thử\",\"MoTa\":\"\",\"TrangThai\":true}"))
        .andExpect(status().isCreated());
    long id =
        db.queryForObject(
            "SELECT MaDanhMuc FROM DanhMucMonAn WHERE TenDanhMuc='Món thử'", Long.class);
    mvc.perform(
            put("/api/admin/categories/" + id)
                .session(session)
                .with(csrf())
                .contentType("application/json")
                .content("{\"TenDanhMuc\":\"Món đã sửa\",\"MoTa\":\"Thử\",\"TrangThai\":true}"))
        .andExpect(status().isOk());
    mvc.perform(delete("/api/admin/categories/" + id).session(session).with(csrf()))
        .andExpect(status().isOk());
    em.flush();
    assertThat(
            db.queryForObject(
                "SELECT TrangThai FROM DanhMucMonAn WHERE MaDanhMuc=?", Boolean.class, id))
        .isFalse();
    assertThat(
            db.queryForObject(
                "SELECT COUNT(*) FROM NhatKyAdmin WHERE DoiTuong=?",
                Integer.class,
                "categories#" + id))
        .isEqualTo(3);
  }

  @Test
  @Transactional
  void comboValidationRollsBack() throws Exception {
    var s = login("admin");
    var old = db.queryForObject("SELECT TenCombo FROM Combo WHERE MaCombo=1", String.class);
    mvc.perform(
            put("/api/admin/combos/1")
                .session(s)
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"TenCombo\":\"Không"
                        + " lưu\",\"MoTa\":\"\",\"GiaBan\":1000,\"HinhAnh\":\"\",\"TrangThai\":true,\"items\":[{\"MaMonAn\":1,\"SoLuong\":1},{\"MaMonAn\":1,\"SoLuong\":2}]}"))
        .andExpect(status().isBadRequest());
    assertThat(db.queryForObject("SELECT TenCombo FROM Combo WHERE MaCombo=1", String.class))
        .isEqualTo(old);
  }

  @Test
  @Transactional
  void validComboSavedAtomically() throws Exception {
    mvc.perform(
            put("/api/admin/combos/1")
                .session(login("admin"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"TenCombo\":\"Combo"
                        + " sửa\",\"MoTa\":\"\",\"GiaBan\":100000,\"HinhAnh\":\"\",\"TrangThai\":true,\"items\":[{\"MaMonAn\":1,\"SoLuong\":2}]}"))
        .andExpect(status().isOk());
    assertThat(
            db.queryForObject("SELECT COUNT(*) FROM ChiTietCombo WHERE MaCombo=1", Integer.class))
        .isEqualTo(1);
    assertThat(db.queryForObject("SELECT SoLuong FROM ChiTietCombo WHERE MaCombo=1", Integer.class))
        .isEqualTo(2);
  }

  @Test
  @Transactional
  void cannotCookUnpaidOrCancelPaid() throws Exception {
    var s = login("admin");
    db.update("UPDATE DonHang SET TrangThaiDon='CHO_CHE_BIEN' WHERE MaDonHang=5");
    mvc.perform(
            post("/api/admin/orders/5/status")
                .session(s)
                .with(csrf())
                .contentType("application/json")
                .content("{\"from\":\"CHO_CHE_BIEN\",\"to\":\"DANG_CHE_BIEN\"}"))
        .andExpect(status().isConflict());
    db.update("UPDATE DonHang SET TrangThaiDon='CHO_THANH_TOAN' WHERE MaDonHang=1");
    mvc.perform(
            post("/api/admin/orders/1/status")
                .session(s)
                .with(csrf())
                .contentType("application/json")
                .content("{\"from\":\"CHO_THANH_TOAN\",\"to\":\"DA_HUY\",\"reason\":\"Kiểm thử\"}"))
        .andExpect(status().isConflict());
  }

  @Test
  @Transactional
  void cannotSkipOrReplayState() throws Exception {
    var s = login("admin");
    mvc.perform(
            post("/api/admin/orders/1/status")
                .session(s)
                .with(csrf())
                .contentType("application/json")
                .content("{\"from\":\"CHO_CHE_BIEN\",\"to\":\"HOAN_THANH\"}"))
        .andExpect(status().isBadRequest());
    String json = "{\"from\":\"CHO_CHE_BIEN\",\"to\":\"DANG_CHE_BIEN\"}";
    mvc.perform(
            post("/api/admin/orders/1/status")
                .session(s)
                .with(csrf())
                .contentType("application/json")
                .content(json))
        .andExpect(status().isOk());
    mvc.perform(
            post("/api/admin/orders/1/status")
                .session(s)
                .with(csrf())
                .contentType("application/json")
                .content(json))
        .andExpect(status().isConflict());
  }

  @Test
  @Transactional
  void lockingInvalidatesExistingSession() throws Exception {
    var s = login("admin");
    db.update("UPDATE TaiKhoan SET TrangThai=0 WHERE TenDangNhap='admin'");
    em.clear();
    mvc.perform(get("/api/admin/me").session(s)).andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  void passwordChangeInvalidatesSession() throws Exception {
    var s = login("admin");
    db.update(
        "UPDATE TaiKhoan SET MatKhauHash=? WHERE TenDangNhap='admin'",
        encoder.encode("differentPassword"));
    em.clear();
    mvc.perform(get("/api/admin/me").session(s)).andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  void cannotLockSelf() throws Exception {
    mvc.perform(delete("/api/admin/users/1").session(login("admin")).with(csrf()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void reportsExcludeUnpaidAndCancelled() throws Exception {
    var day = java.time.LocalDate.now().toString();
    mvc.perform(
            get("/api/admin/reports").session(login("admin")).param("from", day).param("to", day))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.summary.SoDon").value(5))
        .andExpect(jsonPath("$.summary.DoanhThu").value(435000));
  }

  @Test
  void accountResponseDoesNotLeakHash() throws Exception {
    var r =
        mvc.perform(get("/api/admin/users").session(login("admin")))
            .andExpect(status().isOk())
            .andReturn();
    assertThat(r.getResponse().getContentAsString())
        .doesNotContain("MatKhauHash")
        .doesNotContain("$2a$");
  }

  @Test
  void reportsRejectInvalidRange() throws Exception {
    mvc.perform(
            get("/api/admin/reports")
                .session(login("admin"))
                .param("from", "2026-10-13")
                .param("to", "2026-10-01"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  void soldOutDishBlocksItsCombo() throws Exception {
    db.update("UPDATE MonAn SET ConHang=0 WHERE MaMonAn=1");
    mvc.perform(get("/api/admin/combos").session(login("admin")))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$[?(@.MaCombo == 1)].CoTheBan").value(org.hamcrest.Matchers.contains(false)));
  }

  @Test
  @Transactional
  void archiveDishKeepsHistoricalOrder() throws Exception {
    mvc.perform(delete("/api/admin/dishes/1").session(login("admin")).with(csrf()))
        .andExpect(status().isOk());
    mvc.perform(get("/api/admin/orders/1").session(login("admin")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(2));
  }

  @Test
  @Transactional
  void rejectInvalidPercentAndDates() throws Exception {
    mvc.perform(
            post("/api/admin/promotions")
                .session(login("admin"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"MaCode\":\"BAD101\",\"TenKhuyenMai\":\"Sai\",\"LoaiGiam\":\"PHAN_TRAM\",\"GiaTriGiam\":101,\"TrangThai\":true}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @Transactional
  void lastAdminCannotBeDisabledByService() {
    var service = accountService;
    org.springframework.security.core.context.SecurityContextHolder.clearContext();
    assertThatThrownBy(() -> service.khoa(1))
        .isInstanceOf(ApiException.class)
        .hasMessageContaining("ít nhất một Admin");
  }

  @Test
  void mvcPageRendersNamedView() throws Exception {
    mvc.perform(get("/admin/dishes").session(login("admin")))
        .andExpect(status().isOk())
        .andExpect(view().name("admin/layout"))
        .andExpect(content().string(org.hamcrest.Matchers.containsString("data-page=\"dishes\"")));
  }

  @Test
  void maintenanceBlocksWritesAndCanBeTurnedOff() throws Exception {
    var session = login("admin");
    try {
      mvc.perform(
              post("/api/admin/system/maintenance")
                  .session(session)
                  .with(csrf())
                  .contentType("application/json")
                  .content(
                      "{\"enabled\":true,\"message\":\"Bảo trì kiểm"
                          + " thử\",\"password\":\"Admin@123\"}"))
          .andExpect(status().isOk());
      mvc.perform(get("/api/admin/categories").session(session)).andExpect(status().isOk());
      mvc.perform(
              post("/api/admin/categories")
                  .session(session)
                  .with(csrf())
                  .contentType("application/json")
                  .content("{\"TenDanhMuc\":\"Không lưu\",\"TrangThai\":true}"))
          .andExpect(status().isServiceUnavailable());
      mvc.perform(
              post("/api/admin/system/maintenance")
                  .session(session)
                  .with(csrf())
                  .contentType("application/json")
                  .content(
                      "{\"enabled\":false,\"message\":\"Hoạt động\",\"password\":\"Admin@123\"}"))
          .andExpect(status().isOk());
    } finally {
      systemState.maintenance(false, "Hoạt động");
    }
  }

  @Test
  void maintenanceRequiresCorrectPassword() throws Exception {
    mvc.perform(
            post("/api/admin/system/maintenance")
                .session(login("admin"))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"enabled\":true,\"message\":\"Bảo trì\",\"password\":\"wrong-password\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void demoDoesNotPretendToCreateSqlServerBackup() throws Exception {
    mvc.perform(
            post("/api/admin/system/jobs")
                .session(login("admin"))
                .with(csrf())
                .contentType("application/json")
                .content("{\"kind\":\"BACKUP\",\"password\":\"Admin@123\"}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void dashboardUsesEntityHourAggregation() throws Exception {
    mvc.perform(get("/api/admin/dashboard").session(login("admin")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.hourly").isArray())
        .andExpect(jsonPath("$.summary.DoanhThu").value(435000));
  }
}
