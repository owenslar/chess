package dataaccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    Integer createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameId) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    void clear() throws DataAccessException;

}
