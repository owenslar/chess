package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handler.*;
import spark.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();
        // YOU NEED TO CATCH DATAACCESSEXCEPTION HERE

        Spark.exception(DataAccessException.class, this::errorHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static void createRoutes() {
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/game", new CreateHandler());
        Spark.delete("/db", new ClearHandler());
        Spark.get("/game", new ListHandler());
        Spark.put("/game", new JoinHandler());
    }

    public void errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
    }
}
