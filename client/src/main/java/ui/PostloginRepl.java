package ui;

import server.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginRepl {

    private final PostloginClient postloginClient;

    public PostloginRepl(String serverUrl, String authToken, ServerFacade server) {
        postloginClient = new PostloginClient(serverUrl, authToken, server);
    }

    public void run(String initialMessage) {
        System.out.println(initialMessage);
        System.out.println(SET_TEXT_COLOR_MAGENTA + "Type 'help' to see your possible actions.");

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals(SET_TEXT_COLOR_BLUE + "logout successful")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = postloginClient.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess >>> " + SET_TEXT_COLOR_GREEN);
    }
}
