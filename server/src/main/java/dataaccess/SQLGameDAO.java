package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {
    @Override
    public Integer createGame(GameData gameData) throws DataAccessException {
        // MAKE SURE TO USE AUTO_INCREMENT FOR THIS
        return null;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException{
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException{
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
