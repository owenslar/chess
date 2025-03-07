import chess.*;
import dataaccess.AuthDAO;
import dataaccess.DaoFactory;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

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