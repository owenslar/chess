package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private static final ArrayList<UserData> users = new ArrayList<>();

    @Override
    public void createUser(UserData u) {
        users.add(u);
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (user.username() == username) {
                return user;
            }
        }
        return null;
    }
}
