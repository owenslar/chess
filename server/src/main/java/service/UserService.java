package service;

import requestresult.RegisterRequest;
import requestresult.RegisterResult;

public class UserService {

    public RegisterResult register(RegisterRequest r) {
        return null;
        // 1. Verify the input
        // 2. Check to make sure the requested username isn't already taken
        // 3. Create a new User model object: User u = new User(...)
        // 4. Insert new User into the database by calling UserDao.createUser(u)
        // 5. Login the user (create a new AuthToken model object, insert it into the database
        // 6. Create a Register Result and return it
    }
}
