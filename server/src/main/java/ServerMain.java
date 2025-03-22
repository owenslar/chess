
import dataaccess.AuthDAO;
import dataaccess.DaoFactory;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ClearService;
import service.GameService;
import service.UserService;

public class ServerMain {
    public static void main(String[] args) {

        UserDAO userDAO = DaoFactory.createUserDAO();
        AuthDAO authDAO = DaoFactory.createAuthDAO();
        GameDAO gameDAO = DaoFactory.createGameDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        server.Server server = new server.Server(userService, gameService, clearService, authDAO);
        server.run(8080);
    }
}