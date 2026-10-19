package vn.fastpos.admin.system.Admin;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Component;

@Component
public class OperationCoordinator {
  public final ReentrantReadWriteLock database = new ReentrantReadWriteLock(true);
  public final AtomicBoolean busy = new AtomicBoolean(false);
  public volatile boolean restoring = false;
  public final java.util.concurrent.atomic.AtomicLong sessionEpoch =
      new java.util.concurrent.atomic.AtomicLong();
}
