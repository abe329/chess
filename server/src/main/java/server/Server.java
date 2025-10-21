package server;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import io.javalin.*;
import service.GameService;
import service.RequestAndResults.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // UserHandler userHandler = new UserHandler(new UserService());

        // Register your endpoints and exception handlers here.
        DataAccess dataAccess = new MemoryDataAccess();

        UserService userService = new UserService(dataAccess);
        UserHandler userHandler = new UserHandler(userService);
        GameService gameService = new GameService(dataAccess);
        GameHandler gameHandler = new GameHandler(gameService);


        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.post("/game", gameHandler::createGame);

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
