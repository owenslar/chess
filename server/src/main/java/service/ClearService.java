package service;

import dataaccess.*;
import requestresult.ClearResult;

public class ClearService {

    UserDAO userDAO = DaoFactory.createUserDAO();
    GameDAO gameDAO = DaoFactory.createGameDAO();
    AuthDAO authDAO = DaoFactory.createAuthDAO();

    public ClearResult clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new ClearResult(null, 200);
    }
}
