package websocket;

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
    private static final Gson gson = new Gson();
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
                        makeMove(session, commandObject);
                        break;
                    case LEAVE:
                        leave(session, commandObject);
                        break;
                    case RESIGN:
                        resign(session, commandObject);
                        break;
                    default:
                        sendMessage(session, new ErrorMessage("Error: Invalid command"));
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
        if (commandObject.getCommandType() == null) {
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
            sendMessage(session, new ErrorMessage("Error: game not found"));
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

    private void makeMove(Session session, UserGameCommand commandObject) {

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
            sendMessage(session, new ErrorMessage("Error: game not found"));
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

    private void resign(Session session, UserGameCommand commandObject) {

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

    private UserGameCommand deserializeMessage(String message) throws IllegalArgumentException {
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

        if (!jsonObject.has("commandType")) {
            throw new IllegalArgumentException("Missing commandType field");
        }
        String commandString = jsonObject.get("commandType").getAsString();
        CommandType commandType = CommandType.valueOf(commandString);

        if (commandType == MAKE_MOVE) {
            return gson.fromJson(message, MakeMoveCommand.class);
        } else {
            return gson.fromJson(message, UserGameCommand.class);
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed: " + reason + " (code: " + statusCode + ")");
        sessions.removeSession(session);
    }

}
