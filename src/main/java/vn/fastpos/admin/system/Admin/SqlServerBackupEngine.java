package vn.fastpos.admin.system.Admin;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.sql.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SqlServerBackupEngine implements BackupEngine {
  private final String name, dir, url, user, password;
  private final boolean enabled;
  private final HikariDataSource pool;
  private final EntityManagerFactory emf;

  public SqlServerBackupEngine(
      @Value("${fastpos.system.database:KLCN}") String name,
      @Value("${fastpos.system.backup-dir:C:/FastPOSBackups}") String dir,
      @Value("${fastpos.system.admin-url:}") String url,
      @Value("${fastpos.system.admin-user:}") String user,
      @Value("${fastpos.system.admin-password:}") String password,
      @Value("${fastpos.system.backup-enabled:false}") boolean flag,
      Environment env,
      DataSource data,
      EntityManagerFactory emf) {
    if (!name.matches("[A-Za-z][A-Za-z0-9_]{0,127}"))
      throw new IllegalArgumentException("Tên database quản trị không hợp lệ");
    this.name = name;
    this.dir = dir.replaceAll("[\\\\/]+$", "");
    this.url = url;
    this.user = user;
    this.password = password;
    this.emf = emf;
    pool = (HikariDataSource) data;
    enabled = flag && env.acceptsProfiles(org.springframework.core.env.Profiles.of("sqlserver"));
  }

  public boolean enabled() {
    return enabled && !url.isBlank() && !user.isBlank();
  }

  public String databaseName() {
    return name;
  }

  private Connection master() throws Exception {
    if (!enabled())
      throw new IllegalStateException(
          "Chức năng chỉ bật với SQL Server và cấu hình tài khoản sao lưu");
    var c = DriverManager.getConnection(url, user, password);
    if (!"master".equalsIgnoreCase(c.getCatalog())) {
      c.close();
      throw new IllegalStateException("Kết nối quản trị phải trỏ tới master");
    }
    return c;
  }

  private String path(String filename) {
    if (!filename.matches("FastPOS_[A-Za-z0-9_-]+\\.bak"))
      throw new IllegalArgumentException("Tên bản sao lưu không hợp lệ");
    return "N'" + (dir + "/" + filename).replace("'", "''") + "'";
  }

  private void execute(Connection c, String sql) throws Exception {
    try (var statement = c.createStatement()) {
      statement.setQueryTimeout(1800);
      statement.execute(sql);
    }
  }

  private void checkTarget() throws Exception {
    try (var c = pool.getConnection()) {
      if (!name.equalsIgnoreCase(c.getCatalog()))
        throw new IllegalStateException(
            "Database ứng dụng không khớp database sao lưu đã cấu hình");
    }
  }

  public void backup(String filename) throws Exception {
    checkTarget();
    try (var c = master()) {
      execute(
          c,
          "BACKUP DATABASE ["
              + name
              + "] TO DISK = "
              + path(filename)
              + " WITH COPY_ONLY, CHECKSUM, INIT");
    }
    verify(filename);
  }

  public void verify(String filename) throws Exception {
    try (var c = master()) {
      try (var s = c.createStatement();
          var r = s.executeQuery("RESTORE HEADERONLY FROM DISK = " + path(filename))) {
        if (!r.next()
            || !name.equalsIgnoreCase(r.getString("DatabaseName"))
            || r.getInt("BackupType") != 1)
          throw new IllegalStateException(
              "Bản sao lưu không phải full backup của database đã cấu hình");
      }
      execute(c, "RESTORE VERIFYONLY FROM DISK = " + path(filename) + " WITH CHECKSUM");
    }
  }

  public void restore(String filename) throws Exception {
    verify(filename);
    pool.getHikariPoolMXBean().suspendPool();
    pool.getHikariPoolMXBean().softEvictConnections();
    try (var c = master()) {
      try {
        execute(c, "ALTER DATABASE [" + name + "] SET SINGLE_USER WITH ROLLBACK IMMEDIATE");
        execute(
            c,
            "RESTORE DATABASE ["
                + name
                + "] FROM DISK = "
                + path(filename)
                + " WITH REPLACE, RECOVERY");
      } finally {
        execute(c, "ALTER DATABASE [" + name + "] SET MULTI_USER");
      }
    } finally {
      pool.getHikariPoolMXBean().resumePool();
      pool.getHikariPoolMXBean().softEvictConnections();
      emf.getCache().evictAll();
    }
  }

  public void check() throws Exception {
    checkTarget();
    try (var c = pool.getConnection();
        var s = c.createStatement()) {
      s.executeQuery("SELECT TOP 0 ConHang FROM MonAn").close();
      s.executeQuery("SELECT TOP 0 MaNhatKy FROM NhatKyAdmin").close();
    }
    try (var c = master();
        var s = c.createStatement()) {
      s.setQueryTimeout(1800);
      boolean results =
          s.execute("DBCC CHECKDB ([" + name + "]) WITH TABLERESULTS, NO_INFOMSGS, ALL_ERRORMSGS");
      while (true) {
        if (results) {
          try (var r = s.getResultSet()) {
            if (r.next())
              throw new IllegalStateException("CHECKDB báo lỗi. Liên hệ quản trị cơ sở dữ liệu.");
          }
        } else if (s.getUpdateCount() == -1) break;
        results = s.getMoreResults();
      }
    }
  }
}
