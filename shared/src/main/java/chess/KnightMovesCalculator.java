package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessMove move = tryMove(board, myPosition, 2, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 2, -1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -2, -1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -2, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 1, 2);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 1, -2);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, 2);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, -2);
        if (move != null) {
            moves.add(move);
        }
        return moves;
    }

    private static ChessMove tryMove(ChessBoard board, ChessPosition myPosition, int change_x, int change_y) {
        int new_x = myPosition.getRow() + change_x;
        int new_y = myPosition.getColumn() + change_y;
        if (new_x <= 8 && new_x >= 1 && new_y <= 8 && new_y >= 1) {
            ChessPiece myPiece = board.getPiece(myPosition);
            ChessPosition nextPosition = new ChessPosition(new_x, new_y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                return move;
            } else {
                if (myPiece.getTeamColor() != nextPiece.getTeamColor()) {
                    return move;
                }
            }
        }
        return null;
    }
}
