package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import model.AuthData;
import model.requestsandresults.*;
// import java.util.UUID;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Map;

public class GameService {
    private final DataAccess dataAccess;

    public GameService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ServiceException {
        if (request.authToken() == null || request.gameName() == null) {
            throw new ServiceException("Error: bad request");
        }
        try {
            AuthData auth = dataAccess.getAuth(request.authToken());
            if (auth == null) { throw new ServiceException("Error: unauthorized"); }

            GameData game = new GameData(null, null, null, request.gameName(), null);
            GameData created = dataAccess.createGame(game);  // use returned object from DB
            return new CreateGameResult(created.gameID());
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            if (auth == null) {
                throw new ServiceException("Error: unauthorized");
            }
            Map<Integer, GameData> gamesMap = dataAccess.listGames(authToken);
            ArrayList<GameData> gamesList = new ArrayList<>(gamesMap.values());

            return new ListGamesResult(gamesList);

        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    public EmptyResult joinGame(String authToken, JoinGameRequest request) throws ServiceException {
        // System.out.println(">>> joinGame(): color=" + request.playerColor() + ", gameID=" + request.gameID());
        if (request.playerColor() == null || request.gameID() == null) {
            throw new ServiceException("Error: bad request");
        }

        try {
            AuthData auth = dataAccess.getAuth(authToken);
            if (auth == null) { throw new ServiceException("Error: unauthorized"); }
            GameData game = dataAccess.getGame(request.gameID());
            // if (game == null) { System.out.println(">>> joinGame(): game is null"); }
            if (game == null) { throw new ServiceException("Error: bad request"); }

            String color = request.playerColor().toLowerCase();
            String white = game.whiteUsername();
            String black = game.blackUsername();

            if (color.equals("white")) {
                if (white != null) { throw new ServiceException("Error: already taken"); }
                white = auth.username();
            } else if (color.equals("black")) {
                if (black != null) { throw new ServiceException("Error: already taken"); }
                black = auth.username();
            } else {
                throw new ServiceException("Error: bad request"); // invalid color
            }

            GameData updated = new GameData(game.gameID(), white, black, game.gameName(), game.game());
            // System.out.println(">>> joinGame(): updating DB with white=" + white + ", black=" + black);
            dataAccess.joinGame(updated);
            return new EmptyResult();

        } catch (DataAccessException e) {
            // System.out.println(">>> joinGame(): caught DataAccessException: " + e.getMessage());
            throw new ServiceException("Error: " + e.getMessage());
        }

    }

    public GameData getGame(int gameID) throws ServiceException {
        try {
            GameData game = dataAccess.getGame(gameID);
            if (game == null) {
                throw new ServiceException("Error: bad gameID");
            }
            return game;
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

    public void updateGame(GameData game) throws ServiceException {
        try {
            dataAccess.updateGame(game);
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }

}
