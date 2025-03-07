package service;

import dataaccess.*;
import requestresult.ClearResult;

public class ClearService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearResult clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        return new ClearResult(null, 200);
    }
}
