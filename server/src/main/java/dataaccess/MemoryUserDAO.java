package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private static final ArrayList<UserData> users = new ArrayList<>();

    @Override
    public void createUser(UserData u) throws DataAccessException {
        users.add(u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }
}
