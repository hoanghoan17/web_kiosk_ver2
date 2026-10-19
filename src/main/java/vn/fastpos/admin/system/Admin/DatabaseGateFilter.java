package vn.fastpos.admin.system.Admin;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class DatabaseGateFilter extends OncePerRequestFilter {
  private final SystemState state;
  private final OperationCoordinator operations;

  public DatabaseGateFilter(SystemState state, OperationCoordinator operations) {
    this.state = state;
    this.operations = operations;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest r, HttpServletResponse s, FilterChain chain)
      throws ServletException, IOException {
    String uri = r.getRequestURI();
    boolean control = uri.startsWith("/api/admin/system/");
    if (operations.restoring
        && !(uri.equals("/api/admin/system/status") || uri.equals("/api/admin/system/jobs"))) {
      unavailable(s, "Đang khôi phục dữ liệu. Vui lòng chờ hoàn tất.");
      return;
    }
    boolean mutation = java.util.Set.of("POST", "PUT", "PATCH", "DELETE").contains(r.getMethod());
    if (state.maintenance().enabled()
        && mutation
        && !control
        && !uri.equals("/login")
        && !uri.equals("/logout")) {
      unavailable(s, state.maintenance().message());
      return;
    }
    // Shared gate drains in-flight requests before exclusive restore; status uses only local
    // control state.
    if (control) {
      chain.doFilter(r, s);
      return;
    }
    if (!operations.database.readLock().tryLock()) {
      unavailable(s, "Cơ sở dữ liệu đang được bảo trì");
      return;
    }
    try {
      chain.doFilter(r, s);
    } finally {
      operations.database.readLock().unlock();
    }
  }

  private void unavailable(HttpServletResponse s, String message) throws IOException {
    s.setStatus(503);
    s.setHeader("Retry-After", "30");
    s.setContentType("application/json;charset=UTF-8");
    s.getWriter()
        .write(
            new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(java.util.Map.of("message", message)));
  }
}
