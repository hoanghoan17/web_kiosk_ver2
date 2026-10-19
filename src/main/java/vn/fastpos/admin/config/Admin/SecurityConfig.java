package vn.fastpos.admin.config.Admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.fastpos.admin.repository.Admin.TaiKhoanRepository;
import vn.fastpos.admin.system.Admin.DatabaseGateFilter;
import vn.fastpos.admin.system.Admin.OperationCoordinator;
import vn.fastpos.admin.system.Admin.SystemState;

@Configuration
public class SecurityConfig {
  static class LoginUser extends User {
    final String hashAtLogin;
    final long epoch;

    LoginUser(UserDetails u, long epoch) {
      super(u.getUsername(), u.getPassword(), u.isEnabled(), true, true, true, u.getAuthorities());
      hashAtLogin = u.getPassword();
      this.epoch = epoch;
    }
  }

  @Bean
  PasswordEncoder passwords() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService users(TaiKhoanRepository repo, OperationCoordinator operations) {
    return name -> {
      var t =
          repo.findByTenDangNhap(name)
              .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));
      return new LoginUser(
          User.withUsername(name)
              .password(t.getMatKhauHash())
              .roles(t.getVaiTro().getTenVaiTro())
              .disabled(!Boolean.TRUE.equals(t.getTrangThai()))
              .build(),
          operations.sessionEpoch.get());
    };
  }

  @Bean
  SecurityFilterChain security(
      HttpSecurity http,
      TaiKhoanRepository repo,
      SystemState state,
      OperationCoordinator operations)
      throws Exception {
    http.authorizeHttpRequests(
            a ->
                a.requestMatchers(
                        "/login.html",
                        "/styles.css",
                        "/login.js",
                        "/assets/**",
                        "/api/csrf",
                        "/error")
                    .permitAll()
                    .requestMatchers(
                        "/api/admin/**", "/ws/admin", "/admin/**", "/", "/index.html", "/app.js")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .formLogin(
            f ->
                f.loginPage("/login.html")
                    .loginProcessingUrl("/login")
                    .successHandler((r, s, a) -> s.setStatus(204))
                    .failureHandler(
                        (r, s, e) -> {
                          s.setStatus(401);
                          s.setContentType("application/json;charset=UTF-8");
                          s.getWriter()
                              .write(
                                  "{\"message\":\"Sai tài khoản, mật khẩu hoặc tài khoản đã bị"
                                      + " khóa.\"}");
                        }))
        .logout(l -> l.logoutUrl("/logout").logoutSuccessHandler((r, s, a) -> s.setStatus(204)))
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (r, s, x) -> {
                          if (r.getRequestURI().startsWith("/api/")) s.sendError(401);
                          else s.sendRedirect("/login.html");
                        })
                    .accessDeniedHandler((r, s, x) -> s.sendError(403)))
        .addFilterBefore(
            new DatabaseGateFilter(state, operations), UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(
            new OncePerRequestFilter() {
              protected void doFilterInternal(
                  HttpServletRequest r, HttpServletResponse s, FilterChain chain)
                  throws ServletException, IOException {
                var a = SecurityContextHolder.getContext().getAuthentication();
                boolean statusOnly =
                    r.getMethod().equals("GET")
                        && java.util.Set.of("/api/admin/system/status", "/api/admin/system/jobs")
                            .contains(r.getRequestURI());
                if (a != null
                    && a.isAuthenticated()
                    && !a.getName().equals("anonymousUser")
                    && !(operations.restoring && statusOnly)) {
                  var t = repo.findByTenDangNhap(a.getName());
                  boolean valid =
                      t.isPresent()
                          && a.getPrincipal() instanceof LoginUser login
                          && login.epoch == operations.sessionEpoch.get()
                          && login.hashAtLogin.equals(t.get().getMatKhauHash())
                          && Boolean.TRUE.equals(t.get().getTrangThai())
                          && a.getAuthorities().stream()
                              .anyMatch(
                                  x ->
                                      x.getAuthority()
                                          .equals("ROLE_" + t.get().getVaiTro().getTenVaiTro()));
                  if (!valid) {
                    SecurityContextHolder.clearContext();
                    if (r.getSession(false) != null) r.getSession(false).invalidate();
                    s.sendError(401);
                    return;
                  }
                }
                chain.doFilter(r, s);
              }
            },
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
