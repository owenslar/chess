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
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> result = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ChessGame game = convertToChessGame(rs.getString("game"));
                        result.add(new GameData(rs.getInt("gameId"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game));
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, " +
                    "gameName = ?, game = ? WHERE gameId = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());
                ps.setString(3, gameData.gameName());
                ps.setString(4, convertToJson(gameData.game()));
                ps.setInt(5, gameData.gameID());

                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Game does not exist");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE games";
        executeStatement(statement);
    }

    private String convertToJson(ChessGame game) {
        if (game == null) {
            return null;
        } else {
            return new Gson().toJson(game);
        }
    }

    private ChessGame convertToChessGame(String json) {
        return new Gson().fromJson(json, ChessGame.class);
    }
}
