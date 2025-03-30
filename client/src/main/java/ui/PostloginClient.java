package ui;

import chess.ChessGame;
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
    private final Map<Integer, Integer> games = new HashMap<>();

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
        if (params.length == 1) {
            // This is all temporary code for observing, you probably need to change how this works later
            GameplayRepl gameplayRepl = new GameplayRepl(serverUrl, authToken, server);
            gameplayRepl.run(new ChessGame(), "WHITE");
            return "";
        }
        throw new ResponseException(400, "Expected: <gameNumber>");
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            String color = params[1].toUpperCase();
            try {
                JoinRequest joinRequest = new JoinRequest(authToken, color, games.get(Integer.parseInt(params[0])));
                server.join(joinRequest);
                GameplayRepl gameplayRepl = new GameplayRepl(serverUrl, authToken, server);
                gameplayRepl.run(new ChessGame(), color);
                return "";
            } catch (NumberFormatException e) {
                throw new ResponseException(400, "Expected: <gameNumber> [WHITE|BLACK]");
            } catch (NullPointerException e) {
                throw new ResponseException(400, "Invalid game number");
            }
        }
        throw new ResponseException(400, "Expected: <id> [WHITE|BLACK]");
    }

    public String list(String... params) throws ResponseException {
        if (params.length == 0) {
            ListRequest listRequest = new ListRequest(authToken);
            ListResult listResult = server.list(listRequest);
            games.clear();
            for (GameSummary game : listResult.games()) {
                games.put(games.size() + 1, game.gameID());
            }
            return listGamesString(listResult.games(), games);
        }
        throw new ResponseException(400, "Expected no arguments after 'list'");
    }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            CreateRequest createRequest = new CreateRequest(params[0], authToken);
            CreateResult createResult = server.create(createRequest);
            games.put(games.size() + 1, createResult.gameID());
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

    private String listGamesString(List<GameSummary> games, Map<Integer, Integer> gamesMap) {
        StringBuilder result = new StringBuilder();
        if (games.isEmpty()) {
            return "No games currently.";
        }
        for (int i = 1; i <= gamesMap.size(); i++) {
            Integer gameId = gamesMap.get(i);
            if (gameId == null) {
                continue;
            }

            GameSummary currentGame = null;
            for (GameSummary game : games) {
                if (game.gameID() == gameId) {
                    currentGame = game;
                    break;
                }
            }
            appendGameToGamesList(result, currentGame);
        }
        return result.toString();
    }

    private void appendGameToGamesList(StringBuilder result, GameSummary game) {
        if (game == null) {
            return;
        }
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
}
