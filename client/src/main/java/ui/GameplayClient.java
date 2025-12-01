package ui;

import chess.ChessGame;
import exceptions.ResponseException;
import websocket.MessageHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;

public class GameplayClient implements Client, MessageHandler {
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final Integer gameID;
    private final String color;
    private final WebSocketFacade ws;

    public GameplayClient(ServerFacade serverUrl, String authToken, String username, Integer gameID, String color) throws ResponseException {
        this.server = serverUrl;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.color = color;
        this.ws = new WebSocketFacade(serverUrl.getServerUrl(), this);

        try {
            ChessGame game = getGame();
            ChessboardRenderer renderer = new ChessboardRenderer(game, color);
            renderer.displayBoard();
        } catch (Exception e) {
            System.out.println("Error displaying board: " + e.getMessage());
        }

        System.out.println("Press enter to return to menu...");
    }

    @Override
    public void notify(ServerMessage msg) {
        switch(msg.getServerMessageType()) {
//            case LOAD_GAME -> handleLoadGame(msg);
//            case NOTIFICATION -> handleNotification(msg);
//            case ERROR -> handleError(msg);
        }
    }

    @Override
    public ClientStateTransition eval(String input) {
        var previous = new InternalClient(server, authToken, username);
        return ClientStateTransition.switchTo("Returning to menu...", previous);
    }

    @Override
    public String help() {
        return """
            Commands:
                move <START POSITION> <END POSITION>
                resign
                leave
                redraw
                legal <PIECE POSITION>
                help
            """;
    }

    private ChessGame getGame() {
        // change this part later
        return new ChessGame();
    }
}
