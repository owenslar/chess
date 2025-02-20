package handler;

import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class BaseHandler implements Route {

    //
    //
    // Maybe put everything in a try-catch to catch DataAccessAcceptions

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {

        String authToken = request.headers("authorization");

        if (requiresAuth()) {
            // call the authDao that checks if the authToken is valid and not null and not empty
            // if it is a bad auth then return a 401 unauthorized error
        } else {
            authToken = null;
        }

        return processRequest(request, response, authToken);
    }

    protected abstract Object processRequest(Request req, Response res, String authToken) throws DataAccessException;

    protected boolean requiresAuth() {
        return true;
    }
}
