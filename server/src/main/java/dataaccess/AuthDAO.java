package dataaccess;

import model.AuthData;
import java.util.UUID;

public abstract class AuthDAO {

    public abstract void createAuth(AuthData authData) throws DataAccessException;

    public abstract AuthData getAuth(String authToken) throws DataAccessException;

    public abstract AuthData getAuthByUsername(String username) throws DataAccessException;

    public abstract void deleteAuth(String authToken) throws DataAccessException;

    public abstract void clear() throws DataAccessException;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
