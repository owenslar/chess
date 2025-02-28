package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import requestresult.ListRequest;
import requestresult.ListResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class ListHandler extends BaseHandler {

    GameService gameService = new GameService();

    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {
        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = gameService.list(listRequest);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        res.status(listResult.statusCode());
        return gson.toJson(listResult);
    }
}
