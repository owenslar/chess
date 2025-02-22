package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static final ArrayList<AuthData> auths = new ArrayList<>();

    @Override
    public AuthData createAuth(String username) {
        String authToken = generateToken();
        AuthData authData = new AuthData(authToken, username);
        auths.add(authData);
        return authData;
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData authData : auths) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData a) {
        auths.removeIf(authData -> authData.equals(a));
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
