package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.fail;

public class UserServiceTests {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }


    @Test
    public void positiveRegisterTest() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("owenlarson", "foobar", "owlar23@icstudents.org");

            RegisterResult actual = userService.register(registerRequest);

            Assertions.assertNull(actual.message());
            Assertions.assertNotNull(actual.authToken());
            Assertions.assertNotNull(actual.username());
            Assertions.assertEquals("owenlarson", actual.username());
            Assertions.assertEquals(200, actual.statusCode());
        } catch (DataAccessException e) {
            fail("DataAccessException was caught");
        }

    }

    @Test
    public void badRequestRegisterTest() {
        try {
            RegisterRequest registerRequest = new RegisterRequest(null,"password", "a@o.org");

            RegisterResult actual = userService.register(registerRequest);

            Assertions.assertEquals("Error: bad request", actual.message());
            Assertions.assertEquals(400, actual.statusCode());
            Assertions.assertNull(actual.username());
            Assertions.assertNull(actual.authToken());
        } catch (DataAccessException e) {
            fail("DataAccessException was caught");
        }
    }

    @Test
    public void registerSameUserTwiceTest() {
        try {
            RegisterRequest registerRequest = new RegisterRequest("owen", "foo", "o@gmail.com");

            userService.register(registerRequest);

            RegisterResult actual = userService.register(registerRequest);

            Assertions.assertEquals("Error: already taken", actual.message());
            Assertions.assertEquals(403, actual.statusCode());
            Assertions.assertNull(actual.username());
            Assertions.assertNull(actual.authToken());
        } catch (DataAccessException e) {
            fail("DataAccessException was caught");
        }
    }
}
