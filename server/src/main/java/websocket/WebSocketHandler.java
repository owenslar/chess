package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import static websocket.commands.UserGameCommand.CommandType.*;
import websocket.commands.UserGameCommand.CommandType;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;


@WebSocket
public class WebSocketHandler {

    private final WebSocketSessions sessions = new WebSocketSessions();
    private static final Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
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
                        sendMessage(new ErrorMessage("Invalid command"));
                }
            }

        } catch(Exception e) {
            sendMessage(new ErrorMessage("Invalid message format"));
        }
    }

    private void connect(Session session, UserGameCommand commandObject) {

    }

    private void makeMove(Session session, UserGameCommand commandObject) {

    }

    private void leave(Session session, UserGameCommand commandObject) {

    }

    private void resign(Session session, UserGameCommand commandObject) {

    }

    private void sendMessage(ServerMessage messageObject) {

    }

    private void broadcastMessage(Integer gameID, ServerMessage messageObject, Session exceptThisSession) {

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

}
