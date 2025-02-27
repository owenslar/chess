package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.CreateRequest;
import requestresult.CreateResult;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;

public class GameServiceTests {

    GameService gameService;
    UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
        gameService = new GameService();
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
}
