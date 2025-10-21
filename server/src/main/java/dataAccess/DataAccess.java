package dataAccess;

import model.AuthData;
import model.UserData;
import model.GameData;

public interface DataAccess {
    void clear();
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void createGame(GameData game) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;

    // Keep sticking more mini interfaces in to this MEGA interface
}
