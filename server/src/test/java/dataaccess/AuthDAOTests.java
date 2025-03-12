package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthDAOTests {

    AuthDAO authDAO;
    AuthData auth1;
    AuthData auth2;
    AuthData badAuth;

    @BeforeAll
    public static void configureDB() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    @BeforeEach
    public void setUp() {
        authDAO = DaoFactory.createAuthDAO();
        auth1 = new AuthData("testAuthToken1", "testUsername1");
        auth2 = new AuthData("testAuthToken2", "testUsername2");
        badAuth = new AuthData(null, "badAuthUsername");

    }

    @Test
    public void positiveCreateAuthTest() {
        try {
            authDAO.createAuth(auth1);
        } catch (DataAccessException e) {
            Assertions.fail("caught unexpected DAE");
        }

        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT * FROM auths WHERE username = 'testUsername1'");

                Assertions.assertTrue(rs.next(), "Auth should be inserted into the database");
                Assertions.assertEquals("testUsername1", rs.getString("username"));
                Assertions.assertEquals("testAuthToken1", rs.getString("authToken"));
            }
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("caught unexpected exception");
        }
    }

    @Test
    public void nullAuthTokenTest() {
        try {
            authDAO.createAuth(badAuth);
            Assertions.fail("Expected SQLException due to NOT NULL constraint");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Column 'authToken' cannot be null", e.getMessage());
        }
    }

    @Test
    public void positiveGetAuthTest() {
        try {
            authDAO.createAuth(auth1);

            AuthData actualAuth = authDAO.getAuth(auth1.authToken());

            Assertions.assertEquals(actualAuth.username(), auth1.username());
            Assertions.assertEquals(actualAuth.authToken(), auth1.authToken());
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void negativeGetAuthTest() {
        try {
            AuthData actualAuth = authDAO.getAuth("nonExistentAuthToken");
            Assertions.assertNull(actualAuth, "Expected null for non-existent auth");
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void positiveDeleteAuthTest() {
        try {
            authDAO.createAuth(auth1);
            authDAO.createAuth(auth2);

            authDAO.deleteAuth(auth2.authToken());

            int authCount = getAuthCount();

            Assertions.assertEquals(1, authCount);
        } catch (DataAccessException e) {
            Assertions.fail("caught DAE");
        }
    }

    @Test
    public void deleteNotExistentAuthTest() {
        try {
            authDAO.createAuth(auth1);

            int countBeforeDelete = getAuthCount();

            authDAO.deleteAuth("nonExistentAuthToken");

            int countAfterDelete = getAuthCount();

            Assertions.assertEquals(countBeforeDelete, countAfterDelete);
        } catch (DataAccessException e) {
            Assertions.fail("Caught a DAE");
        }
    }

    @Test
    public void clearTest() {

        try {
            authDAO.createAuth(auth1);
            authDAO.createAuth(auth2);

            authDAO.clear();
        } catch (DataAccessException e) {
            Assertions.fail("Caught an unexpected DataAccessException");
        }

        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM auths");
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
    public void cleanUp() {
        try (var conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("""
                DELETE FROM auths
                WHERE authToken IN ('testAuthToken1', 'testAuthToken2')
            """)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Caught an exception when cleaning up: " + e.getMessage());
        }
    }

    private int getAuthCount() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT COUNT(*) FROM auths";
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return 0;
    }
}
