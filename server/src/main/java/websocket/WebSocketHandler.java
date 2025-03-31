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
        System.out.println("Made it into websockethandler");
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
                        sendMessage(session, new ErrorMessage("Invalid command"));
                }
            }

        } catch(IllegalArgumentException e) {
            sendMessage(session, new ErrorMessage("Invalid message format"));
        }
    }

    private void connect(Session session, UserGameCommand commandObject) throws DataAccessException, IOException {
        // SKIP THE AUTHENTICATION FOR NOW BUT MAYBE ADD IT LATER
        // Authenticate user
        AuthData authData = authDAO.getAuth(commandObject.getAuthToken());
        if (authData == null) {
            sendMessage(session, new ErrorMessage("Error: unauthorized"));
            return;
        }
        // 1. Make sure the user is authenticated and add them to your sessions collection for the correct game
        sessions.addSessionToGame(commandObject.getGameID(), session);

        // 1. Send a LOAD_GAME message to current session
        GameData currentGame = gameDAO.getGame(commandObject.getGameID());
        if (currentGame == null) {
            sendMessage(session, new ErrorMessage("Error: Invalid GameID"));
            return;
        }
        LoadGameMessage loadGameMessage = new LoadGameMessage(currentGame.game());
        sendMessage(session, loadGameMessage);

        // 2. Send a NOTIFICATION message to all OTHER clients connected to that game that someone joined
        NotificationMessage notificationMessage = new NotificationMessage(authData.username() + " joined the game!");
        broadcastMessage(commandObject.getGameID(), notificationMessage, session);
        // remember to use their username in the message
    }

    private void makeMove(Session session, UserGameCommand commandObject) {

    }

    private void leave(Session session, UserGameCommand commandObject) {

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
