package vn.fastpos.admin.config.Admin;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class Realtime implements WebSocketConfigurer {
  private final Updates updates;

  public Realtime(Updates updates) {
    this.updates = updates;
  }

  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(updates, "/ws/admin");
  }

  @Component
  public static class Updates extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public void afterConnectionEstablished(WebSocketSession s) throws Exception {
      if (s.getPrincipal() == null) {
        s.close(CloseStatus.NOT_ACCEPTABLE);
        return;
      }
      sessions.add(s);
    }

    public void afterConnectionClosed(WebSocketSession s, CloseStatus status) {
      sessions.remove(s);
    }

    public void handleTextMessage(WebSocketSession s, TextMessage m) {}

    public void changed() {
      if (TransactionSynchronizationManager.isSynchronizationActive())
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
              public void afterCommit() {
                broadcast();
              }
            });
      else broadcast();
    }

    private void broadcast() {
      for (var s : sessions)
        try {
          synchronized (s) {
            if (s.isOpen()) s.sendMessage(new TextMessage("{\"type\":\"refresh\"}"));
          }
        } catch (Exception e) {
          sessions.remove(s);
        }
    }
  }
}
