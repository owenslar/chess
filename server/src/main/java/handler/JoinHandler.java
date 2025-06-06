package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import requestresult.JoinRequest;
import requestresult.JoinResult;
import service.GameService;
import spark.Request;
import spark.Response;

public class JoinHandler extends BaseHandler {

    private final GameService gameService;

    public JoinHandler(AuthDAO authDAO, GameService gameService) {
        super(authDAO);
        this.gameService = gameService;
    }

    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {

        JoinRequest tempRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        JoinRequest joinRequest = new JoinRequest(authToken, tempRequest.playerColor(), tempRequest.gameID());

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        JoinResult joinResult = gameService.join(joinRequest);

        res.status(joinResult.statusCode());

        return gson.toJson(joinResult);
    }
}
