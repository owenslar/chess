package dataaccess;

public class DaoFactory {
    private static final boolean USE_SQL = false;

    public static AuthDAO createAuthDAO() {
        return USE_SQL ? new SQLAuthDAO() : new MemoryAuthDAO();
    }

    public static UserDAO createUserDAO() {
        return USE_SQL ? new SQLUserDAO() : new MemoryUserDAO();
    }
}
