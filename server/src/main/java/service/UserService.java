package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;

import java.util.Objects;

public class UserService {

    UserDAO userDAO = DaoFactory.createUserDAO();
    AuthDAO authDAO = DaoFactory.createAuthDAO();

    public RegisterResult register(RegisterRequest r) throws DataAccessException {
        // 1. Verify the input
        if (r.username() == null || r.username().isEmpty()
                || r.email() == null || r.email().isEmpty()
                || r.password() == null || r.password().isEmpty()) {
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

    public LoginResult login(LoginRequest r) throws DataAccessException {
        // 1. Verify the input
        if (r.username() == null || r.username().isEmpty() || r.password() == null || r.password().isEmpty()) {
            return new LoginResult(null, null, "Error: unauthorized", 401);
        }

        // 2. Check to make sure the request username exists in the database
        UserData user = userDAO.getUser(r.username());
        if (user == null) {
            return new LoginResult(null, null, "Error: unauthorized", 401);
        }

        // 3. If the username exists, check if the password matches
        if (!Objects.equals(user.password(), r.password())) {
            return new LoginResult(null, null, "Error: unauthorized", 401);
        }

        // 3.5. If the user is already logged in, return the authToken they already should be using
        AuthData loggedInAuthData = authDAO.getAuthByUsername(r.username());
        if (loggedInAuthData != null) {
            return new LoginResult(r.username(), loggedInAuthData.authToken(), null, 200);
        }

        // 4. If the password matches login the user (generate a new AuthToken model object, insert it into database)
        String authToken = AuthDAO.generateToken();
        AuthData authData = new AuthData(authToken, r.username());
        authDAO.createAuth(authData);

        // 6. Create a LoginResult and return it
        return new LoginResult(r.username(), authToken, null, 200);
    }
}