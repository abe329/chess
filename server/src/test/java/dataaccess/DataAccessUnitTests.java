package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessUnitTests {
    @Test
    @DisplayName("Clear")
    public void testClear() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();

        dao.createUser(new UserData("beef", "1234", "sdf@ijhn.com"));
        assertNotNull(dao.getUser("beef"));
        dao.clear();
        assertNull(dao.getUser("beef"));
    }

    @Test
    @DisplayName("CreateUser - Positive")
    public void testCreateUser_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();

        dao.clear();
        UserData user = new UserData("abe", "mypassword", "a@gmail.com");
        dao.createUser(user);
        UserData retrievedUser = dao.getUser("abe"); // implement getUser to fetch UserData by username
        assertNotNull(retrievedUser);

        assertEquals("abe", retrievedUser.username());
        assertEquals("a@gmail.com", retrievedUser.email());

        assertTrue(BCrypt.checkpw("mypassword", retrievedUser.password()));
    }

    @Test
    @DisplayName("CreateUser - Negative")
    public void testCreateUser_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        UserData user1 = new UserData("abe", "mypassword", "a@gmail.com");
        dao.createUser(user1);
        UserData user2 = new UserData("abe", "dingalingaling", "123@gmail.com");

        DataAccessException thrown = assertThrows(DataAccessException.class, () -> {
            dao.createUser(user2);
        });

        assertTrue(thrown.getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("GetUser - Positive")
    public void testGetUser_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("There", "once", "was@a.ship"));
        UserData user = dao.getUser("There");

        assertNotNull(user);
        assertEquals("There", user.username());
    }

    @Test
    @DisplayName("GetUser - Negative")
    public void testGetUser_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        assertNull(dao.getUser("that"));
    }

    @Test
    @DisplayName("VerifyUser - Positive")
    public void testVerifyUser_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("set", "to", "sea@and.the"));
        assertTrue(dao.verifyUser("set", "to"));
    }

    @Test
    @DisplayName("VerifyUser - Negative")
    public void testVerifyUser_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("name", "of", "the@ship.was"));
        assertFalse(dao.verifyUser("name", "a"));
        assertFalse(dao.verifyUser("Billy", "of"));
    }

    @Test
    @DisplayName("ListGames - Positive")
    public void testListGames_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();
        dao.createUser(new UserData("Tea", "The", "winds@blew.up"));
        dao.createAuth(new AuthData("token123", "Tea"));

        dao.createGame(new GameData(0, "Tea", null, "Game1", new chess.ChessGame()));
        dao.createGame(new GameData(0, "Tea", null, "game2", new chess.ChessGame()));

        var games = dao.listGames("token123");
        assertEquals(2, games.size());
    }

    @Test
    @DisplayName("ListGames - Negative")
    public void testListGames_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.listGames("invalidToken");
        });

        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    @DisplayName("createAuth - Positive")
    public void testCreateAuth_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        // Create a user (auth has FK dependency)
        dao.createUser(new UserData("him", "abc123", "sail@sea.com"));

        AuthData auth = new AuthData("tokenABC", "him");
        dao.createAuth(auth);

        // Retrieve it
        AuthData retrieved = dao.getAuth("tokenABC");

        assertNotNull(retrieved, "Auth record should exist after creation");
        assertEquals("tokenABC", retrieved.authToken());
        assertEquals("him", retrieved.username());
    }

    @Test
    @DisplayName("createAuth - Negative")
    public void testCreateAuth_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("zuckerberg", "gime", "c@ship.com"));

        AuthData auth1 = new AuthData("dupToken", "zuckerberg");
        dao.createAuth(auth1);

        AuthData auth2 = new AuthData("dupToken", "zuckerberg");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.createAuth(auth2);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("unable"),
                "Expected an error indicating duplicate token insertion failed");
    }

    @Test
    @DisplayName("getAuth - Positive")
    public void testGetAuth_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("kanye", "west", "mate@ship.com"));
        dao.createAuth(new AuthData("tokenXYZ", "kanye"));

        AuthData retrieved = dao.getAuth("tokenXYZ");
        assertNotNull(retrieved);
        assertEquals("tokenXYZ", retrieved.authToken());
        assertEquals("kanye", retrieved.username());
    }


    @Test
    @DisplayName("GetAuth - Negative")
    public void testGetAuth_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        assertNull(dao.getAuth("opnunToken"));
    }

    @Test
    @DisplayName("DeleteAuth - Positive")
    public void testDeleteAuth_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("lime", "password", "l@gmail.com"));
        dao.createAuth( new AuthData("token123", "lime"));

        dao.deleteAuth("token123");

        assertNull(dao.getAuth("token123"));
    }

    @Test
    @DisplayName("deleteAuth - Negative")
    public void testDeleteAuth_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        assertNull(dao.getAuth("ghostToken"));

        assertDoesNotThrow(() -> dao.deleteAuth("ghostToken"),
                "Deleting a nonexistent token should not throw an exception");

        assertNull(dao.getAuth("ghostToken"));
    }


    @Test
    @DisplayName("createGame - Positive")
    public void testCreateGame_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("white", "pass", "white@game.com"));
        dao.createUser(new UserData("black", "pass", "black@game.com"));

        GameData game = new GameData(0, "white", "black", "EpicBattle", new chess.ChessGame());
        GameData created = dao.createGame(game);

        assertNotNull(created);
        assertTrue(created.gameID() > 0, "Game ID should be auto-generated");

        GameData retrieved = dao.getGame(created.gameID());
        assertNotNull(retrieved, "Game should be retrievable after creation");
        assertEquals("EpicBattle", retrieved.gameName());
    }

    @Test
    @DisplayName("createGame - Negative")
    public void testCreateGame_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        // This user doesn’t exist in user table
        GameData badGame = new GameData(0, "ghost", null, "PhantomGame", new chess.ChessGame());

        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.createGame(badGame);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("unable") ||
                        ex.getMessage().toLowerCase().contains("foreign"),
                "Expected FK constraint failure or general DB error");
    }

    @Test
    @DisplayName("getGame - Positive")
    public void testGetGame_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("w", "123", "w@w.com"));
        dao.createUser(new UserData("b", "123", "b@b.com"));

        GameData game = dao.createGame(new GameData(0, "w", "b", "RetrieveMe", new chess.ChessGame()));

        GameData retrieved = dao.getGame(game.gameID());
        assertNotNull(retrieved);
        assertEquals("RetrieveMe", retrieved.gameName());
        assertEquals("w", retrieved.whiteUsername());
    }


    @Test
    @DisplayName("GetGame - Negative")
    public void testGetGame_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        assertNull(dao.getGame(999), "Should return null for nonexistent gameID");
    }

    @Test
    @DisplayName("JoinGame - Positive")
    public void testJoinGame_Positive() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("her", "bow", "dipped@down.blow"));
        dao.createUser(new UserData("my", "bully", "boys@bow.soon"));

        GameData game = dao.createGame(new GameData(0, "her", null, "Joinable", new chess.ChessGame()));

        GameData updated = new GameData(game.gameID(), "her", "my", "Joinable", game.game());
        dao.joinGame(updated);

        GameData retrieved = dao.getGame(game.gameID());
        assertEquals("my", retrieved.blackUsername());

    }

    @Test
    @DisplayName("joinGame - Negative (invalid game ID)")
    public void testJoinGame_Negative() throws Exception {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createUser(new UserData("player1", "123", "p1@email.com"));

        // Try to join a game that doesn’t exist
        DataAccessException ex = assertThrows(DataAccessException.class, () -> {
            dao.joinGame(new GameData(999, "player1", "player2", "WHITE", new chess.ChessGame()));
        });

        assertTrue(ex.getMessage().toLowerCase().contains("not found") ||
                ex.getMessage().toLowerCase().contains("full"));
    }

}
