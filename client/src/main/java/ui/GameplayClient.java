package ui;

import chess.*;
import exceptions.ResponseException;
import model.GameData;
import websocket.MessageHandler;
import websocket.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.util.Arrays;

public class GameplayClient implements Client, MessageHandler {
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final Integer gameID;
    private final String color;
    private final WebSocketFacade ws;
    private ChessboardRenderer renderer;
    private GameData currentGame;


    public GameplayClient(ServerFacade serverUrl, String authToken, String username, Integer gameID, String color) throws ResponseException {
        this.server = serverUrl;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.color = color;
        this.ws = new WebSocketFacade(serverUrl.getServerUrl(), this);

        try {
            UserGameCommand connect = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    authToken,
                    gameID
            );

            ws.send(connect);
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
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (command) {
                case "help" -> ClientStateTransition.stay(help());
                case "redraw" -> {
                    if (renderer != null) { renderer.displayBoard(); }
                    yield ClientStateTransition.stay("");
                }
                case "leave" -> sendLeave();
                case "move" -> handleMove(params);
                case "highlight" -> handleHighlight(params);
                case "resign" -> sendResign();
                default -> ClientStateTransition.stay("Unknown command. Type 'help'.");
            };
        } catch (ResponseException e) {
            return ClientStateTransition.stay("Unknown command. Type 'help'.");
        }
    }

    private ClientStateTransition sendLeave() throws ResponseException {
        UserGameCommand leave = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        ws.send(leave);
        ws.close();

        var internal = new InternalClient(server, authToken, username);
        return ClientStateTransition.switchTo("Leaving game...", internal);
    }

    private ClientStateTransition sendResign() throws ResponseException {
        UserGameCommand resign = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );
        ws.send(resign);
        return ClientStateTransition.stay("You resigned the game.");
    }

    private ClientStateTransition handleMove(String[] tokens) throws ResponseException {if (tokens.length < 2) {
            return ClientStateTransition.stay("Usage: move <FROM> <TO> [PROMOTION - optional]");
        }
        var from = ChessPosition.fromAlgebraic(tokens[0]);
        var to = ChessPosition.fromAlgebraic(tokens[1]);

        ChessPiece.PieceType promotion = null;
        if (tokens.length == 3) {
            promotion = parsePromotionPiece(tokens[2]);
        }

        ChessMove move = new ChessMove(from, to, promotion);
        MakeMoveCommand makeMove = new MakeMoveCommand(
                authToken,
                gameID,
                move
        );
        ws.send(makeMove);
        return ClientStateTransition.stay("");
    }

    private ClientStateTransition handleHighlight(String[] tokens) throws ResponseException {
        if (tokens.length != 1) {
            return ClientStateTransition.stay("Usage: highlight <PIECE POSITION");
        }
        var pos = ChessPosition.fromAlgebraic(tokens[0]);
        var moves = currentGame.getGame().validMoves(pos);

        renderer.highlight(moves);

        return ClientStateTransition.stay("");
    }

    @Override
    public String help() {
        return """
            Commands:
                move <FROM> <TO> [PROMOTION - optional] 
                resign
                leave
                redraw
                highlight <PIECE POSITION>
                help
            """;
    }

    private ChessPiece.PieceType parsePromotionPiece(String token) {
        token = token.toLowerCase();

        return switch (token) {
            case "q" -> ChessPiece.PieceType.QUEEN;
            case "r" -> ChessPiece.PieceType.ROOK;
            case "b" -> ChessPiece.PieceType.BISHOP;
            case "n" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalArgumentException("Invalid promotion piece: " + token);
        };
    }
}
