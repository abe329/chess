package ui;

import chess.ChessGame;
import exceptions.ResponseException;
import model.GameData;
import websocket.MessageHandler;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

public class GameplayClient implements Client, MessageHandler {
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final Integer gameID;
    private final String color;
    private final WebSocketFacade ws;
    private ChessboardRenderer renderer;
    private GameData currentGame;  // whatever server sends as game


    public GameplayClient(ServerFacade serverUrl, String authToken, String username, Integer gameID, String color) throws ResponseException {
        this.server = serverUrl;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.color = color;
        this.ws = new WebSocketFacade(serverUrl.getServerUrl(), this);

        try {
//            ChessGame game = getGame();
//            ChessboardRenderer renderer = new ChessboardRenderer(game, color);
//            renderer.displayBoard();
            UserGameCommand connect = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    authToken,
                    gameID
            );

            ws.send(connect); // IS THIS RIGHT??
        } catch (Exception e) {
            System.out.println("Error displaying board: " + e.getMessage());
        }

        System.out.println("Press enter to return to menu...");
    }

    @Override
    public void notify(ServerMessage msg) {
        switch(msg.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage gm = (LoadGameMessage) msg;
                loadGame(gm.getGame());
            }
            case NOTIFICATION -> {
                NotificationMessage nm = (NotificationMessage) msg;
                System.out.println("> " + nm.getMessage());
            }
            case ERROR -> {
                ErrorMessage em = (ErrorMessage) msg;
                System.out.println("ERROR: " + em.getErrorMessage());
            }
        }
    }

    public void loadGame(GameData gdata) {
        this.currentGame = gdata;
        if (renderer == null) {
            renderer = new ChessboardRenderer(currentGame.getGame(), color);
        } else {
            renderer = new ChessboardRenderer(currentGame.getGame(), color);
        }
        renderer.displayBoard();
    }

    @Override
    public ClientStateTransition eval(String input) {
        var tokens = input.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isBlank()) {
            return ClientStateTransition.stay("Type 'help' for commands.");
        }
        var command = tokens[0].toLowerCase();
        return switch (command) {
            case "help" -> ClientStateTransition.stay(help());
            case "redraw" -> redrawBoard(); // ClientStateTransition.stay(""); }
            case "leave" -> sendLeave();
            case "move" -> handleMove(tokens); //ClientStateTransition.stay(""); }
            case "legal" -> handleLegal(tokens); //ClientStateTransition.stay(""); }
            case "resign" -> handleResign(); //ClientStateTransition.stay(""); }
            default -> ClientStateTransition.stay("Unknown command. Type 'help'.");
        };
    }

    private ClientStateTransition sendLeave() throws ResponseException {
        UserGameCommand leave = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        ws.send(leave);
        ws.close();
        return goBackToMenu();
    }

    private ClientStateTransition goBackToMenu() {
        var internal = new InternalClient(server, authToken, username);
        return ClientStateTransition.switchTo("Leaving game...", internal);
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
