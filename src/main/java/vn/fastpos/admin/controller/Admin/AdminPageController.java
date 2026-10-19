package vn.fastpos.admin.controller.Admin;

import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class AdminPageController {
  private static final Set<String> PAGES =
      Set.of(
          "dashboard",
          "orders",
          "kitchen",
          "categories",
          "dishes",
          "combos",
          "promotions",
          "users",
          "reports",
          "audit",
          "system");

  @GetMapping({"/", "/index.html"})
  public String home() {
    return "redirect:/admin/dashboard";
  }

  @GetMapping("/admin/{page}")
  public String admin(@PathVariable String page, Model model) {
    if (!PAGES.contains(page)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    model.addAttribute("initialPage", page);
    return "admin/layout";
  }
}
