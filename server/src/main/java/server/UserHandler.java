package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.*;
import java.util.Map;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            UserResult result = userService.register(req);

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

    public void login(Context ctx) {
        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            UserResult result = userService.login(req);

            var body = gson.toJson(result);
            ctx.status(200);
            ctx.json(body);

        } catch (ServiceException e) {
            String msg = e.getMessage();
            var errorBody = gson.toJson(Map.of("message", msg));

            if (msg.contains("bad request")) ctx.status(400);
            else if (msg.contains("already taken")) ctx.status(403);
            else ctx.status(500);

            ctx.json(errorBody);

        } catch (Exception e) {
            var body = gson.toJson(Map.of("message", e.getMessage()));
            ctx.status(500);
            ctx.json(body);
        }
    }
}
