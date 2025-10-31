package dataaccess;

import com.google.gson.Gson;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var stmt = conn.createStatement()) {
                stmt.executeUpdate("TRUNCATE TABLE auth");
                stmt.executeUpdate("TRUNCATE TABLE game");
                stmt.executeUpdate("TRUNCATE TABLE user");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database: " + e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
//        var statement = "INSERT INTO user (username, password) VALUES (?, ?)";
//        executeUpdate(statement, user.username(), user.password());
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, hashedPassword); // store hashed password, not clear text
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting user: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean verifyUser(String username, String providedPassword) throws DataAccessException{
        UserData user = getUser(username);
        if (user == null) {
            return false;
        }
        return BCrypt.checkpw(providedPassword, user.password());
    }


    @Override
    public void createAuth(AuthData auth) throws DataAccessException{
        var statement = "INSERT into auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        String json = new Gson().toJson(game);
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameJSON) VALUES (?, ?, ?, ?)";
        int id = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);
        return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        var statement = "SELECT gameJSON FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("gameJSON");
                    return new Gson().fromJson(json, GameData.class);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Map<Integer, GameData> listGames(String authToken) throws DataAccessException {
        if (getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        var result = new HashMap<Integer, GameData>();
        var statement = "SELECT gameID, gameJSON FROM game";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("gameID");
                GameData game = new Gson().fromJson(rs.getString("gameJSON"), GameData.class);
                result.put(id, game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void joinGame(GameData game) throws DataAccessException {
        String json = new Gson().toJson(game);
        var statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameJSON=? WHERE gameID=?";
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), json, game.gameID());
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param instanceof Integer p) { ps.setInt(i + 1, p); }
                    // else if (param instanceof PetType p) { ps.setString(i + 1, p.toString()); }
                    else if (param == null) { ps.setNull(i + 1, NULL); }
                }
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update database: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL PRIMARY KEY,
            password VARCHAR(255) NOT NULL
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL PRIMARY KEY,
            username VARCHAR(255) NOT NULL,
            FOREIGN KEY (username) references user(username) ON DELETE CASCADE
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL PRIMARY KEY,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255),
            gameJSON TEXT NOT NULL,
            FOREIGN KEY (whiteUsername) REFERENCES user(username) ON DELETE SET NULL,
            FOREIGN KEY (blackUsername) REFERENCES user(username) ON DELETE SET NULL
        )
        """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to configure database.");
            // throw new DataAccessException(DataAccessException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
