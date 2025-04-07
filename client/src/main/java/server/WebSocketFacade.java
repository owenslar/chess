package server;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import ui.NotificationHandler;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                    String type = jsonObject.get("serverMessageType").getAsString();
                    Gson gson = new Gson();
                    ServerMessage serverMessage;

                    switch (type) {
                        case "LOAD_GAME":
                            serverMessage = gson.fromJson(message, LoadGameMessage.class);
                            break;
                        case "NOTIFICATION":
                            serverMessage = gson.fromJson(message, NotificationMessage.class);
                            break;
                        case "ERROR":
                            serverMessage = gson.fromJson(message, ErrorMessage.class);
                            break;
                        default:
                            throw new ResponseException(500, "Unknown message type: " + type);
                    }
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(UserGameCommand connectCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(connectCommand.toString());
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(MakeMoveCommand moveCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(moveCommand.toString());
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(UserGameCommand leaveCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(leaveCommand.toString());
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(UserGameCommand resignCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(resignCommand.toString());
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
