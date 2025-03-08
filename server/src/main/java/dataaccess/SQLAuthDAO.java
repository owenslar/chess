package dataaccess;

import model.AuthData;

import java.sql.SQLException;

import static dataaccess.DBUtils.executeStatement;

public class SQLAuthDAO extends AuthDAO {
    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        executeStatement(statement, authData.authToken(), authData.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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
