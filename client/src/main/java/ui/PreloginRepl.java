package ui;

import static ui.EscapeSequences.*;

import java.util.Scanner;

public class PreloginRepl {
    private final PreloginClient client;

    public PreloginRepl(String serverUrl) {
        client = new PreloginClient(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + BLACK_QUEEN + " Welcome to Chess. Here are your possible actions.");
        System.out.print(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE + client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals(SET_TEXT_COLOR_BLUE + "quitting chess")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() { System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Login >>> " + SET_TEXT_COLOR_GREEN); }
}
