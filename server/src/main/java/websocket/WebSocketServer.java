package websocket;

import chess.ChessGame;
import chess.ChessMove;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import com.google.gson.Gson;
import websocket.messages.*;

import static chess.ChessGame.TeamColor.*;

public class WebSocketServer implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final GameService gameService;
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
            case MAKE_MOVE -> {
                MakeMoveCommand mCommand = gson.fromJson(json, MakeMoveCommand.class);
                onMakeMove(ctx, mCommand);
            }
            case LEAVE -> onLeave(ctx, command);
            case RESIGN -> onResign(ctx, command);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        connections.removeConnection(ctx);
        System.out.println("Websocket closed");
    }

    private void onConnect(WsContext ctx, UserGameCommand cmd) {
        String auth = cmd.getAuthToken();
        int gameID = cmd.getGameID();
        try {
            // authenticate the token
            String username = userService.authenticate(auth);

            // validate game exists
            GameData game = gameService.getGame(gameID);
            if (game == null) {
                sendError(ctx, "Error: bad gameID");
                return;
            }

            // Add connection
            connections.addConnection(gameID, username, ctx);

            //Send loadGame back to this user
            LoadGameMessage load = new LoadGameMessage(game);
            ctx.send(gson.toJson(load));

            // Send notificiation to others
            String role;

            if (username.equals(game.whiteUsername())) {
                role = "joined as white";
            } else if (username.equals(game.blackUsername())) {
                role = "joined as black";
            } else {
                role = "joined as observer";
            }

            NotificationMessage note =
                    new NotificationMessage(username + " " + role);


            broadcastToOthers(gameID, ctx, note);

        } catch (Exception ex) {
            sendError(ctx, "Error: " + ex.getMessage());
        }
    }

    private void onMakeMove(WsContext ctx, MakeMoveCommand cmd) {
        try {

            //Authenticate auth token
            String auth = cmd.getAuthToken();
            if (auth == null) {
                sendError(ctx, "unauthorized");
                return;
            }
            String username = userService.authenticate(auth);

            // Check game
            GameData game = gameService.getGame(cmd.getGameID());
            if (game == null) {
                sendError(ctx, "bad gameID");
                return;
            }

            ChessGame chess = game.game(); // You store an actual ChessGame object

            if (chess.isGameOver()) {
                sendError(ctx, "game is already over.");
                return;
            }

            // Check turn
            var whoseTurn = chess.getTeamTurn();
            boolean userIsWhite = username.equals(game.whiteUsername());
            boolean userIsBlack = username.equals(game.blackUsername());

            if (!userIsWhite && !userIsBlack) {
                sendError(ctx, "observers cannot move");
                return;
            }

            if (whoseTurn == ChessGame.TeamColor.WHITE && !userIsWhite
                    || whoseTurn == ChessGame.TeamColor.BLACK && !userIsBlack) {
                sendError(ctx, "not your turn");
                return;
            }

            // Validate move
            ChessMove move = cmd.getMove();
            var legalMoves = chess.validMoves(move.getStartPosition());

            if (!legalMoves.contains(move)) {
                sendError(ctx, "illegal move");
                return;
            }
            // Apply move
            chess.makeMove(move);
            // Update game
            GameData updated = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    chess
            );
            gameService.updateGame(updated);

            // Send updated board to ALL
            LoadGameMessage loadMsg = new LoadGameMessage(updated);
            broadcastToAll(cmd.getGameID(), loadMsg);

            // Send notification to others
            String formattedMove = moveToString(move);
            NotificationMessage note = new NotificationMessage(username + " moved " + formattedMove);
            broadcastToOthers(cmd.getGameID(), ctx, note);

            // Check for checkmate first
            boolean whiteCM = chess.isInCheckmate(WHITE);
            boolean blackCM = chess.isInCheckmate(BLACK);
            boolean whiteSM = chess.isInStalemate(WHITE);
            boolean blackSM = chess.isInStalemate(BLACK);

            // Checkmate
            if (whiteCM || blackCM) {
                chess.setGameOver(true);
                gameService.updateGame(updated); //MAYBE change this later

                String loserName = whiteCM ? game.whiteUsername() : game.blackUsername();
                NotificationMessage cmNote = new NotificationMessage(loserName + " is checkmated");
                broadcastToAll(cmd.getGameID(), cmNote);

                return;
            }
            // Stalemate
            if (whiteSM || blackSM) {
                chess.setGameOver(true);
                gameService.updateGame(updated);

                NotificationMessage smNote =
                        new NotificationMessage("Game is a stalemate");
                broadcastToAll(cmd.getGameID(), smNote);
                return;
            }

            // Look for check
            ChessGame.TeamColor opponent =
                    (whoseTurn == WHITE ? BLACK : WHITE);

            if (chess.isInCheck(opponent)) {
                String inCheckUser =
                        (opponent == WHITE ? game.whiteUsername() : game.blackUsername());
                NotificationMessage checkNote =
                        new NotificationMessage(inCheckUser + " is in check");
                broadcastToAll(cmd.getGameID(), checkNote);
            }


        } catch (Exception ex) {
            sendError(ctx, ex.getMessage());
        }
    }

    private void onLeave(WsContext ctx, UserGameCommand cmd) {
        try {
            String username = userService.authenticate(cmd.getAuthToken());
            GameData game = gameService.getGame(cmd.getGameID());
            if (game == null) {
                sendError(ctx, "bad gameID");
                return;
            }

            int gameID = cmd.getGameID();

            String white = game.whiteUsername();
            String black = game.blackUsername();

            if (username.equals(white)) { white = null; }
            if (username.equals(black)) { black = null; }

            GameData updated = new GameData(
                    game.gameID(),
                    white,
                    black,
                    game.gameName(),
                    game.game()
            );
            gameService.updateGame(updated);

            connections.removeConnection(ctx);
            NotificationMessage note = new NotificationMessage(username + " left the game");
            broadcastToOthers(gameID, ctx, note);

        } catch (Exception e) {
            sendError(ctx, e.getMessage());
        }
    }

    private void onResign(WsContext ctx, UserGameCommand cmd) {
        try {
            String username = userService.authenticate(cmd.getAuthToken());
            GameData game = gameService.getGame(cmd.getGameID());
            if (game == null) {
                sendError(ctx, "bad gameID");
                return;
            }
            boolean isPlayer =
                    username.equals(game.whiteUsername()) ||
                            username.equals(game.blackUsername());

            if (!isPlayer) {
                sendError(ctx, "observers cannot resign");
                return;
            }

            ChessGame chess = game.game();
            if (chess.isGameOver()) {
                sendError(ctx, "game is already over.");
                return;
            }

            chess.setGameOver(true);
            GameData updated = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName(),
                    chess
            );
            gameService.updateGame(updated);

            NotificationMessage nm = new NotificationMessage(username + " resigned");
            broadcastToAll(game.gameID(), nm);

        } catch (Exception e){
            sendError(ctx, e.getMessage());
        }
    }

    private void broadcastToOthers(int gameID, WsContext exclude, ServerMessage msg) {
        var json = gson.toJson(msg);

        for (WsContext c : connections.getConnections(gameID)) {
            try {
                if (c.session.equals(exclude.session)) continue;
                if (!c.session.isOpen()) {
                    connections.removeConnection(c);
                    continue;
                }
                c.send(json);
            } catch (Exception ex) {
                connections.removeConnection(c);
            }
        }
    }

    private void broadcastToAll(int gameID, ServerMessage msg) {
        var json = gson.toJson(msg);
        for (WsContext c : connections.getConnections(gameID)) {
            c.send(json);
        }
    }

    private String moveToString(ChessMove move) {
        return move.getStartPosition().toAlgebraic() + " → " + move.getEndPosition().toAlgebraic();
    }

    private void sendError(WsContext ctx, String message) {
        ErrorMessage err = new ErrorMessage(message);
        ctx.send(gson.toJson(err));
    }
}

