package handler;

import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler extends BaseHandler {


    @Override
    public Object processRequest(Request request, Response response, String authToken) throws DataAccessException {
        return null;
    }

    @Override
    protected boolean requiresAuth() {
        return false;
    }
}
