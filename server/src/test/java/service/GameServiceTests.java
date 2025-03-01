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
    RegisterRequest registerRequest;
    RegisterResult registerResult;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService();
        clearService.clear();
        registerRequest = new RegisterRequest("username", "password", "foor@bar");
        registerResult = userService.register(registerRequest);
    }

    @Test
    public void positiveCreateTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        CreateResult createResult = gameService.create(createRequest);

        Assertions.assertEquals(200, createResult.statusCode());
        Assertions.assertNull(createResult.message());
        Assertions.assertEquals(1, createResult.gameID());
    }

    @Test
    public void badRequestCreateTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("", registerResult.authToken());
        CreateResult createResult = gameService.create(createRequest);

        Assertions.assertNull(createResult.gameID());
        Assertions.assertEquals("Error: bad request", createResult.message());
        Assertions.assertEquals(400, createResult.statusCode());
    }

    @Test
    public void positiveListTest() throws DataAccessException {
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
        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        gameService.create(createRequest);

        ListRequest listRequest = new ListRequest("");
        ListResult listResult = gameService.list(listRequest);

        Assertions.assertNull(listResult.games());
        Assertions.assertEquals(401, listResult.statusCode());
        Assertions.assertEquals("Error: unauthorized", listResult.message());
    }

    @Test
    public void positiveJoinTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        CreateResult createResult = gameService.create(createRequest);

        JoinRequest joinRequest = new JoinRequest(registerResult.authToken(), "WHITE", createResult.gameID());
        JoinResult joinResult = gameService.join(joinRequest);

        Assertions.assertNull(joinResult.message());
        Assertions.assertEquals(200, joinResult.statusCode());
    }

    @Test
    public void badRequestJoinTest() throws DataAccessException {
        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        CreateResult createResult = gameService.create(createRequest);

        JoinRequest joinRequest = new JoinRequest(registerResult.authToken(), "", createResult.gameID());
        JoinResult joinResult = gameService.join(joinRequest);

        Assertions.assertEquals("Error: bad request", joinResult.message());
        Assertions.assertEquals(400, joinResult.statusCode());
    }
}
