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
        // System.out.println(">>> CLEARING DATABASE <<<");
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {

            // Disable FK checks so truncates won't fail
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            stmt.executeUpdate("TRUNCATE TABLE auth");
            stmt.executeUpdate("TRUNCATE TABLE game");
            stmt.executeUpdate("TRUNCATE TABLE user");

            // Re-enable FK checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

        } catch (SQLException e) {
            throw new DataAccessException("clearing database: " + e.getMessage());
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
                ps.setString(2, hashedPassword);
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("duplicate") ||
                    e.getMessage().toLowerCase().contains("unique")) {
                throw new DataAccessException("Username already taken", e);
            }
            throw new DataAccessException("inserting user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM user WHERE username=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("getting user: " + e.getMessage());
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
            throw new DataAccessException("getting auth: " + e.getMessage());
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
        // System.out.println(">>> DAO.createGame(): returned id=" + id);
        return new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        // System.out.println(">>> DAO.getGame(): checking gameID=" + gameID);
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJSON FROM game WHERE gameID=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("gameJSON");
                    GameData parsed = new Gson().fromJson(json, GameData.class);
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            parsed.game()
                    );
                } else {
                    // System.out.println(">>> DAO.getGame(): no game found for ID=" + gameID);
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: getting game: " + e.getMessage());
        }
    }


    @Override
    public Map<Integer, GameData> listGames(String authToken) throws DataAccessException {
        if (getAuth(authToken) == null) {
            throw new DataAccessException("unauthorized");
        }

        var result = new HashMap<Integer, GameData>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJSON FROM game";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("gameID");
                String json = rs.getString("gameJSON");
                GameData game = new Gson().fromJson(json, GameData.class);

                // ensure usernames match DB columns in case JSON is outdated
                game = new GameData(
                        id,
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game.game()
                );

                result.put(id, game);
            }
        } catch (SQLException e) {
            throw new DataAccessException("listing games: " + e.getMessage());
        }
        return result;
    }


    @Override
    public void joinGame(GameData game) throws DataAccessException {
        GameData existing = getGame(game.gameID());
        // System.out.println(">>> DAO.joinGame(): gameID=" + game.gameID());

        if (existing == null) {
            throw new DataAccessException("Error: bad request"); // invalid gameID
        }

        if (game.whiteUsername() != null && existing.whiteUsername() != null
                && !game.whiteUsername().equals(existing.whiteUsername())) {
            throw new DataAccessException("Error: already taken");
        }
        if (game.blackUsername() != null && existing.blackUsername() != null
                && !game.blackUsername().equals(existing.blackUsername())) {
            throw new DataAccessException("Error: already taken");
        }

        String white = game.whiteUsername() != null ? game.whiteUsername() : existing.whiteUsername();
        String black = game.blackUsername() != null ? game.blackUsername() : existing.blackUsername();

        // System.out.println(">>> DAO.joinGame(): writing white=" + white + ", black=" + black);

        String json = new Gson().toJson(new GameData(game.gameID(), white, black, game.gameName(), game.game()));
        var statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameJSON=? WHERE gameID=?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            ps.setString(1, white);
            ps.setString(2, black);
            ps.setString(3, json);
            ps.setInt(4, game.gameID());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0 && getGame(game.gameID()) == null) {
                throw new DataAccessException("Error: bad request");
            }

        } catch (SQLException e) {
            // System.out.println(">>> DAO.joinGame(): SQLException: " + e.getMessage());
            throw new DataAccessException("Error: updating game failed: " + e.getMessage());
        }
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
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("duplicate") || msg.contains("unique")) {
                throw new DataAccessException("unable to insert duplicate");
            } else if (msg.contains("foreign key")) {
                throw new DataAccessException("foreign key failure");
            }
            throw new DataAccessException("database failure: " + e.getMessage());
        }
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL
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
            gameID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
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
        // System.out.println("Starting configureDatabase...");

        // System.out.println("Attempting MySQL connection using URL: " + DatabaseManager.getConnectionUrl());

        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            // System.out.println("Connection successful: " + conn);
            // System.out.println("Database: " + DatabaseManager.getDatabaseName());

            for (String statement : createStatements) {
                // System.out.println("Executing SQL statement: " + statement);

                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
            // System.out.println("Database configuration complete!");
        } catch (SQLException ex) {
            // System.err.println("SQLException occurred: " + ex.getMessage());
            ex.printStackTrace();  // This will show the detailed reason for failure
            throw new DataAccessException("Unable to configure database.", ex);
        }    // throw new DataAccessException(DataAccessException.Code.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
    }
}