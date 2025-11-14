package ui;

import com.google.gson.Gson;
import model.requestsandresults.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public EmptyResult clear() throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .DELETE().build();
        var response = sendRequest(built);
        return handleResponse(response, EmptyResult.class);
    }

    public UserResult register(RegisterRequest request) throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(gson.toJson(request)))
                .build();
        var response = sendRequest(built);
        return handleResponse(response, UserResult.class);
    }

    public UserResult login(LoginRequest request) throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(gson.toJson(request)))
                .build();
        var response = sendRequest(built);
        return handleResponse(response, UserResult.class);
    }

    public UserResult logout(String authToken) throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Authorization", authToken)
                .DELETE()
                .build();
        var response = sendRequest(built);
        return handleResponse(response, UserResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("Authorization", request.authToken())
                .POST(BodyPublishers.ofString(gson.toJson(request)))
                .build();
        var response = sendRequest(built);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", request.authToken())
                .GET()
                .build();
        var response = sendRequest(built);
        return handleResponse(response, ListGamesResult.class);
    }

    public EmptyResult joinGame(String authToken, JoinGameRequest request) throws ClientException {
        var built = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .PUT(BodyPublishers.ofString(gson.toJson(request)))
                .build();
        var response = sendRequest(built);
        return handleResponse(response, EmptyResult.class);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ClientException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ClientException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ClientException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ClientException.fromJson(body);
            }

            throw new ClientException(status, ClientException.fromHttpStatusCode(status));
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
