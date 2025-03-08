package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAOTests {

    UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
        userDAO = DaoFactory.createUserDAO();
    }

    @Test
    public void createUserPositiveTest() throws DataAccessException {
        UserData user = new UserData("Tusername", "Tpassword", "Temail");
        userDAO.createUser(user);

        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username = 'Tusername'");

                Assertions.assertTrue(rs.next(), "User should be inserted into the database");
                Assertions.assertEquals("Tusername", rs.getString("username"));
                Assertions.assertEquals("Tpassword", rs.getString("password"));
                Assertions.assertEquals("Temail", rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Test
    public void nullEmailTest() {
        UserData user = new UserData("testUsername", "testPassword", null);
        try {
            userDAO.createUser(user);
            Assertions.fail("Expected SQLException due to NOT NULL constraint");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Column 'email' cannot be null", e.getMessage());
        }
    }

    @AfterEach
    public void cleanUp() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM users WHERE username = 'Tusername'");
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
