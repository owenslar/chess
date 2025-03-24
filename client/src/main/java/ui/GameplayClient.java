package ui;

import chess.ChessGame;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class GameplayClient {
    private final String serverUrl;
    private final String authToken;
    private final ServerFacade server;

    public GameplayClient(String serverUrl, String authToken, ServerFacade server) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return SET_TEXT_COLOR_BLUE + "leaving game";
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    public String stringifyGame(ChessGame game, String orientation) {
        StringBuilder result = new StringBuilder();
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        if (Objects.equals(orientation, "BLACK")) {
            letters = new String[]{"h","g","f","e","d","c","b","a"};
        }
        appendLettersRow(result, letters);
        result.append("REST OF THE CHESS BOARD PRINTED HERE\n");
        appendLettersRow(result, letters);
        return result.toString();
    }

    private void appendLettersRow(StringBuilder result, String[] letters) {
        result.append(SET_BG_COLOR_BLUE + SET_TEXT_COLOR_BLUE + "   ");
        for (String letter : letters) {
            result.append("  " + RESET_TEXT_COLOR).append(letter).append(SET_TEXT_COLOR_BLUE).append(" ");
        }
        result.append("   ").append(RESET_BG_COLOR).append("\n");
    }
}
