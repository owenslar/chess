package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
        switch (type) {
            case BISHOP:
                return BishopMovesCalculator.pieceMoves(board, myPosition);
            case KING:
                return KingMovesCalculator.pieceMoves(board, myPosition);
        }
        return new ArrayList<>();
    }
}
