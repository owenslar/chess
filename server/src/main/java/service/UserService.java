package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;

public class UserService {

    MemoryUserDAO userDAO = new MemoryUserDAO();
    MemoryAuthDAO authDAO = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        // 1. Verify the input
        if (r.username() == null || r.username().isEmpty() || r.email() == null || r.email().isEmpty() || r.password() == null || r.password().isEmpty()) {
            return new RegisterResult(null, null, "Error: bad request", 400);
        }

        // 2. Check to make sure the requested username isn't already taken
        if (userDAO.getUser(r.username()) != null) {
            return new RegisterResult(null, null, "Error: already taken", 403);
        }

        // 3. Create a new User model object: User u = new User(...)
        UserData u = new UserData(r.username(), r.password(), r.email());

        // 4. Insert new User into the database by calling UserDao.createUser(u)
        userDAO.createUser(u);

        // 5. Login the user (create a new AuthToken model object, insert it into the database)
        String authToken = AuthDAO.generateToken();
        AuthData authData = new AuthData(authToken, r.username());
        authDAO.createAuth(authData);

        // 6. Create a Register Result and return it
        return new RegisterResult(r.username(), authToken, null, 200);
    }
}