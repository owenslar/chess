package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
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

    private static ChessMove tryMove(ChessBoard board, ChessPosition myPosition, int changeX, int changeY) {
        int newX = myPosition.getRow() + changeX;
        int newY = myPosition.getColumn() + changeY;
        if (newX <= 8 && newX >= 1 && newY <= 8 && newY >= 1) {
            ChessPiece myPiece = board.getPiece(myPosition);
            ChessPosition nextPosition = new ChessPosition(newX, newY);
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
