package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestresult.ClearResult;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clear();
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
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "testEmail");

        RegisterResult registerResult = serverFacade.register(registerRequest);

        Assertions.assertEquals("testUser", registerResult.username());
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertNull(registerResult.message());
    }

    @Test
    public void negativeRegisterTest() {
        RegisterRequest registerRequest = new RegisterRequest(null, "testPass", "testEmail");
        try {
            serverFacade.register(registerRequest);
            Assertions.fail("Expected to catch an error");
        } catch (ResponseException e) {
            Assertions.assertEquals("Error: bad request", e.getMessage());
            Assertions.assertEquals(400, e.getStatusCode());
        }
    }



}
