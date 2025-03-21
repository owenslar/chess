package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestresult.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private RegisterRequest registerRequest;
    private RegisterRequest badRegisterRequest;
    private LoginRequest loginRequest;
    private LoginRequest badLoginRequest;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clear();
    }

    @BeforeEach
    public void declareReqRes() {
        registerRequest = new RegisterRequest("testUser", "testPass", "testEmail");
        badRegisterRequest = new RegisterRequest(null, "testPass", "testEmail");
        loginRequest = new LoginRequest("testUser", "testPass");
        badLoginRequest = new LoginRequest("testUser", "wrongPass");
    }

    @AfterEach
    public void clearDb() {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void clearTest() {
        ClearResult clearResult = serverFacade.clear();
        Assertions.assertNull(clearResult.message());
    }

    @Test
    public void positiveRegisterTest() {
        RegisterResult registerResult = serverFacade.register(registerRequest);

        Assertions.assertEquals("testUser", registerResult.username());
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertNull(registerResult.message());
    }

    @Test
    public void negativeRegisterTest() {
        try {
            serverFacade.register(badRegisterRequest);
            Assertions.fail("Expected to catch an error");
        } catch (ResponseException e) {
            Assertions.assertEquals("Error: bad request", e.getMessage());
            Assertions.assertEquals(400, e.getStatusCode());
        }
    }

    @Test
    public void positiveLoginTest() {
        try {
            serverFacade.register(registerRequest);
            LoginResult loginResult = serverFacade.login(loginRequest);

            Assertions.assertEquals("testUser", loginResult.username());
            Assertions.assertNotNull(loginResult.authToken());
            Assertions.assertTrue(loginResult.authToken().length() > 10);
        } catch (ResponseException e) {
            Assertions.fail("caught an unexpected exception");
        }
    }

    @Test
    public void negativeLoginTest() {
        try {
            serverFacade.register(registerRequest);
            serverFacade.login(badLoginRequest);
            Assertions.fail("Expected an error that wasn't thrown");
        } catch (ResponseException e) {
            Assertions.assertEquals("Error: unauthorized", e.getMessage());
            Assertions.assertEquals(401, e.getStatusCode());
        }
    }


}
