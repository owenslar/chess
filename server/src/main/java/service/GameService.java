package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dtos.GameSummary;
import model.AuthData;
import model.GameData;
import requestresult.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

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

    public JoinResult join(JoinRequest r) throws DataAccessException {
        // They will already be authenticated
        // 1. Verify the input, (verify the playerColor is either WHITE or BLACK and not empty or null),
        // return a bad request error if not
        if (!Objects.equals(r.playerColor(), "WHITE") && !Objects.equals(r.playerColor(), "BLACK")) {
            return new JoinResult("Error: bad request", 400);
        }
        // 2. Verify that the game exists, return a bad request error if not
        GameData requestedGameData = gameDAO.getGame(r.gameID());
        if (requestedGameData == null) {
            return new JoinResult("Error: bad request", 400);
        }

        // 3. If the game exists, make sure the requested color is not already taken (return 403 if it is)
        GameData newGameData;
        AuthData callerAuthData = authDAO.getAuth(r.authToken());

        if (r.playerColor().equals("WHITE")) {
            if (Objects.equals(requestedGameData.whiteUsername(), callerAuthData.username())) {
                return new JoinResult(null, 200);
            }
            if (requestedGameData.whiteUsername() != null) {
                return new JoinResult("Error: already taken", 403);
            }
            newGameData = new GameData(requestedGameData.gameID(),
                    callerAuthData.username(),
                    requestedGameData.blackUsername(),
                    requestedGameData.gameName(),
                    requestedGameData.game());
            gameDAO.updateGame(newGameData);
        }
        else {
            if (Objects.equals(requestedGameData.blackUsername(), callerAuthData.username())) {
                return new JoinResult(null, 200);
            }
            if (requestedGameData.blackUsername() != null) {
                return new JoinResult("Error: already taken", 403);
            }
            newGameData = new GameData(requestedGameData.gameID(),
                    requestedGameData.whiteUsername(),
                    callerAuthData.username(),
                    requestedGameData.gameName(),
                    requestedGameData.game());
            gameDAO.updateGame(newGameData);
        }

        // 5. Create a join result object and return it
        return new JoinResult(null, 200);
    }
}
