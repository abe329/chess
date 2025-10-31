package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: already taken");
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public boolean verifyUser(String username, String providedPassword) {
        UserData user = users.get(username);
        if (user == null) {
            return false;
        }
        return user.password().equals(providedPassword);
    }


    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        auths.remove(authToken);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
        return game;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        return game;
    }

    public Map<Integer, GameData> listGames(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return games;
    }

    @Override
    public void joinGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Error: bad request");
        }
        games.put(game.gameID(), game);
    }
}
