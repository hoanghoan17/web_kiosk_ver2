package vn.fastpos.admin.system.Admin;

public interface BackupEngine {
  boolean enabled();

  String databaseName();

  void backup(String filename) throws Exception;

  void verify(String filename) throws Exception;

  void restore(String filename) throws Exception;

  void check() throws Exception;
}
