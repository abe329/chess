package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.Map;

public class ErrorResponse {
    private static final Gson GSON = new Gson();

    public static void sendError(Context ctx, String message) {
        String lower = message.toLowerCase();
        int errorStatus = 500;

        if (lower.contains("bad request")) { errorStatus = 400; }
        else if (lower.contains("unauthorized")) { errorStatus = 401; }
        else if (lower.contains("already taken") || lower.contains("forbidden")) { errorStatus = 403; }

        ctx.status(errorStatus).json(GSON.toJson(Map.of("message", message)));
    }

    public static void sendError(Context ctx, String message, int errorStatus) {
        ctx.status(errorStatus).json(GSON.toJson(Map.of("message", message)));
    }
}
