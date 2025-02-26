package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.*;
import org.junit.jupiter.api.Assertions;

public class UserServiceTests {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }


    @Test
    public void positiveRegisterTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("owenlarson", "foobar", "owlar23@icstudents.org");

        RegisterResult actual = userService.register(registerRequest);

        Assertions.assertNull(actual.message());
        Assertions.assertNotNull(actual.authToken());
        Assertions.assertNotNull(actual.username());
        Assertions.assertEquals("owenlarson", actual.username());
        Assertions.assertEquals(200, actual.statusCode());
    }

    @Test
    public void badRequestRegisterTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest(null,"password", "a@o.org");

        RegisterResult actual = userService.register(registerRequest);

        Assertions.assertEquals("Error: bad request", actual.message());
        Assertions.assertEquals(400, actual.statusCode());
        Assertions.assertNull(actual.username());
        Assertions.assertNull(actual.authToken());
    }

    @Test
    public void registerSameUserTwiceTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("owen", "foo", "o@gmail.com");

        userService.register(registerRequest);

        RegisterResult actual = userService.register(registerRequest);

        Assertions.assertEquals("Error: already taken", actual.message());
        Assertions.assertEquals(403, actual.statusCode());
        Assertions.assertNull(actual.username());
        Assertions.assertNull(actual.authToken());
    }

    @Test
    public void positiveLoginRequest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("owenlarson", "foobar", "owlar23@icstudents.org");
        RegisterResult registerResult = userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("owenlarson", "foobar");
        LoginResult loginResult = userService.login(loginRequest);

        Assertions.assertEquals(200, loginResult.statusCode());
        Assertions.assertEquals(registerResult.authToken(), loginResult.authToken());
        Assertions.assertEquals(registerResult.username(), loginResult.username());
        Assertions.assertNull(loginResult.message());
    }

    @Test
    public void nonExistentUserTest() throws DataAccessException {
        LoginRequest loginRequest = new LoginRequest("Non-existent-User", "password");
        LoginResult loginResult = userService.login(loginRequest);

        Assertions.assertEquals(401, loginResult.statusCode());
        Assertions.assertEquals("Error: unauthorized", loginResult.message());
        Assertions.assertNull(loginResult.username());
        Assertions.assertNull(loginResult.authToken());
    }

    @Test
    public void wrongPasswordTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("owen", "larson", "o@bar.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("owen", "lars");
        LoginResult loginResult = userService.login(loginRequest);

        Assertions.assertEquals(401, loginResult.statusCode());
        Assertions.assertEquals("Error: unauthorized", loginResult.message());
        Assertions.assertNull(loginResult.username());
        Assertions.assertNull(loginResult.authToken());
    }

    @Test
    public void positiveLogoutTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("owen", "larson", "owen@gmail");
        RegisterResult registerResult = userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult logoutResult = userService.logout(logoutRequest);

        Assertions.assertEquals(200, logoutResult.statusCode());
        Assertions.assertNull(logoutResult.message());
    }

    @Test
    public void emptyAuthTokenTest() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "email");
        userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest("");
        LogoutResult logoutResult = userService.logout(logoutRequest);

        Assertions.assertEquals(401, logoutResult.statusCode());
        Assertions.assertEquals("Error: unauthorized", logoutResult.message());
    }
}
