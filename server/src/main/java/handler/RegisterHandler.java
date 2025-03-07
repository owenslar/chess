package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends BaseHandler {
    private final UserService userService;

    public RegisterHandler(AuthDAO authDAO, UserService userService) {
        super(authDAO);
        this.userService = userService;
    }

    @Override
    public Object processRequest(Request request, Response response, String authToken) throws DataAccessException {
        RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        response.status(registerResult.statusCode());
        return gson.toJson(registerResult);
    }

    @Override
    protected boolean requiresAuth() {
        return false;
    }
}
