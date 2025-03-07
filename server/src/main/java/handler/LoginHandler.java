package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import requestresult.LoginRequest;
import requestresult.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseHandler {
    private final UserService userService;

    public LoginHandler(AuthDAO authDAO, UserService userService) {
        super(authDAO);
        this.userService = userService;
    }

    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        res.status(loginResult.statusCode());
        return gson.toJson(loginResult);

    }

    @Override
    protected boolean requiresAuth() {
        return false;
    }
}
