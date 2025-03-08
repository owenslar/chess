package dataaccess;

import model.UserData;



public class SQLUserDAO implements UserDAO {
    @Override
    public void createUser(UserData u) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        DBUtils.executeStatement(statement, u.username(), u.password(), u.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
