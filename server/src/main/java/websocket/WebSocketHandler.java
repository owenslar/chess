package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import static websocket.commands.UserGameCommand.CommandType.*;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


@WebSocket
public class WebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private static final Gson GSON = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connected: " + session.getRemoteAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        try {

            UserGameCommand commandObject = deserializeMessage(message);
            if (commandObject != null) {
                switch (commandObject.getCommandType()) {
                    case CONNECT:
                        connect(session, commandObject);
                        break;
                    case MAKE_MOVE:
                        makeMove(session, (MakeMoveCommand) commandObject);
                        break;
                    case LEAVE:
                        leave(session, commandObject);
                        break;
                    case RESIGN:
                        resign(session, commandObject);
                        break;
                    default:
                        sendMessage(session, new ErrorMessage("Error: Invalid command type"));
                }
            }

        } catch(IllegalArgumentException e) {
            sendMessage(session, new ErrorMessage("Error: Invalid message format"));
        }
    }

    private void connect(Session session, UserGameCommand commandObject) throws DataAccessException, IOException {
        // SKIP THE AUTHENTICATION FOR NOW BUT MAYBE ADD IT LATER
        // 1. Authenticate user and add them to sessions
        if (commandObject.getAuthToken() == null) {
            sendMessage(session, new ErrorMessage("Error: no AuthToken provided"));
            return;
        }
        if (commandObject.getGameID() == null) {
            sendMessage(session, new ErrorMessage("Error: no GameID provided"));
            return;
        }
        AuthData authData = authDAO.getAuth(commandObject.getAuthToken());
        if (authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return;
        }
        sessions.addSessionToGame(commandObject.getGameID(), session);

        // 2. Send a LOAD_GAME message to current session
        GameData currentGame = gameDAO.getGame(commandObject.getGameID());
        if (currentGame == null) {
            sendMessage(session, new ErrorMessage("Error: invalid gameID"));
            return;
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(currentGame.game());
        sendMessage(session, loadGameMessage);

        // 3. Send a NOTIFICATION message to all OTHER clients connected to that game that someone joined including what role
        // and their username
        NotificationMessage notificationMessage = getNotificationMessage(currentGame, authData);
        broadcastMessage(commandObject.getGameID(), notificationMessage, session);
    }

    private NotificationMessage getNotificationMessage(GameData currentGame, AuthData authData) {
        NotificationMessage notificationMessage;
        boolean whiteUser = Objects.equals(currentGame.whiteUsername(), authData.username());
        boolean blackUser = Objects.equals(currentGame.blackUsername(), authData.username());
        if (whiteUser && blackUser) {
            notificationMessage = new NotificationMessage(authData.username() + " is now playing as both players");
        } else if (whiteUser) {
            notificationMessage = new NotificationMessage(authData.username() + " is now playing as WHITE");
        } else if (blackUser) {
            notificationMessage = new NotificationMessage(authData.username() + " is now playing as BLACK");
        } else {
            notificationMessage = new NotificationMessage(authData.username() + " is now observing the game");
        }
        return notificationMessage;
    }

    private void makeMove(Session session, MakeMoveCommand commandObject) throws IOException, DataAccessException {
        String authToken = commandObject.getAuthToken();
        Integer gameID = commandObject.getGameID();
        ChessMove move = commandObject.getMove();
        if (authToken == null) {
            sendMessage(session, new ErrorMessage("Error: no authToken provided"));
            return;
        }
        if (gameID == null) {
            sendMessage(session, new ErrorMessage("Error: no gameID provided"));
            return;
        }
        if (move == null) {
            sendMessage(session, new ErrorMessage("Error: no chess move provided"));
            return;
        }
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return;
        }
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            sendMessage(session, new ErrorMessage("Error: invalid gameID"));
            return;
        }
        int startPosCol = move.getStartPosition().getColumn();
        int startPosRow = move.getStartPosition().getRow();
        int endPosCol = move.getEndPosition().getColumn();
        int endPosRow = move.getEndPosition().getRow();
        if (startPosCol > 8 || startPosCol < 1 || startPosRow > 8
                || startPosRow < 1 || endPosRow > 8 || endPosRow < 1 || endPosCol > 8 || endPosCol < 1) {
            sendMessage(session, new ErrorMessage("Error: invalid move (out of bounds)"));
            return;
        }
        ChessGame.TeamColor moveColor = gameData.game().getBoard().getPiece(move.getStartPosition()).getTeamColor();
        if (moveColor == null) {
            sendMessage(session, new ErrorMessage("Error: invalid move"));
            return;
        }
        ChessGame.TeamColor playerColor = null;
        boolean isBothPlayers = false;
        boolean whiteUser = Objects.equals(gameData.whiteUsername(), authData.username());
        boolean blackUser = Objects.equals(gameData.blackUsername(), authData.username());
        if (whiteUser) {
            playerColor = ChessGame.TeamColor.WHITE;
        }
        if (blackUser) {
            if (playerColor == ChessGame.TeamColor.WHITE) {
                isBothPlayers = true;
            }
            playerColor = ChessGame.TeamColor.BLACK;
        }
        if (playerColor == null) {
            sendMessage(session, new ErrorMessage("Error: cannot make a move as an observer"));
            return;
        }
        if (playerColor != gameData.game().getTeamTurn()) {
            if (!isBothPlayers) {
                sendMessage(session, new ErrorMessage("Error: not your turn"));
                return;
            }
        }
        try {
            gameData.game().makeMove(move);
        } catch (InvalidMoveException e) {
            sendMessage(session, new ErrorMessage("Error: invalid move"));
            return;
        }
        ChessGame.TeamColor defendingColor = (moveColor == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;
        boolean isCheckmate = gameData.game().isInCheckmate(defendingColor);
        boolean isStalemate = gameData.game().isInStalemate(defendingColor);
        boolean isCheck = gameData.game().isInCheck(defendingColor);
        if (isCheckmate || isStalemate) {
            gameData.game().setIsOver(true);
        }
        gameDAO.updateGame(gameData);
        LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
        broadcastMessage(gameID, loadGameMessage, null);
        String defendingUsername;
        if (defendingColor == ChessGame.TeamColor.WHITE) {
            defendingUsername = gameData.whiteUsername();
        } else {
            defendingUsername = gameData.blackUsername();
        }
        NotificationMessage moveMessage = new NotificationMessage(authData.username() + " moved: " +
                convertChessPosition(move.getStartPosition()) + " -> " + convertChessPosition(move.getEndPosition()));
        broadcastMessage(gameID, moveMessage, session);
        if (isCheckmate) {
            NotificationMessage checkmateMessage = new NotificationMessage(defendingUsername + " is in checkmate! " + authData.username() + " won the game");
            broadcastMessage(gameID, checkmateMessage, null);
            return;
        }
        if (isStalemate) {
            NotificationMessage stalemateMessage = new NotificationMessage(defendingUsername + " is in stalemate! The game has ended in a draw");
            broadcastMessage(gameID, stalemateMessage, null);
            return;
        }
        if (isCheck) {
            NotificationMessage checkMessage = new NotificationMessage(defendingUsername + " is now in check!");
            broadcastMessage(gameID, checkMessage, null);
        }
    }

    private void leave(Session session, UserGameCommand commandObject) throws IOException, DataAccessException {
        // 1. Authenticate the user and validate the command (check if the return types are null)
        if (commandObject.getAuthToken() == null) {
            sendMessage(session, new ErrorMessage("Error: no authToken provided"));
            return;
        }
        if (commandObject.getGameID() == null) {
            sendMessage(session, new ErrorMessage("Error: no GameID provided"));
            return;
        }
        AuthData authData = authDAO.getAuth(commandObject.getAuthToken());
        if (authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return;
        }

        // 2. Retrieve the game from the db, update it by removing the player, update it in the DB
        GameData gameData = gameDAO.getGame(commandObject.getGameID());
        if (gameData == null) {
            sendMessage(session, new ErrorMessage("Error: invalid gameID"));
            return;
        }
        if (Objects.equals(gameData.whiteUsername(), authData.username())) {
            GameData newGameData = new GameData(gameData.gameID(), null,
                    gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        } else if (Objects.equals(gameData.blackUsername(), authData.username())) {
            GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    null, gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        } else {
            if (!Objects.equals(sessions.getIDForSession(session), commandObject.getGameID())) {
                sendMessage(session, new ErrorMessage("Error: invalid leave command"));
                return;
            }
            broadcastMessage(commandObject.getGameID(), new NotificationMessage(authData.username() + " is no longer observing the game"), session);
            sessions.removeSessionFromGame(commandObject.getGameID(), session);
            return;
        }

        // 3. Send a Notification object to all other clients in the game
        // informing them that the user left (both players and observers)
        NotificationMessage notificationMessage = new NotificationMessage(authData.username() + " left the game");
        broadcastMessage(commandObject.getGameID(), notificationMessage, session);
        sessions.removeSessionFromGame(commandObject.getGameID(), session);
    }

    private void resign(Session session, UserGameCommand commandObject) throws IOException, DataAccessException {
        // 1. validate commands
        if (commandObject.getAuthToken() == null) {
            sendMessage(session, new ErrorMessage("Error: no authToken provided"));
            return;
        }

        if (commandObject.getGameID() == null) {
            sendMessage(session, new ErrorMessage("Error: no gameID provided"));
            return;
        }

        // 2. authenticate user
        AuthData authData = authDAO.getAuth(commandObject.getAuthToken());
        if (authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return;
        }


        // 3. mark the game as over and update it in database
        GameData gameData = gameDAO.getGame(commandObject.getGameID());
        if (gameData == null) {
            sendMessage(session, new ErrorMessage("Error: invalid gameID"));
            return;
        }

        if (!Objects.equals(authData.username(), gameData.whiteUsername()) &&
                !Objects.equals(authData.username(), gameData.blackUsername())) {
            sendMessage(session, new ErrorMessage("Error: cannot resign as an observer"));
            return;
        }
        if (gameData.game().getIsOver()) {
            sendMessage(session, new ErrorMessage("Error: game already resigned"));
            return;
        }

        gameData.game().setIsOver(true);
        gameDAO.updateGame(gameData);

        // 4. send a notification to ALL users in the game informing them that he resigned
        NotificationMessage notificationMessage = new NotificationMessage(authData.username() + " resigned the game");
        broadcastMessage(commandObject.getGameID(), notificationMessage, null);
    }

    private void sendMessage(Session session, ServerMessage messageObject) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(messageObject.toString());
        } else {
            sessions.removeSession(session);
        }
    }

    private void broadcastMessage(Integer gameID, ServerMessage messageObject, Session exceptThisSession) throws IOException {
        Set<Session> gameSessions = sessions.getSessionsForGame(gameID);
        ArrayList<Session> removeSessions = new ArrayList<>();
        for (Session session : gameSessions) {
            if (session.isOpen()) {
                if (!session.equals(exceptThisSession)) {
                    session.getRemote().sendString(messageObject.toString());
                }
            } else {
                removeSessions.add(session);
            }
        }

        for (Session session : removeSessions) {
            sessions.removeSession(session);
        }
    }

    private String convertChessPosition(ChessPosition pos) {
        String result = "";
        switch (pos.getColumn()) {
            case 1:
                result += "a";
                break;
            case 2:
                result += "b";
                break;
            case 3:
                result += "c";
                break;
            case 4:
                result += "d";
                break;
            case 5:
                result += "e";
                break;
            case 6:
                result += "f";
                break;
            case 7:
                result += "g";
                break;
            case 8:
                result += "h";
                break;
        }
        result += pos.getRow();
        return result;
    }

    private UserGameCommand deserializeMessage(String message) throws IllegalArgumentException {
        JsonObject jsonObject = GSON.fromJson(message, JsonObject.class);

        if (!jsonObject.has("commandType")) {
            throw new IllegalArgumentException("Missing commandType field");
        }
        String commandString = jsonObject.get("commandType").getAsString();
        CommandType commandType = valueOf(commandString);

        if (commandType == MAKE_MOVE) {
            return GSON.fromJson(message, MakeMoveCommand.class);
        } else {
            return GSON.fromJson(message, UserGameCommand.class);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed: " + reason + " (code: " + statusCode + ")");
        sessions.removeSession(session);
    }

}
