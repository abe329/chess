package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.Map;

public class ErrorResponse {
    private static final Gson gson = new Gson();

    public static void sendError(Context ctx, String message) {
        int errorStatus = 500;

        if (message.contains("bad request")) ctx.status(400);
        else if (message.contains("unauthorized")) ctx.status(401);
        else if (message.contains("already taken")) ctx.status(403);

        ctx.status(errorStatus).json(gson.toJson(Map.of("message", message)));
    }

    public static void sendError(Context ctx, String message, int errorStatus) {
        ctx.status(errorStatus).json(gson.toJson(Map.of("message", message)));
    }
}
