package ui;

import exception.ResponseException;
import requestresult.LogoutRequest;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PostloginClient {
    private final String serverUrl;
    private final String authToken;
    private final ServerFacade server;

    public PostloginClient(String serverUrl, String authToken, ServerFacade server) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> SET_TEXT_COLOR_BLUE + help();
                case "logout" -> SET_TEXT_COLOR_BLUE + logout();
                case "create" -> SET_TEXT_COLOR_BLUE + create(params);
                case "list" -> SET_TEXT_COLOR_BLUE + list();
                case "join" -> SET_TEXT_COLOR_BLUE + join(params);
                case "observe" -> SET_TEXT_COLOR_BLUE + observe(params);
                default -> SET_TEXT_COLOR_RED + "\nCommand not found, here are the valid commands:" + help();
            };
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    public String observe(String... params) throws ResponseException {
        return "IMPLEMENT OBSERVE";
    }

    public String join(String... params) throws ResponseException {
        return "IMPLEMENT JOIN";
    }

    public String list() throws ResponseException {
        return "IMPLEMENT LIST";
    }

    public String create(String... params) throws ResponseException {
        return "IMPLEMENT CREATE";
    }

    public String logout() throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        return "logout";
    }

    public String help() {
        return "create <NAME> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE +
                "list " + SET_TEXT_COLOR_MAGENTA + "- games\n" + SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] "
                + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE + "observe <ID> " +
                SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE + "logout " + SET_TEXT_COLOR_MAGENTA +
                "- when you are done\n" + SET_TEXT_COLOR_BLUE + "quit " + SET_TEXT_COLOR_MAGENTA + "- playing chess\n"
                + SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands";
    }
}
