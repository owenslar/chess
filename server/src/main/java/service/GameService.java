package service;

import chess.ChessGame;
import dataaccess.DaoFactory;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dtos.GameSummary;
import model.GameData;
import requestresult.CreateRequest;
import requestresult.CreateResult;
import requestresult.ListRequest;
import requestresult.ListResult;

import java.util.ArrayList;
import java.util.List;

public class GameService {

    GameDAO gameDAO = DaoFactory.createGameDAO();

    public CreateResult create(CreateRequest r) throws DataAccessException {
        // 1. Verify the input
        if (r.gameName() == null || r.gameName().isEmpty()) {
            return new CreateResult(null, "Error: bad request", 400);
        }

        // 2. Create new GameData object with null values and a game name
        GameData newGame = new GameData(0, null, null,
                r.gameName(), new ChessGame());

        // 3. Insert the new game into the database
        int gameID = gameDAO.createGame(newGame);

        // 4. create a CreateResult object and return it
        return new CreateResult(gameID, null, 200);
    }

    public ListResult list(ListRequest r) throws DataAccessException {
        // GAME DATA CONTAINS A CHESSGAME WHICH LIST RESULT SHOUlD NOT HAVE

        // 1. Check authToken
        if (r.authToken() == null || r.authToken().isEmpty()) {
            return new ListResult(null, "Error: unauthorized", 401);
        }
        // 2. Call gameDAO method that returns a list of games
        ArrayList<GameData> gameDataList = gameDAO.listGames();

        // 3. Convert list of GameData objects to GameSummary objects (exclude ChessGame)
        List<GameSummary> summaries = new ArrayList<>();
        for (GameData gameData : gameDataList) {
            summaries.add(GameSummary.from(gameData));
        }

        // 4. Create a ListResult object that contains the list of games and correct status code and return it
        return new ListResult(summaries, null, 200);

    }
}
