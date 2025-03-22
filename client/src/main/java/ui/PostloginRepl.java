package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class PostloginRepl {

    private final PostloginClient postloginClient;
    private final String serverUrl;
    private final String authToken;

    public PostloginRepl(String serverUrl, String authToken) {
        postloginClient = new PostloginClient(serverUrl, authToken);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
    }

    public void run(String initialMessage) {
        System.out.println(initialMessage);

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("logout")) {
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
