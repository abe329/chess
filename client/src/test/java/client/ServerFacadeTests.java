package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ClientException;
import ui.ServerFacade;
import model.requestsandresults.*;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }




    @Test
    @DisplayName("Register - trial")
    void register() throws Exception {
        var request = new RegisterRequest("player1", "password", "p1@email.com");
        var authData = facade.register(request);
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @DisplayName("Register - Positive")
    public void registerPositive() throws Exception {
        var request = new RegisterRequest("haein", "password", "alice@email.com");
        var result = facade.register(request);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("haein", result.username());
    }

    @Test
    @DisplayName("Register - Negative")
    public void registerNegative() throws Exception {
        var request = new RegisterRequest("bob", "pw", "b@mail.com");
        facade.register(request);

        Assertions.assertThrows(ClientException.class, () -> {
            facade.register(request);  // second time should fail
        });
    }

    @Test
    @DisplayName("Login - Positive")
    public void loginPositive() throws Exception {
        facade.register(new RegisterRequest("h*ck", "pw", "j@mail.com"));

        var result = facade.login(new LoginRequest("h*ck", "pw"));

        Assertions.assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("Login - Negative")
    public void loginNegative() throws Exception {
        facade.register(new RegisterRequest("johnpork", "greatpw", "jj@mail.com"));

        Assertions.assertThrows(ClientException.class, () -> {
            facade.login(new LoginRequest("johnpork", "horriblepw")); // wrong password obviously, because it's names wrongpw
        });
    }

    @Test
    @DisplayName("Logout - Positive")
    public void logoutPositive() throws Exception {
        var user = facade.register(new RegisterRequest("sara", "pass", "s@mail.com"));
        var auth = user.authToken();

        var result = facade.logout(auth);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Logout - Negative")
    public void logoutNegative() {
        Assertions.assertThrows(ClientException.class, () -> {
            facade.logout("doesn'texisthahaha");
        });
    }

    @Test
    @DisplayName("CreateGame - Positive")
    public void createGamePositive() throws Exception {
        var auth = facade.register(new RegisterRequest("gorm", "pw", "g@mail.com")).authToken();
        var request = new CreateGameRequest(auth, "myGame");

        var result = facade.createGame(request);

        Assertions.assertTrue(result.gameID() > 0);
    }

    @Test
    @DisplayName("CreateGame - Negative")
    public void createGameNegative() throws Exception {
        var auth = facade.register(new RegisterRequest("gime", "pw", "g@mail.com")).authToken();
        var request = new CreateGameRequest(auth, null);  // name missing

        Assertions.assertThrows(ClientException.class, () -> {
            facade.createGame(request);
        });
    }

    @Test
    @DisplayName("ListGames - Positive")
    public void listGamesPositive() throws Exception {
        var auth = facade.register(new RegisterRequest("lime", "pw", "l@mail.com")).authToken();
        facade.createGame(new CreateGameRequest(auth, "game1"));
        facade.createGame(new CreateGameRequest(auth, "game2"));

        var result = facade.listGames(new ListGamesRequest(auth));

        Assertions.assertTrue(result.games().size() >= 2);
    }

    @Test
    @DisplayName("ListGames - Negative")
    public void listGamesNegative_invalidAuth() {
        Assertions.assertThrows(ClientException.class, () -> {
            facade.listGames(new ListGamesRequest("badauth"));
        });
    }

    @Test
    @DisplayName("joinGame - Positive")
    public void joinGamePositive() throws Exception {
        var auth = facade.register(new RegisterRequest("kate", "pw", "k@mail.com")).authToken();
        var game1 = facade.createGame(new CreateGameRequest(auth, "joinTest"));

        var request = new JoinGameRequest("WHITE", game1.gameID());

        var result = facade.joinGame(request);

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("joinGame - Negative")
    public void joinGameNegative_badColor() throws Exception {
        var auth = facade.register(new RegisterRequest("tom", "pw", "t@mail.com")).authToken();
        var game = facade.createGame(new CreateGameRequest(auth, "badColorGame"));

        var request = new JoinGameRequest("PURPLE", game.gameID()); // invalid

        Assertions.assertThrows(ClientException.class, () -> {
            facade.joinGame(request);
        });
    }


}
