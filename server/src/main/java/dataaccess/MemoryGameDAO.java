package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {

    private final AtomicInteger gameIDCounter = new AtomicInteger(0);
    private final static Map<Integer, GameData> GAMES = new HashMap<>();

    @Override
    public Integer createGame(GameData game) throws DataAccessException {
        int gameID = gameIDCounter.incrementAndGet();
        GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        GAMES.put(gameID, newGame);
        return gameID;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        return GAMES.get(gameId);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(GAMES.values());
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        GAMES.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        GAMES.clear();
    }
}
