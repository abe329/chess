package websocket;

import io.javalin.websocket.WsContext;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<Integer, Set<WsContext>> games = new ConcurrentHashMap<>();
    private final Map<WsContext, String> users = new ConcurrentHashMap<>();

    public void addConnection(int gameID, String username, WsContext ctx) {
        games.computeIfAbsent(gameID, g -> ConcurrentHashMap.newKeySet()).add(ctx);
        users.put(ctx, username);
    }

    public void removeConnection(WsContext ctx) {
        users.remove(ctx);
        games.values().forEach(set -> set.remove(ctx));
    }

    public Set<WsContext> getConnections(int gameID) {
        return games.getOrDefault(gameID, ConcurrentHashMap.newKeySet());
    }

    public String getUsername(WsContext ctx) {
        return users.get(ctx);
    }
}
