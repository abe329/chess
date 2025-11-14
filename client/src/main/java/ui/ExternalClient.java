package ui;

import model.requestsandresults.*;

import java.util.Arrays;

public class ExternalClient implements Client {
    private final ServerFacade server;

    public ExternalClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    @Override
    public ClientStateTransition eval(String input) throws ClientException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "help" -> ClientStateTransition.stay(help());
            case "quit" -> ClientStateTransition.quit("Adios good friend!");
            default -> ClientStateTransition.stay("Unknown command. Type help.");
        };
    }

    @Override
    public String help() {
        return """
            Commands:
              register <USERNAME> <PASSWORD> <EMAIL> - create an account
              login <USERNAME> <PASSWORD> - login to play chess
              quit - stop playing chess
              help - list of possible commands
            """;
    }

    private ClientStateTransition login(String[] tokens) throws ClientException {
        var username = tokens.length >= 1 ? tokens[0] : prompt("Username: ");
        var password = tokens.length >= 2 ? tokens[1] : prompt("Password: ");

        var result = server.login(new LoginRequest(username, password));
        var post = new InternalClient(server, result.authToken(), username);

        return ClientStateTransition.switchTo(String.format("Logged in as %s", username), post);
    }

    private ClientStateTransition register(String[] tokens) throws ClientException {
        var username = tokens.length >= 1 ? tokens[0] : prompt("Username: ");
        var password = tokens.length >= 2 ? tokens[1] : prompt("Password: ");
        var email = tokens.length >= 3 ? tokens[2] : prompt("Email: ");

        var result = server.register(new RegisterRequest(username, password, email));
        var post = new InternalClient(server, result.authToken(), username);

        return ClientStateTransition.switchTo(String.format("Logged in as %s", username), post);
    }

    private String prompt(String msg) {
        System.out.print(msg);
        return new java.util.Scanner(System.in).nextLine();
    }
}
