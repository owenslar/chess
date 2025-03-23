package ui;

import dtos.GameSummary;
import exception.ResponseException;
import requestresult.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ui.EscapeSequences.*;

public class PostloginClient {
    private final String serverUrl;
    private final String authToken;
    private final ServerFacade server;
    private Map<Integer, String> games = new HashMap<>();

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
                case "list" -> SET_TEXT_COLOR_BLUE + list(params);
                case "join" -> SET_TEXT_COLOR_BLUE + join(params);
                case "observe" -> SET_TEXT_COLOR_BLUE + observe(params);
                default -> SET_TEXT_COLOR_RED + "\nCommand not found, here are the valid commands:\n" + SET_TEXT_COLOR_BLUE + help();
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

    public String list(String... params) throws ResponseException {
        if (params.length == 0) {
            ListRequest listRequest = new ListRequest(authToken);
            ListResult listResult = server.list(listRequest);
            for (GameSummary game : listResult.games()) {
                games.put(game.gameID(), game.gameName());
            }
            return listGamesString(listResult.games());
        }
        throw new ResponseException(400, "Expected no arguments after 'list'");
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateRequest createRequest = new CreateRequest(params[0], authToken);
            CreateResult createResult = server.create(createRequest);
            games.put(createResult.gameID(), params[0]);
            return "Game created successfully";
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String logout() throws ResponseException {
        server.logout(new LogoutRequest(authToken));
        return "logout successful";
    }

    public String help() {
        return "create <NAME> " + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE +
                "list " + SET_TEXT_COLOR_MAGENTA + "- games\n" + SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK] "
                + SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE + "observe <ID> " +
                SET_TEXT_COLOR_MAGENTA + "- a game\n" + SET_TEXT_COLOR_BLUE + "logout " + SET_TEXT_COLOR_MAGENTA +
                "- when you are done\n" + SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA + "- with possible commands\n";
    }

    private String listGamesString(List<GameSummary> games) {
        StringBuilder result = new StringBuilder();
        if (games.isEmpty()) {
            return "No games currently.";
        }
        for (GameSummary game : games) {
            result.append(game.gameID()).append(". ").append("Game name: ")
                    .append(game.gameName())
                    .append("   White");
            if (game.whiteUsername() != null) {
                result.append(": ").append(game.whiteUsername());
            }
            else {
                result.append(" empty");
            }
            result.append("   Black");
            if (game.blackUsername() != null) {
                result.append(": ").append(game.blackUsername());
            } else {
                result.append(" empty");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
