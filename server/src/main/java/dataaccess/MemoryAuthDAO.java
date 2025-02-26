package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO extends AuthDAO {
    private static final ArrayList<AuthData> AUTHS = new ArrayList<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        AUTHS.add(a);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : AUTHS) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public AuthData getAuthByUsername(String username) throws DataAccessException {
        for (AuthData authData : AUTHS) {
            if (authData.username().equals(username)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AUTHS.removeIf(authData -> authData.authToken().equals(authToken));
    }

}
