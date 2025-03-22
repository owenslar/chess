package ui;

import static ui.EscapeSequences.*;

import java.util.Objects;
import java.util.Scanner;

public class PreloginRepl {
    private final PreloginClient client;
    private final PostloginRepl postloginRepl;

    public PreloginRepl(String serverUrl) {
        client = new PreloginClient(serverUrl);
        postloginRepl = new PostloginRepl(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + BLACK_QUEEN + " Welcome to Chess. Here are your possible actions.");
        System.out.print(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE + client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (Objects.equals(result, "Successfully registered.")) {
                    System.out.print(result);

                } else {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                String msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() { System.out.print("\n" + SET_TEXT_COLOR_LIGHT_GREY + "Chess Login >>> " + SET_TEXT_COLOR_GREEN); }
}
