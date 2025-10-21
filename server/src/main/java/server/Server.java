package server;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import io.javalin.*;
import service.AdminService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // Register your endpoints and exception handlers here.
        DataAccess dataAccess = new MemoryDataAccess(); //probs gonna change this later on!

        AdminHandler adminHandler = new AdminHandler(new AdminService(dataAccess));
        UserHandler userHandler = new UserHandler(new UserService(dataAccess));
        GameHandler gameHandler = new GameHandler(new GameService(dataAccess));


        javalin.delete("/db", adminHandler::clear);
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
