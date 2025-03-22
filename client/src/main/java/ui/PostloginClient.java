package ui;

import exception.ResponseException;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PostloginClient {
    private final String serverUrl;
    private final String authToken;

    public PostloginClient(String serverUrl, String authToken) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return "Made it into postloginClient and here is my auth" + authToken;
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }
}
