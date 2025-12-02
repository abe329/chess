package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import service.AdminService;
import service.GameService;
import service.UserService;
import websocket.WebSocketServer;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (Exception e) {
            e.printStackTrace();
            dataAccess = new MemoryDataAccess();
        }

        AdminHandler adminHandler = new AdminHandler(new AdminService(dataAccess));
        UserHandler userHandler = new UserHandler(new UserService(dataAccess));
        GameHandler gameHandler = new GameHandler(new GameService(dataAccess));

        var userService = new UserService(dataAccess);
        var gameService = new GameService(dataAccess);
        var WebSocketServer = new WebSocketServer(userService, gameService);


        javalin.delete("/db", adminHandler::clear);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.post("/game", gameHandler::createGame);
        javalin.get("/game", gameHandler::listGames);
        javalin.put("/game", gameHandler::joinGame);
        javalin.ws("/ws", ws -> {
            ws.onConnect(WebSocketServer);
            ws.onMessage(WebSocketServer);
            ws.onClose(WebSocketServer);
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
