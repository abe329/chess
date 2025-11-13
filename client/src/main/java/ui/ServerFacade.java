package ui;

import model.requestsandresults.*;
import java.net.http.HttpClient;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserResult register(RegisterRequest request) throws ClientException {...}

    public UserResult login(LoginRequest request) throws ClientException {...}

    public UserResult logout(String authToken) throws ClientException { ... }

    public CreateGameResult createGame(CreateGameRequest request) throws ClientException {...}

    public ListGamesResult listGames(ListGamesRequest request) throws ClientException {...}

    public EmptyResult joinGame(JoinGameRequest request) throws ClientException {...}
}
