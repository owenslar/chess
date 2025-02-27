package service;

import chess.ChessGame;
import dataaccess.DaoFactory;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import requestresult.CreateRequest;
import requestresult.CreateResult;

public class GameService {

    GameDAO gameDAO = DaoFactory.createGameDAO();
    private static int numGamesCreated = 0;

    public CreateResult create(CreateRequest r) throws DataAccessException {
        // 1. Verify the input
        if (r.gameName() == null || r.gameName().isEmpty()) {
            return new CreateResult(null, "Error: bad request", 400);
        }

        // 2. Create new GameData object with null values and a game name
        numGamesCreated += 1;
        GameData newGame = new GameData(numGamesCreated, null, null,
                r.gameName(), new ChessGame());

        // 3. Insert the new game into the database
        gameDAO.createGame(newGame);

        // 4. create a CreateResult object and return it
        return new CreateResult(numGamesCreated, null, 200);
    }
}
