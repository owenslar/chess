package handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessException;
import requestresult.ClearResult;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {

    ClearService clearService = new ClearService();

    @Override
    protected Object processRequest(Request req, Response res, String authToken) throws DataAccessException {
        ClearResult clearResult = clearService.clear();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        res.status(clearResult.statusCode());
        return gson.toJson(clearResult);
    }
}
