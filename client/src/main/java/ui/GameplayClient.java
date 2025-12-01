package ui;

import chess.ChessGame;

public class GameplayClient implements Client{
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final Integer gameID;
    private final String color;

    public GameplayClient(ServerFacade serverUrl, String authToken, String username, Integer gameID, String color) {
        this.server = serverUrl;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.color = color;

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
