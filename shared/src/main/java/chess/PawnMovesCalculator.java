package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && x == 2) {
            ChessPosition nextPosition = new ChessPosition(x + 2, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessPiece middlePiece = board.getPiece(new ChessPosition(x + 1, y));
            if (nextPiece == null && middlePiece == null) {
                ChessMove move = new ChessMove(myPosition, nextPosition);
                moves.add(move);
            }
        }
        else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && x == 7) {
            ChessPosition nextPosition = new ChessPosition(x - 2, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessPiece middlePiece = board.getPiece(new ChessPosition(x - 1, y));
            if (nextPiece == null && middlePiece == null) {
                ChessMove move = new ChessMove(myPosition, nextPosition);
                moves.add(move);
            }
        }
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && x != 7) {
            ChessPosition nextPosition = new ChessPosition(x + 1, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if (nextPiece == null) {
                ChessMove move = new ChessMove(myPosition, nextPosition);
                moves.add(move);
            }
            if (y < 8) {
                nextPosition = new ChessPosition(x + 1, y + 1);
                nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, nextPosition);
                    moves.add(move);
                }
            }
            if (y > 1) {
                nextPosition = new ChessPosition(x + 1, y - 1);
                nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, nextPosition);
                    moves.add(move);
                }
            }
        }
        else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && x != 2) {
            ChessPosition nextPosition = new ChessPosition(x - 1, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if (nextPiece == null) {
                ChessMove move = new ChessMove(myPosition, nextPosition);
                moves.add(move);
            }
            if (y < 8) {
                nextPosition = new ChessPosition(x - 1, y + 1);
                nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, nextPosition);
                    moves.add(move);
                }
            }
            if (y > 1) {
                nextPosition = new ChessPosition(x - 1, y - 1);
                nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, nextPosition);
                    moves.add(move);
                }
            }
        }
        return moves;
    }
}
