package vn.fastpos.admin.controller.Admin;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.fastpos.admin.dto.Admin.CatalogRequests.*;
import vn.fastpos.admin.service.Admin.TaiKhoanService;
import vn.fastpos.admin.service.Admin.ThucDonService;

@RestController
@RequestMapping("/api/admin")
public class CatalogController {
  private final ThucDonService menu;
  private final TaiKhoanService users;

  public CatalogController(ThucDonService menu, TaiKhoanService users) {
    this.menu = menu;
    this.users = users;
  }

  @GetMapping("/categories")
  public Object listCategory() {
    return menu.danhMuc();
  }

  @PostMapping("/categories")
  public ResponseEntity<?> addCategory(@Valid @RequestBody DanhMucRequest request) {
    return ResponseEntity.status(201).body(menu.luuDanhMuc(null, request));
  }

  @PutMapping("/categories/{id}")
  public Object editCategory(@PathVariable Integer id, @Valid @RequestBody DanhMucRequest request) {
    return menu.luuDanhMuc(id, request);
  }

  @DeleteMapping("/categories/{id}")
  public Object stopCategory(@PathVariable Integer id) {
    menu.ngung("categories", id);
    return Map.of("message", "Đã ngừng sử dụng, lịch sử được giữ lại");
  }

  @GetMapping("/dishes")
  public Object listDish() {
    return menu.monAn();
  }

  @PostMapping("/dishes")
  public ResponseEntity<?> addDish(@Valid @RequestBody MonAnRequest request) {
    return ResponseEntity.status(201).body(menu.luuMonAn(null, request));
  }

  @PutMapping("/dishes/{id}")
  public Object editDish(@PathVariable Integer id, @Valid @RequestBody MonAnRequest request) {
    return menu.luuMonAn(id, request);
  }

  @DeleteMapping("/dishes/{id}")
  public Object stopDish(@PathVariable Integer id) {
    menu.ngung("dishes", id);
    return Map.of("message", "Đã ngừng sử dụng, lịch sử được giữ lại");
  }

  @GetMapping("/combos")
  public Object listCombo() {
    return menu.combos();
  }

  @PostMapping("/combos")
  public ResponseEntity<?> addCombo(@Valid @RequestBody ComboRequest request) {
    return ResponseEntity.status(201).body(menu.luuCombo(null, request));
  }

  @PutMapping("/combos/{id}")
  public Object editCombo(@PathVariable Integer id, @Valid @RequestBody ComboRequest request) {
    return menu.luuCombo(id, request);
  }

  @DeleteMapping("/combos/{id}")
  public Object stopCombo(@PathVariable Integer id) {
    menu.ngung("combos", id);
    return Map.of("message", "Đã ngừng sử dụng, lịch sử được giữ lại");
  }

  @GetMapping("/promotions")
  public Object listPromotion() {
    return menu.khuyenMai();
  }

  @PostMapping("/promotions")
  public ResponseEntity<?> addPromotion(@Valid @RequestBody KhuyenMaiRequest request) {
    return ResponseEntity.status(201).body(menu.luuKhuyenMai(null, request));
  }

  @PutMapping("/promotions/{id}")
  public Object editPromotion(
      @PathVariable Integer id, @Valid @RequestBody KhuyenMaiRequest request) {
    return menu.luuKhuyenMai(id, request);
  }

  @DeleteMapping("/promotions/{id}")
  public Object stopPromotion(@PathVariable Integer id) {
    menu.ngung("promotions", id);
    return Map.of("message", "Đã ngừng sử dụng, lịch sử được giữ lại");
  }

  @GetMapping("/users")
  public Object listUser() {
    return users.danhSach();
  }

  @PostMapping("/users")
  public ResponseEntity<?> addUser(@Valid @RequestBody TaiKhoanRequest request) {
    return ResponseEntity.status(201).body(users.luu(null, request));
  }

  @PutMapping("/users/{id}")
  public Object editUser(@PathVariable Integer id, @Valid @RequestBody TaiKhoanRequest request) {
    return users.luu(id, request);
  }

  @DeleteMapping("/users/{id}")
  public Object stopUser(@PathVariable Integer id) {
    users.khoa(id);
    return Map.of("message", "Đã ngừng sử dụng, lịch sử được giữ lại");
  }

  @GetMapping("/roles")
  public Object roles() {
    return users.vaiTro();
  }
}
