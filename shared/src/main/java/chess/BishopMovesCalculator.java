package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends DirectionalPieceMoves {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();

        addDirectionalMoves(board, moves, myPosition, myPiece, 1, 1);  // Up-right
        addDirectionalMoves(board, moves, myPosition, myPiece, 1, -1); // Up-left
        addDirectionalMoves(board, moves, myPosition, myPiece, -1, 1); // Down-right
        addDirectionalMoves(board, moves, myPosition, myPiece, -1, -1); // Down-left



        return moves;
    }
}