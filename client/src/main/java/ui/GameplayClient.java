package ui;

import chess.*;
import exception.ResponseException;
import server.WebSocketFacade;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.util.*;

import static ui.EscapeSequences.*;
import static websocket.commands.UserGameCommand.CommandType.*;

public class GameplayClient {
    private final String authToken;
    private final Integer gameID;
    private ChessGame game;
    private final WebSocketFacade ws;
    private final String orientation;

    public GameplayClient(String serverUrl, String authToken, Integer gameID, NotificationHandler notificationHandler, String orientation) {
        this.authToken = authToken;
        this.gameID = gameID;
        ws = new WebSocketFacade(serverUrl, notificationHandler);
        this.orientation = orientation;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> SET_TEXT_COLOR_BLUE + help();
                case "leave" -> SET_TEXT_COLOR_BLUE + leave(params);
                case "redraw" -> SET_TEXT_COLOR_BLUE + redraw(params);
                case "move" -> SET_TEXT_COLOR_BLUE + makeMove(params);
                case "resign" -> SET_TEXT_COLOR_BLUE + resign(params);
                case "highlight" -> SET_TEXT_COLOR_BLUE + highlight(params);
                default -> SET_TEXT_COLOR_RED + "\nCommand not found, here are the valid commands:\n" + SET_TEXT_COLOR_BLUE + help();
            };
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        UserGameCommand connectCommand = new UserGameCommand(CONNECT, authToken, gameID);
        ws.connect(connectCommand);
    }

    public String highlight(String... params) throws ResponseException {
        if (params.length == 1) {
            ChessPosition startPos = convertToChessPosition(params[0]);
            ChessPiece piece = game.getBoard().getPiece(startPos);
            if (piece == null) {
                throw new ResponseException(400, "No piece in the provided square");
            }
            Collection<ChessMove> validMoves = game.validMoves(startPos);
            if (validMoves.isEmpty()) {
                return stringifyGame(game, orientation, validMoves) + "\n" + SET_TEXT_COLOR_RED + "no valid moves for that piece";
            }
            return stringifyGame(game, orientation, validMoves);
        }
        throw new ResponseException(400, "Expected: highlight <location> (location is a column and row ex. a5)");
    }

    public String resign(String... params) throws ResponseException {
        if (params.length == 0) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println(SET_TEXT_COLOR_BLUE + "Are you sure you want to resign? y/n");
                GameplayRepl.printPrompt();
                String line = scanner.nextLine().trim().toLowerCase();
                if (line.equals("y")) {
                    UserGameCommand resignCommand = new UserGameCommand(RESIGN, authToken, gameID);
                    ws.resign(resignCommand);
                    return "";
                } else if (line.equals("n")) {
                    return "";
                } else {
                    System.out.println(SET_TEXT_COLOR_RED + "Invalid input. Please type 'y' or 'n'.");
                }
            }
        }
        throw new ResponseException(400, "Expected no arguments after 'resign'");
    }

    public String makeMove(String... params) throws ResponseException {
        if (params.length == 2) {
            String start = params[0];
            String end = params[1];
            ChessMove move = convertToChessMove(start, end, null);
            if (game.getBoard().getPiece(move.getStartPosition()).getPieceType()
                    == ChessPiece.PieceType.PAWN && (move.getEndPosition().getRow() == 1 || move.getEndPosition().getRow() == 8)) {
                throw new ResponseException(400, "Invalid move: promotion piece required");
            }
            if (game.getIsOver()) {
                throw new ResponseException(400, "Invalid move: the game is over");
            }
            MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
            ws.makeMove(moveCommand);
            return "";
        } else if (params.length == 3) {
            String start = params[0];
            String end = params[1];
            String promotionPiece = params[2].toUpperCase();
            ChessMove move = convertToChessMove(start, end, promotionPiece);
            if (game.getIsOver()) {
                throw new ResponseException(400, "Invalid move: the game is over");
            }
            MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
            ws.makeMove(moveCommand);
            return "";
        }
        throw new ResponseException(400, "Expected: move <start> <end> <promotionPiece> (promotionPiece may be empty)");
    }

    private ChessMove convertToChessMove(String start, String end, String promotionPiece) throws ResponseException {
        ChessPosition startPos = convertToChessPosition(start);
        ChessPosition endPos = convertToChessPosition(end);
        ChessPiece.PieceType promotionPieceType = null;
        if (promotionPiece != null) {
            try {
                promotionPieceType = ChessPiece.PieceType.valueOf(promotionPiece);
            } catch (IllegalArgumentException e) {
                throw new ResponseException(400, "Invalid promotion piece type, expected [QUEEN|ROOK|BISHOP|KNIGHT]");
            }
        }
        return new ChessMove(startPos, endPos, promotionPieceType);
    }

    private ChessPosition convertToChessPosition(String pos) throws ResponseException {
        if (pos.isEmpty()) {
            throw new ResponseException(400, "invalid move: missing location");
        }
        int col = getCol(pos);
        char rowLetter = pos.charAt(1);
        if (!Character.isDigit(rowLetter)) {
            throw new ResponseException(400, "invalid move: row must be a digit 1-8");
        }
        int row = Character.getNumericValue(rowLetter);
        return new ChessPosition(row, col);
    }

    private int getCol(String pos) {
        if (!(pos.length() == 2)) {
            throw new ResponseException(400, "invalid move: expected a letter (a-h) and a number (1-8) for each position");
        }
        char colLetter = pos.charAt(0);
        int col;
        switch (colLetter) {
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
            default -> throw new ResponseException(400, "invalid move: column must be a letter a-h");
        }
        return col;
    }

    public String leave(String... params) {
        if (params.length == 0) {
            UserGameCommand leaveCommand = new UserGameCommand(LEAVE, authToken, gameID);
            ws.leave(leaveCommand);
            return "left game";
        }
        throw new ResponseException(400, "Expected no parameters after 'leave'");
    }

    public String redraw(String... params) {
        if (params.length == 0) {
            return "\n" + stringifyGame(this.game, orientation, null);
        }
        throw new ResponseException(400, "Expected no parameters after 'redraw'");
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public String stringifyGame(ChessGame game, String orientation, Collection<ChessMove> highlightMoves) {
        StringBuilder result = new StringBuilder();
        String[] letters = {"a","b","c","d","e","f","g","h"};
        if (Objects.equals(orientation, "BLACK")) {
            letters = new String[]{"h","g","f","e","d","c","b","a"};
        }
        appendLettersRow(result, letters);
        appendChessBoard(result, game, orientation, highlightMoves);
        appendLettersRow(result, letters);
        return result.toString();
    }

    private void appendChessBoard(StringBuilder result, ChessGame game, String orientation, Collection<ChessMove> highlightMoves) {
        ChessBoard board = game.getBoard();
        boolean isHighlightSquare;
        boolean isBaseHighlightSquare;
        if (Objects.equals(orientation, "WHITE")) {
            for (int i = 8; i > 0; i--) {
                appendNumber(result, i);
                for (int j = 1; j < 9; j++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                    isHighlightSquare = false;
                    isBaseHighlightSquare = false;
                    if (highlightMoves != null) {
                        int finalI = i;
                        int finalJ = j;
                        isHighlightSquare = highlightMoves.stream()
                                .anyMatch(move -> move.getEndPosition().equals(new ChessPosition(finalI, finalJ)));
                        isBaseHighlightSquare = highlightMoves.stream()
                            .anyMatch(move -> move.getStartPosition().equals(new ChessPosition(finalI, finalJ)));
                    }
                    if (isBaseHighlightSquare) {
                        appendBaseHighlightSquare(result, piece);
                    }
                    else if (i % 2 == 0 && j % 2 == 1) {
                        if (isHighlightSquare) {
                            appendLightHighlightSquare(result, piece);
                        } else {
                            appendLightSquare(result, piece);
                        }
                    }
                    else if (i % 2 == 0) {
                        if (isHighlightSquare) {
                            appendDarkHighlightSquare(result, piece);
                        } else {
                            appendDarkSquare(result, piece);
                        }
                    }
                    else if (j % 2 == 0) {
                        if (isHighlightSquare) {
                            appendLightHighlightSquare(result, piece);
                        } else {
                            appendLightSquare(result, piece);
                        }
                    }
                    else {
                        if (isHighlightSquare) {
                            appendDarkHighlightSquare(result, piece);
                        } else {
                            appendDarkSquare(result, piece);
                        }
                    }
                }
                appendNumber(result, i);
                result.append(RESET_BG_COLOR).append("\n");
            }
        } else {
            for (int i = 1; i < 9; i++) {
                appendNumber(result, i);
                for (int j = 8; j > 0; j--) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                    isHighlightSquare = false;
                    isBaseHighlightSquare = false;
                    if (highlightMoves != null) {
                        int finalI = i;
                        int finalJ = j;
                        isHighlightSquare = highlightMoves.stream()
                                .anyMatch(move -> move.getEndPosition().equals(new ChessPosition(finalI, finalJ)));
                        isBaseHighlightSquare = highlightMoves.stream()
                                .anyMatch(move -> move.getStartPosition().equals(new ChessPosition(finalI, finalJ)));
                    }
                    if (isBaseHighlightSquare) {
                        appendBaseHighlightSquare(result, piece);
                    }
                    else if (i % 2 == 1 && j % 2 == 0) {
                        if (isHighlightSquare) {
                            appendLightHighlightSquare(result, piece);
                        } else {
                            appendLightSquare(result, piece);
                        }
                    }
                    else if (i % 2 == 1) {
                        if (isHighlightSquare) {
                            appendDarkHighlightSquare(result, piece);
                        } else {
                            appendDarkSquare(result, piece);
                        }
                    }
                    else if (j % 2 == 0) {
                        if (isHighlightSquare) {
                            appendDarkHighlightSquare(result, piece);
                        } else {
                            appendDarkSquare(result, piece);
                        }
                    }
                    else {
                        if (isHighlightSquare) {
                            appendLightHighlightSquare(result, piece);
                        } else {
                            appendLightSquare(result, piece);
                        }
                    }
                }
                appendNumber(result, i);
                result.append(RESET_BG_COLOR).append("\n");
            }
        }
    }

    private void appendBaseHighlightSquare(StringBuilder result, ChessPiece piece) {
        if (piece == null) {
            result.append(SET_BG_COLOR_HIGHLIGHT_YELLOW_DARK + SET_TEXT_COLOR_HIGHLIGHT_YELLOW_DARK).append(EMPTY);
        } else {
            result.append(SET_BG_COLOR_HIGHLIGHT_YELLOW_DARK + SET_TEXT_COLOR_HIGHLIGHT_YELLOW_DARK)
                    .append(determineChessPiece(piece))
                    .append(SET_TEXT_COLOR_HIGHLIGHT_YELLOW_DARK);
        }
    }

    private void appendLightHighlightSquare(StringBuilder result, ChessPiece piece) {
        if (piece == null) {
            result.append(SET_BG_COLOR_HIGHLIGHT_GREEN_LIGHT + SET_TEXT_COLOR_HIGHLIGHT_GREEN_LIGHT).append(EMPTY);
        } else {
            result.append(SET_BG_COLOR_HIGHLIGHT_GREEN_LIGHT + SET_TEXT_COLOR_HIGHLIGHT_GREEN_LIGHT)
                    .append(determineChessPiece(piece))
                    .append(SET_TEXT_COLOR_HIGHLIGHT_GREEN_LIGHT);
        }
    }

    private void appendDarkHighlightSquare(StringBuilder result, ChessPiece piece) {
        if (piece == null) {
            result.append(SET_BG_COLOR_HIGHLIGHT_GREEN_DARK + SET_TEXT_COLOR_HIGHLIGHT_GREEN_DARK).append(EMPTY);
        } else {
            result.append(SET_BG_COLOR_HIGHLIGHT_GREEN_DARK + SET_TEXT_COLOR_HIGHLIGHT_GREEN_DARK)
                    .append(determineChessPiece(piece))
                    .append(SET_TEXT_COLOR_HIGHLIGHT_GREEN_DARK);
        }
    }

    private void appendLightSquare(StringBuilder result, ChessPiece piece) {
        if (piece == null) {
            result.append(SET_BG_COLOR_SOFT_WHITE + SET_TEXT_COLOR_SOFT_WHITE)
                    .append(EMPTY);
        } else {
            result.append(SET_BG_COLOR_SOFT_WHITE + SET_TEXT_COLOR_SOFT_WHITE)
                    .append(determineChessPiece(piece))
                    .append(SET_TEXT_COLOR_SOFT_WHITE);
        }
    }

    private void appendDarkSquare(StringBuilder result, ChessPiece piece) {
        if (piece == null) {
            result.append(SET_BG_COLOR_LIGHT_GREEN + SET_TEXT_COLOR_LIGHT_GREEN)
                    .append(EMPTY);
        } else {
            result.append(SET_BG_COLOR_LIGHT_GREEN + SET_TEXT_COLOR_LIGHT_GREEN)
                    .append(determineChessPiece(piece))
                    .append(SET_TEXT_COLOR_LIGHT_GREEN);
        }
    }

    private void appendNumber(StringBuilder result, int i) {
        result.append(SET_BG_COLOR_CHESS_BACKGROUND + SET_TEXT_COLOR_CHESS_BACKGROUND)
                .append(" ").append(SET_TEXT_COLOR_WHITE).append(i)
                .append(SET_TEXT_COLOR_CHESS_BACKGROUND).append(" ");
    }

    private String determineChessPiece(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case QUEEN -> SET_TEXT_COLOR_WHITE_PIECE + WHITE_QUEEN;
                case KING -> SET_TEXT_COLOR_WHITE_PIECE + WHITE_KING;
                case BISHOP -> SET_TEXT_COLOR_WHITE_PIECE + WHITE_BISHOP;
                case KNIGHT -> SET_TEXT_COLOR_WHITE_PIECE + WHITE_KNIGHT;
                case ROOK -> SET_TEXT_COLOR_WHITE_PIECE + WHITE_ROOK;
                case PAWN -> SET_TEXT_COLOR_WHITE_PIECE + WHITE_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case QUEEN -> SET_TEXT_COLOR_BLACK + WHITE_QUEEN;
                case KING -> SET_TEXT_COLOR_BLACK + WHITE_KING;
                case BISHOP -> SET_TEXT_COLOR_BLACK + WHITE_BISHOP;
                case KNIGHT -> SET_TEXT_COLOR_BLACK + WHITE_KNIGHT;
                case ROOK -> SET_TEXT_COLOR_BLACK + WHITE_ROOK;
                case PAWN -> SET_TEXT_COLOR_BLACK + WHITE_PAWN;
            };
        }
    }

    private void appendLettersRow(StringBuilder result, String[] letters) {
        result.append(SET_BG_COLOR_CHESS_BACKGROUND + SET_TEXT_COLOR_CHESS_BACKGROUND + "   ");
        for (String letter : letters) {
            result.append(" " + SET_TEXT_COLOR_WHITE).append(letter).append(SET_TEXT_COLOR_CHESS_BACKGROUND).append(" ");
        }
        result.append("   ").append(RESET_BG_COLOR).append("\n");
    }

    public String help() {
        return "move <start> <end> <promotionPiece> " + SET_TEXT_COLOR_MAGENTA + "- make a move (promotionPiece can be empty)\n"
                + SET_TEXT_COLOR_BLUE + "highlight <location> " + SET_TEXT_COLOR_MAGENTA + "- highlight valid moves for a piece\n" +
                SET_TEXT_COLOR_BLUE + "redraw " + SET_TEXT_COLOR_MAGENTA + "- redraw the board\n" + SET_TEXT_COLOR_BLUE +
                "resign " + SET_TEXT_COLOR_MAGENTA + "- resign the game\n" + SET_TEXT_COLOR_BLUE + "leave " +
                SET_TEXT_COLOR_MAGENTA + "- leave the game\n" + SET_TEXT_COLOR_BLUE + "help " + SET_TEXT_COLOR_MAGENTA +
                "- give possible commands\n";

    }

}
