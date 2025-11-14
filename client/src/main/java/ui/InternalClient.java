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
//            case "observe" -> observe(params);
            case "logout" -> logout();
            case "help" -> ClientStateTransition.stay(help());
            case "quit" -> ClientStateTransition.quit("Adios good friend!");
            default -> ClientStateTransition.stay("Unknown command. Type help.");
        };
    }

    private ClientStateTransition create(String[] tokens) throws ClientException {
        var gameName = tokens.length > 1 ? tokens[1] : prompt("Game Name: ");

        server.createGame(new CreateGameRequest(authToken, gameName));
        System.out.println("Successfully created game " + gameName);

        return ClientStateTransition.stay("");
    }

    private ClientStateTransition list() throws ClientException {
        var result = server.listGames(new ListGamesRequest(authToken));
        System.out.println(result);
        return ClientStateTransition.stay("");
    }

    private ClientStateTransition join(String[] tokens) throws ClientException {
        var gameID = Integer.valueOf(tokens.length > 1 ? tokens[1] : prompt("Game ID: "));
        var playerColor = tokens.length > 1 ? tokens[1] : prompt("Player Color: ");

        server.joinGame(authToken, new JoinGameRequest(playerColor.toUpperCase(), gameID));
        var post = new GameplayClient(server, authToken, username, gameID);
        return ClientStateTransition.switchTo(String.format("Logged in as %s", username), post);
    }

    // WRONG!!!! I THINK I NEED TO FIX A LOT OF CODE TO GET THIS TO WORK
    private ClientStateTransition observe(String[] tokens) throws ClientException {
        var gameID = Integer.valueOf(tokens.length > 1 ? tokens[1] : prompt("Game ID: "));
        server.joinGame(authToken, new JoinGameRequest("WHITE", gameID));
        var post = new GameplayClient(server, authToken, username, gameID);
        return ClientStateTransition.switchTo("Observing game " + gameID, post);
    }

    private ClientStateTransition logout() throws ClientException {
        server.logout(authToken);
        return ClientStateTransition.quit("Successfully logged out.");
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
