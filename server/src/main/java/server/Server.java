package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final AuthDAO authDAO;

    public Server() {
        UserDAO userDAO = DaoFactory.createUserDAO();
        AuthDAO authDAO = DaoFactory.createAuthDAO();
        GameDAO gameDAO = DaoFactory.createGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.authDAO = authDAO;
    }

    public Server(UserService userService, GameService gameService, ClearService clearService, AuthDAO authDAO) {
        this.userService = userService;
        this.gameService = gameService;
        this.clearService = clearService;
        this.authDAO = authDAO;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();

        Spark.exception(DataAccessException.class, this::errorHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void createRoutes() {
        Spark.post("/user", new RegisterHandler(authDAO, userService));
        Spark.post("/session", new LoginHandler(authDAO, userService));
        Spark.delete("/session", new LogoutHandler(authDAO, userService));
        Spark.post("/game", new CreateHandler(authDAO, gameService));
        Spark.delete("/db", new ClearHandler(authDAO, clearService));
        Spark.get("/game", new ListHandler(authDAO, gameService));
        Spark.put("/game", new JoinHandler(authDAO, gameService));
    }

    public void errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
    }
}
