package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static dataaccess.DBUtils.executeStatement;

public class SQLGameDAO implements GameDAO {
    @Override
    public Integer createGame(GameData gameData) throws DataAccessException {
        // MAKE SURE TO USE AUTO_INCREMENT FOR THIS
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeStatement(statement,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                convertToJson(gameData.game()));
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM games WHERE gameId=?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = convertToChessGame(rs.getString("game"));
                        return new GameData(rs.getInt("gameId"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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

    private String convertToJson(ChessGame game) {
        return new Gson().toJson(game);
    }

    private ChessGame convertToChessGame(String json) {
        return new Gson().fromJson(json, ChessGame.class);
    }
}
