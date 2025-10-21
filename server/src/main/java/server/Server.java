package server;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import io.javalin.*;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        DataAccess dataAccess = new MemoryDataAccess();

        // Pass it to all services/handlers
        UserService userService = new UserService(dataAccess);
        UserHandler userHandler = new UserHandler(userService);

        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);

//        javalin.exception(Exception.class, (e, ctx) -> {
//            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
//
//        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
