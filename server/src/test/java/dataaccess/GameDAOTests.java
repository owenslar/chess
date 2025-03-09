package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GameDAOTests {

    GameDAO gameDAO;
    GameData game;
    GameData game2;
    GameData badGame;

    @BeforeEach
    public void setUp() {
        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initiate DB: " + e.getMessage());
        }
        gameDAO = DaoFactory.createGameDAO();
        game = new GameData(0, null, null, "testGameName", new ChessGame());
        game2 = new GameData(0, null, null, "testGame2", new ChessGame());
        badGame = new GameData(0, null, null, "badGame", null);
    }

    @Test
    public void positiveCreateGameTest() {
        try {
            Integer gameId = gameDAO.createGame(game);

            Assertions.assertNotNull(gameId);

            try (var conn = DatabaseManager.getConnection()) {
                String query = "SELECT * FROM games WHERE gameId = ?";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setInt(1, gameId);
                    try (ResultSet rs = ps.executeQuery()) {
                        Assertions.assertTrue(rs.next());

                        Assertions.assertEquals(game.whiteUsername(), rs.getString("whiteUsername"));
                        Assertions.assertEquals(game.blackUsername(), rs.getString("blackUsername"));
                        Assertions.assertEquals(game.gameName(), rs.getString("gameName"));
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void badCreateGameTest() {
        try {
            gameDAO.createGame(badGame);
            Assertions.fail("Expected SQLException due to NOT NULL constraint");
        } catch (DataAccessException e) {
            Assertions.assertEquals("Column 'game' cannot be null", e.getMessage());
        }
    }

    @Test
    public void positiveGetGameTest() {
        try {
            int gameId = gameDAO.createGame(game);

            GameData actualGame = gameDAO.getGame(gameId);

            Assertions.assertEquals(actualGame.gameID(), gameId);
            Assertions.assertEquals(actualGame.whiteUsername(), game.whiteUsername());
            Assertions.assertEquals(actualGame.blackUsername(), game.blackUsername());
            Assertions.assertEquals(actualGame.gameName(), game.gameName());
            Assertions.assertEquals(actualGame.game(), game.game());
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void negativeGetGameTest() {
        try {
            GameData actualGame = gameDAO.getGame(999);
            Assertions.assertNull(actualGame, "Expected null for non-existent game");
        } catch (DataAccessException e) {
            Assertions.fail("Caught unexpected DAE");
        }
    }

    @Test
    public void positiveListGamesTest() {
        try {
            gameDAO.createGame(game);
            gameDAO.createGame(game2);

            ArrayList<GameData> games = gameDAO.listGames();

            Assertions.assertFalse(games.isEmpty());
            Assertions.assertEquals(2, games.size());
        } catch (DataAccessException e) {
            Assertions.fail("Caught an unexpected DAE");
        }
    }

    @Test
    public void emptyGamesListTest() {
        try {
            ArrayList<GameData> games = gameDAO.listGames();

            Assertions.assertTrue(games.isEmpty());
        } catch (DataAccessException e) {
            Assertions.fail("Caught an unexpected DAE");
        }
    }

    @AfterEach
    public void cleanUp() {
        try (var conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("TRUNCATE games");
            }
        } catch (SQLException | DataAccessException e) {
            Assertions.fail("Caught an exception when cleaning up");
        }
    }
}
