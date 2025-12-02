package websocket;

import dataaccess.DataAccessException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import service.GameService;
import service.ServiceException;
import service.UserService;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import websocket.messages.*;

public class WebSocketServer implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final GameService gameService;   // you'll inject this
    private final UserService userService;

    public WebSocketServer(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        var json = ctx.message();
        UserGameCommand command = gson.fromJson(json, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> onConnect(ctx, command);
            case MAKE_MOVE -> onMakeMove(ctx, command);
            case LEAVE -> onLeave(ctx, command);
            case RESIGN -> onResign(ctx, command);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void onConnect(WsContext ctx, UserGameCommand cmd) {
        String auth = cmd.getAuthToken();
        int gameID = cmd.getGameID();
        try {
            // 1. validate the token
            String username = userService.authenticate(auth);

            // 2. validate game exists
            GameData game = gameService.getGame(gameID);
            if (game == null) {
                sendError(ctx, "Error: bad gameID");
                return;
            }

            // 3. Add connection to ConnectionManager
            connections.addConnection(gameID, username, ctx);

            // 4. Send LOAD_GAME back to this user
            LoadGameMessage load = new LoadGameMessage(game);
            ctx.send(gson.toJson(load));

            // 5. Send NOTIFICATION to others
            NotificationMessage note =
                    new NotificationMessage(username + " joined the game");

            broadcastToOthers(gameID, ctx, note);

        } catch (Exception ex) {
            sendError(ctx, "Error: " + ex.getMessage());
        }
    }

    private void onMakeMove(WsContext ctx, UserGameCommand cmd) {
        // TODO: update game + broadcast
    }

    private void onLeave(WsContext ctx, UserGameCommand cmd) {
        // TODO: broadcast
    }

    private void onResign(WsContext ctx, UserGameCommand cmd) {
        // TODO: broadcast
    }

    private void broadcastToOthers(int gameID, WsContext exclude, ServerMessage msg) {
        var json = gson.toJson(msg);

        for (WsContext c : connections.getConnections(gameID)) {
            if (c != exclude) {
                c.send(json);
            }
        }
    }

    private void sendError(WsContext ctx, String message) {
        ErrorMessage err = new ErrorMessage(message);
        ctx.send(gson.toJson(err));
    }
}

