//package server;
//
//import service.UserService;
//import com.google.gson.Gson;
//import service.RegisterRequest;
//import service.RegisterResult;
//import service.ServiceException;
//import io.javalin.http.*;
//
//import java.util.Map;
//
//
//public class UserHandler {
//    private final UserService userService;
//    private final Gson serializer;
//
//    public UserHandler(UserService userService) {
//        this.userService = userService;
//        this.serializer = new Gson();
//    }
//
//    public void register(Context ctx) {
//        try {
//            RegisterRequest req = serializer.fromJson(ctx.body(), RegisterRequest.class);
//            RegisterResult result = userService.register(req);
//            ctx.status(200);
//            ctx.json(result);
//
//        } catch (ServiceException e) {
//            String msg = e.getMessage();
//            if (msg.contains("bad request")) ctx.status(400);
//            else if (msg.contains("already taken")) ctx.status(403);
//            else ctx.status(500);
//
//            var body = serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
//            ctx.json(body);
//
//        } catch (Exception e) {
//            var body = serializer.toJson(Map.of("message", "Error: " + e.getMessage()));
//            ctx.status(500);
//            ctx.json(body);
//        }
//    }
//}
package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.UserService;
import service.RegisterRequest;
import service.RegisterResult;
import service.ServiceException;
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
            RegisterResult result = userService.register(req);

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
