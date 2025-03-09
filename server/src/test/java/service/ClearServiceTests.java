package service;

import dataaccess.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.*;

public class ClearServiceTests {

    ClearService clearService;
    UserService userService;
    GameService gameService;
    AuthDAO authDAO;
    GameDAO gameDAO;
    UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        authDAO = DaoFactory.createAuthDAO();
        userDAO = DaoFactory.createUserDAO();
        gameDAO = DaoFactory.createGameDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    @Test
    public void positiveClearTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateRequest createRequest = new CreateRequest("newGame", registerResult.authToken());
        gameService.create(createRequest);

        ClearResult clearResult = clearService.clear();

        Assertions.assertEquals(200, clearResult.statusCode());
        Assertions.assertNull(clearResult.message());
        Assertions.assertNull(userDAO.getUser("username"));
        Assertions.assertNull(authDAO.getAuth(registerResult.authToken()));
        Assertions.assertNull(gameDAO.getGame(1));
    }

    @AfterEach
    public void cleanUpDB() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            Assertions.fail("failed to clean up DB");
        }
    }
}
