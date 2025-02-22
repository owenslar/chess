package service;

import requestresult.RegisterRequest;
import requestresult.RegisterResult;

public class UserService {

    public RegisterResult register(RegisterRequest r) {
        // 1. Verify the input
        if (r.username() == null || r.username().isEmpty() || r.email() == null || r.email().isEmpty() ||  r.password() == null || r.password().isEmpty()) {
            return new RegisterResult(null, null, "Error: bad request", 400);
        }

        // 2. Check to make sure the requested username isn't already taken
        // 3. Create a new User model object: User u = new User(...)
        // 4. Insert new User into the database by calling UserDao.createUser(u)
        // 5. Login the user create a new AuthToken model object, insert it into the database
        // 6. Create a Register Result and return it
        return null;
    }
}
