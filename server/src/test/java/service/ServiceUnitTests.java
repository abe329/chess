package service;

import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import model.requestsandresults.CreateGameRequest;
import model.requestsandresults.JoinGameRequest;
import model.requestsandresults.LoginRequest;
import model.requestsandresults.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceUnitTests {
    @Test
    @Order(1)
    @DisplayName("Clear - Positive")
    public void testClearPositive() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        var reg = userService.register(new RegisterRequest("Lordgarmadon", "pw", "gd45@mail.com"));
        String token = reg.authToken();
        gameService.createGame(new CreateGameRequest(token, "Chess Clear"));

        dao.clear();

        assertNull(dao.getUser("Lordgarmadon"));
        assertNull(dao.getAuth(token));
    }


    @Test
    @Order(2)
    @DisplayName("Register - Positive")
    public void testRegisterPositive() throws Exception {
        var dao = new MemoryDataAccess();
        var service = new UserService(dao);

        var request = new RegisterRequest("LeeJensen", "password", "ljensen@email.com");
        var result = service.register(request);

        assertNotNull(result);
        assertEquals("LeeJensen", result.username());
        assertNotNull(result.authToken());

        // Verify the user was stored in the DB
        var storedUser = dao.getUser("LeeJensen");
        assertNotNull(storedUser);
    }

    @Test
    @Order(3)
    @DisplayName("Register - Negative")
    public void testRegisterNegativeAlreadyTaken() throws Exception {
        var dao = new MemoryDataAccess();
        var service = new UserService(dao);

        var request1 = new RegisterRequest("LeeJensen", "password", "ljensen@email.com");
        service.register(request1);  // First register succeeds

        var request2 = new RegisterRequest("LeeJensen", "newpass", "ljensen@email.com");

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            service.register(request2);
        });

        assertTrue(thrown.getMessage().contains("already taken"));
    }

    @Test
    @Order(4)
    @DisplayName("Login - Positive")
    public void testLoginPositive() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);

        // Register first
        userService.register(new RegisterRequest("Sam", "pass", "s@mail.com"));

        var loginResult = userService.login(new LoginRequest("Sam", "pass"));

        assertNotNull(loginResult);
        assertEquals("Sam", loginResult.username());
        assertNotNull(loginResult.authToken());

        // Check auth is stored
        assertNotNull(dao.getAuth(loginResult.authToken()));
    }

    @Test
    @Order(5)
    @DisplayName("Login - Negative")
    public void testLoginNegativeWrongPassword() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);

        userService.register(new RegisterRequest("blob", "secret", "blob@mail.com"));

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            userService.login(new LoginRequest("blob", "wrongpass"));
        });

        assertTrue(thrown.getMessage().contains("unauthorized"));
    }

    @Test
    @Order(6)
    @DisplayName("Logout - Positive")
    public void testLogoutPositive() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);

        var reg = userService.register(new RegisterRequest("michaelscott", "pw", "ms@mail.com"));
        String token = reg.authToken();

        var result = userService.logout(token);
        assertNotNull(result);

        // Auth should be removed
        assertNull(dao.getAuth(token));
    }

    @Test
    @Order(7)
    @DisplayName("Logout - Negative")
    public void testLogoutNegativeUnauthorized() {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            userService.logout("invalid-token");
        });

        assertTrue(thrown.getMessage().contains("unauthorized"));
    }

    @Test
    @Order(8)
    @DisplayName("CreateGame - Positive")
    public void testCreateGamePositive() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        // Register user so we have a valid auth token
        var reg = userService.register(new RegisterRequest("gorm", "pass", "gorm@mail.com"));
        String auth = reg.authToken();

        // Act
        var result = gameService.createGame(new CreateGameRequest(auth, "My Game"));

        // Assert basic result
        assertNotNull(result);
        assertNotNull(result.gameID());
        assertEquals("My Game", dao.getGame(result.gameID()).gameName());

        // Ensure the game is actually stored
        var storedGame = dao.getGame(result.gameID());
        assertNotNull(storedGame);
    }

    @Test
    @Order(9)
    @DisplayName("CreateGame - Negative")
    public void testCreateGameNegativeUnauthorized() {
        var dao = new MemoryDataAccess();
        var gameService = new GameService(dao);

        String invalidAuth = "fake-token";

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            gameService.createGame(new CreateGameRequest(invalidAuth, "Should Fail"));
        });

        assertTrue(thrown.getMessage().contains("unauthorized"));
    }

    @Test
    @Order(10)
    @DisplayName("ListGames - Positive")
    public void testListGamesPositive() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        var reg = userService.register(new RegisterRequest("Richie", "pw", "Saunders@mail.com"));
        String token = reg.authToken();

        gameService.createGame(new CreateGameRequest(token, "Chess 1"));
        gameService.createGame(new CreateGameRequest(token, "Chess 2"));

        var games = gameService.listGames(token);
        assertNotNull(games);
    }

    @Test
    @Order(11)
    @DisplayName("ListGames - Negative")
    public void testListGamesNegativeUnauthorized() {
        var dao = new MemoryDataAccess();
        var gameService = new GameService(dao);

        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            gameService.listGames("fake-token");
        });

        assertTrue(thrown.getMessage().contains("unauthorized"));
    }

    @Test
    @Order(12)
    @DisplayName("JoinGame - Positive")
    public void testJoinGamePositive() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        // Arrange: Register user, create auth, and create a game
        var reg = userService.register(new RegisterRequest("gorm", "pass", "g@mail.com"));
        var create = gameService.createGame(new CreateGameRequest(reg.authToken(), "game1"));

        // Act: User joins as WHITE

        gameService.joinGame(reg.authToken(), new JoinGameRequest("WHITE", create.gameID()));

        // Assert: Check DB to ensure white player is set
        var game = dao.getGame(create.gameID());
        assertEquals("gorm", game.whiteUsername());
    }

    @Test
    @Order(13)
    @DisplayName("JoinGame - Negative")
    public void testJoinGameNegativeSpotTaken() throws Exception {
        var dao = new MemoryDataAccess();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        // Register two users
        var p1 = userService.register(new RegisterRequest("AJ", "pass", "aj@mail.com"));
        var p2 = userService.register(new RegisterRequest("Djbantsa", "pass", "d@mail.com"));

        // Create game & first user joins as BLACK
        var create = gameService.createGame(new CreateGameRequest(p1.authToken(), "game1"));
        gameService.joinGame(p1.authToken(), new JoinGameRequest("BLACK", create.gameID()));

        // Second user tries to join BLACK again
        ServiceException thrown = assertThrows(ServiceException.class, () -> {
            gameService.joinGame(p2.authToken(), new JoinGameRequest("BLACK", create.gameID()));
        });

        assertTrue(thrown.getMessage().contains("already taken"));
    }

}
