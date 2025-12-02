package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;
import java.util.Map;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean verifyUser(String username, String providedPassword) throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    GameData createGame(GameData game) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    Map<Integer, GameData> listGames(String authToken) throws DataAccessException;
    void joinGame(GameData game) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
}
