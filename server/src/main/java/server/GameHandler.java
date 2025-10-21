package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.GameData;
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
            String msg = e.getMessage();
            var errorBody = gson.toJson(Map.of("message", "Error: " + msg));

            if (msg.contains("bad request")) ctx.status(400);
            else if (msg.contains("already taken")) ctx.status(403);
            else ctx.status(500);

            ctx.json(errorBody);

        } catch (Exception e) {
            var body = gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            ctx.status(500);
            ctx.json(body);
        }
    }
}
