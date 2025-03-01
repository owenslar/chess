package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends DirectionalPieceMoves {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();

        addDirectionalMoves(board, moves, myPosition, myPiece, 1, 0);  // Right
        addDirectionalMoves(board, moves, myPosition, myPiece, -1, 0); // Left
        addDirectionalMoves(board, moves, myPosition, myPiece, 0, -1); // Down
        addDirectionalMoves(board, moves, myPosition, myPiece, 0, 1);  // Up

        return moves;
    }
}
