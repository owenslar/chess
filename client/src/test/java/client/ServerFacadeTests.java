package client;

import org.junit.jupiter.api.*;
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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void positiveRegisterTest() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "testEmail");

        RegisterResult registerResult = serverFacade.register(registerRequest);

        Assertions.assertEquals("testUser", registerResult.username());
        Assertions.assertNotNull(registerResult.authToken());
        Assertions.assertNull(registerResult.message());
    }

}
