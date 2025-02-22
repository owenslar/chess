package dataaccess;

import model.AuthData;
import java.util.UUID;

public abstract class AuthDAO {

    abstract void createAuth(AuthData authData) throws DataAccessException;

    abstract AuthData getAuth(String authToken) throws DataAccessException;

    abstract void deleteAuth(AuthData a) throws DataAccessException;

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
