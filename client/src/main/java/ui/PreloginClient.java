package ui;

import exception.ResponseException;
import requestresult.RegisterRequest;
import requestresult.RegisterResult;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreloginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private PostloginRepl postloginRepl;
    private String authToken;


    public PreloginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        authToken = null;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> SET_TEXT_COLOR_BLUE + login(params);
                case "register" -> SET_TEXT_COLOR_BLUE + register(params);
                case "quit" -> SET_TEXT_COLOR_BLUE + "quit";
                case "help" -> SET_TEXT_COLOR_BLUE + help();
                default -> SET_TEXT_COLOR_BLUE + "\nCommand not found, here are the valid commands:\n" + help();
            };
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = server.register(registerRequest);
            authToken = registerResult.authToken();
            postloginRepl = new PostloginRepl(serverUrl, authToken);
            postloginRepl.run(SET_TEXT_COLOR_BLUE + "Registration successful");
            return "\n";
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            return "please input a username and a password with a login command";
        }
        return "";
    }

    public String help() {
        return "\n" + "register <USERNAME> <PASSWORD> <EMAIL> " + SET_TEXT_COLOR_MAGENTA +
                "- to create an account\n" + SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD> " +
                SET_TEXT_COLOR_MAGENTA + "- to play chess\n" + SET_TEXT_COLOR_BLUE + "quit " +
                SET_TEXT_COLOR_MAGENTA + "- playing chess\n" + SET_TEXT_COLOR_BLUE + "help " +
                SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }
}
