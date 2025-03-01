package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends DirectionalPieceMoves {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();

        addDirectionalMoves(board, moves, myPosition, myPiece, 1, 1);  // Up-right
        addDirectionalMoves(board, moves, myPosition, myPiece, 1, -1); // Up-left
        addDirectionalMoves(board, moves, myPosition, myPiece, -1, 1); // Down-right
        addDirectionalMoves(board, moves, myPosition, myPiece, -1, -1); // Down-left

        addDirectionalMoves(board, moves, myPosition, myPiece, 1, 0);  // Right
        addDirectionalMoves(board, moves, myPosition, myPiece, -1, 0); // Left
        addDirectionalMoves(board, moves, myPosition, myPiece, 0, -1); // Down
        addDirectionalMoves(board, moves, myPosition, myPiece, 0, 1);  // Up

        return moves;
    }
}
