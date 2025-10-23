package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.GameService;
import service.RequestAndResults.*;
import service.ServiceException;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            Map<String, String> bodyMap = gson.fromJson(ctx.body(), Map.class);
            String gameName = bodyMap.get("gameName");

            CreateGameRequest req = new CreateGameRequest(authToken, gameName);
            CreateGameResult result = gameService.createGame(req);


            var body = gson.toJson(result);
            ctx.status(200);
            ctx.json(body);

        } catch (ServiceException e) {
            ErrorResponse.sendError(ctx, e.getMessage());
        } catch (Exception e) {
            ErrorResponse.sendError(ctx, e.getMessage(), 500);
        }
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
        } catch (ServiceException e) {
            ErrorResponse.sendError(ctx, e.getMessage());
        } catch (Exception e) {
            ErrorResponse.sendError(ctx, e.getMessage(), 500);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
        } catch (ServiceException e) {
            ErrorResponse.sendError(ctx, e.getMessage());
        } catch (Exception e) {
            ErrorResponse.sendError(ctx, e.getMessage(), 500);
        }
    }
}
