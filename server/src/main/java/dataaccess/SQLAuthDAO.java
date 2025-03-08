package dataaccess;

import model.AuthData;

import static dataaccess.DBUtils.executeStatement;

public class SQLAuthDAO extends AuthDAO {
    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        executeStatement(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE auths";
        executeStatement(statement);
    }
}
