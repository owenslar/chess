package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private final static ArrayList<GameData> GAMES = new ArrayList<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        GAMES.add(game);
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
