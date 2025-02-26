package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO extends AuthDAO {
    private static final ArrayList<AuthData> auths = new ArrayList<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        auths.add(a);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : auths) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        for (AuthData authData : auths) {
            if (authData.username().equals(username)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.removeIf(authData -> authData.authToken().equals(authToken));
    }

}
