package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import org.junit.jupiter.api.Assertions;

public class UserServiceTests {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService();
    }


    @Test
    public void positiveRegisterTest() {
        RegisterRequest registerRequest = new RegisterRequest("owenlarson", "foobar", "owlar23@icstudents.org");

        RegisterResult actual = userService.register(registerRequest);

        Assertions.assertNull(actual.message());
        Assertions.assertNotNull(actual.authToken());
        Assertions.assertNotNull(actual.username());
        Assertions.assertEquals("owenlarson", actual.username());
        Assertions.assertEquals(200, actual.statusCode());

    }

    @Test
    public void badRequestRegisterTest() {
        RegisterRequest registerRequest = new RegisterRequest(null,"password", "a@o.org");

        RegisterResult actual = userService.register(registerRequest);

        Assertions.assertEquals("Error: bad request", actual.message());
        Assertions.assertEquals(400, actual.statusCode());
        Assertions.assertNull(actual.username());
        Assertions.assertNull(actual.authToken());
    }
}
