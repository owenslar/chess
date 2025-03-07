package handler;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class BaseHandler implements Route {

    private final AuthDAO authDAO;

    public BaseHandler(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {

        String authToken = request.headers("authorization");

        if (requiresAuth()) {
            if (authToken == null || authToken.isEmpty()) {
                response.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

            if (authDAO.getAuth(authToken) == null) {
                response.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }

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
