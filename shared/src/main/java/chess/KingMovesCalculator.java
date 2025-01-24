package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessMove move = tryMove(board, myPosition, 1, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 1, 0);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 0, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, 0);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 0, -1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, -1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 1, -1);
        if (move != null) {
            moves.add(move);
        }
        return moves;
    }

    private static ChessMove tryMove(ChessBoard board, ChessPosition myPosition, int x, int y) {
        int new_x = myPosition.getRow() + x;
        int new_y = myPosition.getColumn() + y;
        if (new_x <= 8 && new_x >= 1 && new_y >= 1 && new_y <= 8) {
            ChessPiece myPiece = board.getPiece(myPosition);
            ChessPosition nextPosition = new ChessPosition(new_x, new_y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                return move;
            } else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    return move;
                }
            }
        }
        return null;
    }
}
