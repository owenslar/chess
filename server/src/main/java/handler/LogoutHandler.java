package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import requestresult.LogoutRequest;
import requestresult.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler {

    UserService userService = new UserService();

    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        LogoutResult logoutResult = userService.logout(logoutRequest);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        res.status(logoutResult.statusCode());
        return gson.toJson(logoutResult);
    }
}
