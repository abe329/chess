package ui;

public class GameplayClient implements Client{
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final Integer gameID;

    public GameplayClient(ServerFacade serverUrl, String authToken, String username, Integer gameID) {
        this.server = serverUrl;
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
    }

    @Override
    public ClientStateTransition eval(String input) {
        var previous = new InternalClient(server, authToken, username);
        return ClientStateTransition.switchTo("Returning to menu...", previous);
    }

    @Override
    public String help() {
        return "Not implemented yet";
    }
}
