package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import requestresult.CreateRequest;
import requestresult.CreateResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class CreateHandler extends BaseHandler {

    GameService gameService = new GameService();

    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        CreateRequest tempRequest = gson.fromJson(req.body(), CreateRequest.class);
        CreateRequest createRequest = new CreateRequest(tempRequest.gameName(), authToken);

        CreateResult createResult = gameService.create(createRequest);
        res.status(createResult.statusCode());

        return gson.toJson(createResult);
    }
}
