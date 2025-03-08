package dataaccess;

import model.UserData;

import static dataaccess.DBUtils.executeStatement;


public class SQLUserDAO implements UserDAO {
    @Override
    public void createUser(UserData u) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeStatement(statement, u.username(), u.password(), u.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE users";
        executeStatement(statement);
    }
}
