package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import service.AdminService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        // System.out.println(">>> STARTING NEW SERVER INSTANCE <<< " + this);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // Register your endpoints and exception handlers here.
        // DataAccess dataAccess = new MemoryDataAccess(); //probs gonna change this in the next phase!
        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
            // System.out.println("Using MySQL database for persistence");
        } catch (Exception e) {
            // System.out.println("MySQL not available.");
            e.printStackTrace();
            dataAccess = new MemoryDataAccess();
        }

        AdminHandler adminHandler = new AdminHandler(new AdminService(dataAccess));
        UserHandler userHandler = new UserHandler(new UserService(dataAccess));
        GameHandler gameHandler = new GameHandler(new GameService(dataAccess));


        javalin.delete("/db", adminHandler::clear);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.post("/game", gameHandler::createGame);
        javalin.get("/game", gameHandler::listGames);
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
