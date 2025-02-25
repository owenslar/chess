package dataaccess;

public class DaoFactory {
    private static final boolean use_SQL = false;

    public static AuthDAO createAuthDAO() {
        return use_SQL ? new SQLAuthDAO() : new MemoryAuthDAO();
    }

    public static UserDAO createUserDAO() {
        return use_SQL ? new SQLUserDAO() : new MemoryUserDAO();
    }
}
