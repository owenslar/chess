package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private static final ArrayList<UserData> USERS = new ArrayList<>();

    @Override
    public void createUser(UserData u) throws DataAccessException {
        USERS.add(u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : USERS) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }
}
