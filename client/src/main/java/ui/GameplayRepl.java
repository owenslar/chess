package ui;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayRepl implements NotificationHandler {
    private final GameplayClient client;
    private final String orientation;

    public GameplayRepl(String serverUrl, String authToken, Integer gameID, String orientation) {
        client = new GameplayClient(serverUrl, authToken, gameID, this, orientation);
        client.connect(authToken, gameID);
        this.orientation = orientation;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        String resultStr = "";
        while (!resultStr.equals(SET_TEXT_COLOR_BLUE + "left game")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                resultStr = client.eval(line);
                System.out.print(resultStr);
            } catch (Throwable e) {
                String message = e.toString();
                System.out.print(message);
            }
        }
        System.out.println();
    }

    public void notify(ServerMessage message) {
        switch (message) {
            case NotificationMessage notification ->
                    System.out.println(SET_TEXT_COLOR_YELLOW + notification.getMessage());
            case ErrorMessage error -> System.out.println(SET_TEXT_COLOR_RED + error.getErrorMessage());
            case LoadGameMessage loadGameMessage -> {
                client.setGame(loadGameMessage.getGame());
                System.out.println();
                System.out.println(client.stringifyGame(loadGameMessage.getGame(), orientation, null));
            }
            case null, default -> System.out.println(SET_TEXT_COLOR_RED + "Unknown message type received");
        }
        printPrompt();
    }

    public static void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Game >>> " + SET_TEXT_COLOR_GREEN);
    }
}
