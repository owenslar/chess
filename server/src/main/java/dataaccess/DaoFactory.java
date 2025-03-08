package dataaccess;

public class DaoFactory {
    private static final boolean USE_SQL = true;

    public static AuthDAO createAuthDAO() {
        return USE_SQL ? new SQLAuthDAO() : new MemoryAuthDAO();
    }

    public static UserDAO createUserDAO() {
        return USE_SQL ? new SQLUserDAO() : new MemoryUserDAO();
    }

    public static GameDAO createGameDAO() {
        return USE_SQL ? new SQLGameDAO() : new MemoryGameDAO();
    }
}
