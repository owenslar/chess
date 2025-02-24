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
    public AuthData getAuth(String username) throws DataAccessException {
        for (AuthData authData : auths) {
            if (authData.username().equals(username)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData a) throws DataAccessException {
        auths.removeIf(authData -> authData.equals(a));
    }

}
