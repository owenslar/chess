package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAOTests {

    UserDAO userDAO;
    UserData user;

    @BeforeEach
    public void setUp() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
        userDAO = DaoFactory.createUserDAO();
        user = new UserData("testUsername", "testPassword", "testEmail");
    }

    @Test
    public void createUserPositiveTest() throws DataAccessException {
        try {
            userDAO.createUser(user);
        } catch (DataAccessException e) {
            Assertions.fail("caught unexpected DAE");
        }

        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username = 'testUsername'");

                Assertions.assertTrue(rs.next(), "User should be inserted into the database");
                Assertions.assertEquals("testUsername", rs.getString("username"));
                Assertions.assertEquals("testPassword", rs.getString("password"));
                Assertions.assertEquals("testEmail", rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void positiveGetUserTest() {
        try {
            userDAO.createUser(user);

            UserData actualUser = userDAO.getUser(user.username());

            Assertions.assertEquals(actualUser.username(), user.username());
            Assertions.assertEquals(actualUser.password(), user.password());
            Assertions.assertEquals(actualUser.email(), user.email());
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void negativeGetUserTest() {
        try {
            UserData actualUser = userDAO.getUser("nonExistentUsername");
            Assertions.assertNull(actualUser, "Expected null for non-existent user");
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void nullEmailTest() {
        user = new UserData("testUsername", "testPassword", null);
        try {
            userDAO.createUser(user);
            Assertions.fail("Expected SQLException due to NOT NULL constraint");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Column 'email' cannot be null", e.getMessage());
        }
    }

    @Test
    public void clearTest() {
        UserData user1 = new UserData("test1user", "test1pass", "test1email");
        UserData user2 = new UserData("test2user", "test2pass", "test2email");

        try {
            userDAO.createUser(user1);
            userDAO.createUser(user2);

            userDAO.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Caught an unexpected DataAccessException");
        }

        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) {
                    int count = rs.getInt(1);
                    Assertions.assertEquals(0, count);
                }
            }
        } catch (SQLException e) {
            Assertions.fail("Caught unexpected SQL exception");
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE exception");
        }
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("TRUNCATE users");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
