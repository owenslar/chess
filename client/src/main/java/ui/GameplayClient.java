package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class GameplayClient {
    private final String serverUrl;
    private final String authToken;
    private final ServerFacade server;
    ChessGame game;

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
        String[] letters = {"a","b","c","d","e","f","g","h"};
        if (Objects.equals(orientation, "BLACK")) {
            letters = new String[]{"h","g","f","e","d","c","b","a"};
        }
        appendLettersRow(result, letters);
        appendChessBoard(result, game, orientation);
        appendLettersRow(result, letters);
        return result.toString();
    }

    private void appendChessBoard(StringBuilder result, ChessGame game, String orientation) {
        ChessBoard board = game.getBoard();
        if (Objects.equals(orientation, "WHITE")) {
            for (int i = 8; i > 0; i--) {
                appendNumber(result, i);
                for (int j = 1; j < 9; j++) {
                    ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                    if (i % 2 == 0 && j % 2 == 1) {
                        appendLightSquare(result, piece);
                    }
                    else if (i % 2 == 0) {
                        appendDarkSquare(result, piece);
                    }
                    else if (j % 2 == 0) {
                        appendLightSquare(result, piece);
                    }
                    else {
                        appendDarkSquare(result, piece);
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
                    if (i % 2 == 1 && j % 2 == 0) {
                        appendLightSquare(result, piece);
                    }
                    else if (i % 2 == 1) {
                        appendDarkSquare(result, piece);
                    }
                    else if (j % 2 == 0) {
                        appendDarkSquare(result, piece);
                    }
                    else {
                        appendLightSquare(result, piece);
                    }
                }
                appendNumber(result, i);
                result.append(RESET_BG_COLOR).append("\n");
            }
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

}
