package dataaccess;

import model.AuthData;

import java.util.*;

public class MemoryAuthDAO extends AuthDAO {
    private static final HashMap<String, List<String>> AUTHS = new HashMap<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        AUTHS.computeIfAbsent(a.username(), k -> new ArrayList<>()).add(a.authToken());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (Map.Entry<String, List<String>>  entry : AUTHS.entrySet()) {
            if (entry.getValue().contains(authToken)) {
                return new AuthData(authToken, entry.getKey());
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        for (Map.Entry<String, List<String>> entry : AUTHS.entrySet()) {
            if (entry.getValue().remove(authToken)) {
                break;
            }
        }
    }

    @Override
    public void clear() throws DataAccessException {
        AUTHS.clear();
    }

}
