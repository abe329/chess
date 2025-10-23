package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import model.AuthData;
// import java.util.UUID;
import service.RequestAndResults.*;

import java.util.ArrayList;
import java.util.Map;

public class GameService {
    private final DataAccess dataAccess;
    private int GameIDCounter = 1;

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
        int gameID = GameIDCounter++;
        GameData game = new GameData(gameID, null, null, request.gameName(), null);

        try {
            AuthData auth = dataAccess.getAuth(request.authToken());
            if (auth == null) { throw new ServiceException("Error: unauthorized"); }
            dataAccess.createGame(game);
            return new CreateGameResult(gameID);
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
        if (request.playerColor() == null || request.gameID() == null) {
            throw new ServiceException("Error: bad request");
        }
        try {
            AuthData auth = dataAccess.getAuth(authToken);
            if (auth == null) { throw new ServiceException("Error: unauthorized"); }
            GameData game = dataAccess.getGame(request.gameID());
            if (game == null) { throw new ServiceException("Error: bad request"); }

            String color = request.playerColor().toLowerCase();
            String white = game.whiteUsername();
            String black = game.blackUsername();

            if (color.equals("white")) {
                if (white != null) throw new ServiceException("Error: already taken");
                white = auth.username();
            } else if (color.equals("black")) {
                if (black != null) throw new ServiceException("Error: already taken");
                black = auth.username();
            } else {
                throw new ServiceException("Error: bad request"); // invalid color
            }

            GameData updated = new GameData(game.gameID(), white, black, game.gameName(), game.game());

            dataAccess.joinGame(updated);
            return new EmptyResult();

        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }
}
