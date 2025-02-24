package handler;

import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseHandler {
    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {
        return null;
    }

    @Override
    protected boolean requiresAuth() {
        return false;
    }
}
