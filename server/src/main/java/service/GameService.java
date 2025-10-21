package service;

import dataAccess.DataAccess;
import dataAccess.MemoryDataAccess;
import dataAccess.DataAccessException;
import model.GameData;
// import java.util.UUID;
import service.RequestAndResults.*;

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
            dataAccess.createGame(game);
            return new CreateGameResult(gameID);
        } catch (DataAccessException e) {
            throw new ServiceException("Error: " + e.getMessage());
        }
    }
}
