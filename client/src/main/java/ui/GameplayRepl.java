package ui;

import chess.ChessGame;
import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayRepl {
    private final GameplayClient client;

    public GameplayRepl(String serverUrl, String authToken, ServerFacade server) {
        client = new GameplayClient(serverUrl, authToken, server);
    }

    public void run(ChessGame game, String orientation) {
        System.out.print(client.stringifyGame(game, orientation));

        Scanner scanner = new Scanner(System.in);
        String resultStr = "";
        while (!resultStr.equals(SET_TEXT_COLOR_BLUE + "leaving game")) {
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

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Game >>> " + SET_TEXT_COLOR_GREEN);
    }
}
