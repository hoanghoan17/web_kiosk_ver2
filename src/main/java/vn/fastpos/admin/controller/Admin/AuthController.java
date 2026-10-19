package vn.fastpos.admin.controller.Admin;

import java.security.Principal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
  @Value("${fastpos.demo:false}")
  boolean demo;

  @GetMapping("/api/csrf")
  public Object csrf(CsrfToken token) {
    return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
  }

  @GetMapping("/api/admin/me")
  public Object me(Principal principal) {
    return Map.of("username", principal.getName(), "demo", demo);
  }
}
