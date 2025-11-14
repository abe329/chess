package ui;

import model.requestsandresults.CreateGameRequest;
import model.requestsandresults.*;

import java.util.Arrays;

public class InternalClient implements Client {
    private final ServerFacade server;
    private final String authToken;
    private final String username;

    public InternalClient(ServerFacade serverUrl, String authToken, String username) {
        this.server = serverUrl;
        this.authToken = authToken;
        this.username = username;
    }

    @Override
    public ClientStateTransition eval(String input) throws ClientException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "create" -> create(params);
            case "list" -> list();
            case "join" -> join(params);
            case "observe" -> observe(params);
            case "logout" -> logout();
            case "help" -> ClientStateTransition.stay(help());
            case "quit" -> ClientStateTransition.quit("Adios good friend!");
            default -> ClientStateTransition.stay("Unknown command. Type help.");
        };
    }

    private ClientStateTransition create(String[] params) throws ClientException {
        var gameName = params.length >= 1 ? params[0] : prompt("Game Name: ");
        server.createGame(new CreateGameRequest(authToken, gameName));

        return ClientStateTransition.stay("Successfully created game " + gameName);
    }

    private ClientStateTransition list() throws ClientException {
        var result = server.listGames(new ListGamesRequest(authToken));
        if (result == null || result.games() == null || result.games().isEmpty()) {
            return ClientStateTransition.stay("No games found.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\nCurrent Games:\n");

        int index = 1;
        for (var g : result.games()) {
            sb.append(String.format(
                    "%d. Game name: %s    White: %s    Black: %s\n",
                    index++,
                    g.gameName(),
                    g.whiteUsername() == null ? "-" : g.whiteUsername(),
                    g.blackUsername() == null ? "-" : g.blackUsername()
            ));
        }

        return ClientStateTransition.stay(sb.toString());
    }

    private ClientStateTransition join(String[] params) throws ClientException {
        if (params.length < 2) {
            return ClientStateTransition.stay("Usage: join <ID> <WHITE|BLACK>");
        }

        Integer gameID;
        try {
            gameID = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return ClientStateTransition.stay("Game ID must be a number.");
        }
        var playerColor = params[1].toUpperCase();
        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return ClientStateTransition.stay("Color must be WHITE or BLACK.");
        }

        server.joinGame(authToken, new JoinGameRequest(playerColor, gameID));
        var next = new GameplayClient(server, authToken, username, gameID, playerColor);
        return ClientStateTransition.switchTo("Joined game " + gameID + " as " + playerColor, next);
    }

    private ClientStateTransition observe(String[] tokens) throws ClientException {
        if (tokens.length < 1) {
            return ClientStateTransition.stay("Usage: observe <ID>");
        }

        int gameID;
        try {
            gameID = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            return ClientStateTransition.stay("Game ID must be a number.");
        }
        var next = new GameplayClient(server, authToken, username, gameID, "WHITE");
        return ClientStateTransition.switchTo("Observing game " + gameID, next);
    }

    private ClientStateTransition logout() throws ClientException {
        server.logout(authToken);
        var next = new ExternalClient(server.getServerUrl());
        return ClientStateTransition.switchTo("Successfully logged out.", next);
    }

    @Override
    public String help() {
        return """
            Commands:
              create <NAME>
              list
              join <ID> [WHITE|BLACK]
              observe <ID>
              logout
              quit
              help
            """;
    }

    private String prompt(String msg) {
        System.out.print(msg);
        return new java.util.Scanner(System.in).nextLine();
    }
}
