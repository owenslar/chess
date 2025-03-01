package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {

    private final AtomicInteger gameIDCounter = new AtomicInteger(0);
    private final static ArrayList<GameData> GAMES = new ArrayList<>();

    @Override
    public Integer createGame(GameData game) throws DataAccessException {
        int gameID = gameIDCounter.incrementAndGet();
        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        GAMES.add(newGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        for (GameData game : GAMES) {
            if (game.gameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return GAMES;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        for (int i = 0; i < GAMES.size(); i++) {
            if (GAMES.get(i).gameID() == gameData.gameID()) {
                GAMES.set(i, gameData);
            }
        }
    }

    @Override
    public void clear() throws DataAccessException {
        GAMES.clear();
    }
}
