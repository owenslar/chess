package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.*;

public class GameServiceTests {

    GameService gameService;
    UserService userService;
    ClearService clearService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService();
        clearService.clear();
    }

    @Test
    public void positiveCreateTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "foor@bar");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        CreateResult createResult = gameService.create(createRequest);

        Assertions.assertEquals(200, createResult.statusCode());
        Assertions.assertNull(createResult.message());
        Assertions.assertEquals(1, createResult.gameID());
    }

    @Test
    public void badRequestCreateTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateRequest createRequest = new CreateRequest("", registerResult.authToken());
        CreateResult createResult = gameService.create(createRequest);

        Assertions.assertNull(createResult.gameID());
        Assertions.assertEquals("Error: bad request", createResult.message());
        Assertions.assertEquals(400, createResult.statusCode());
    }

    @Test
    public void positiveListTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "foor@bar");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        gameService.create(createRequest);

        ListRequest listRequest = new ListRequest(registerResult.authToken());
        ListResult listResult = gameService.list(listRequest);

        Assertions.assertNotNull(listResult.games());
        Assertions.assertEquals("newGame", listResult.games().getFirst().gameName());
        Assertions.assertEquals(200, listResult.statusCode());
        Assertions.assertNull(listResult.message());
    }

    @Test
    public void emptyAuthTokenListRequestTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "foor@bar");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        gameService.create(createRequest);

        ListRequest listRequest = new ListRequest("");
        ListResult listResult = gameService.list(listRequest);

        Assertions.assertNull(listResult.games());
        Assertions.assertEquals(401, listResult.statusCode());
        Assertions.assertEquals("Error: unauthorized", listResult.message());
    }
}
