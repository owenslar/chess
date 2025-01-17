package chess;

import java.util.Collection;

public class PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
//        if (type.equals(ChessPiece.PieceType.BISHOP)) {
//            return BishopMovesCalculator.pieceMoves(board, myPosition);
//        }
        return BishopMovesCalculator.pieceMoves(board, myPosition);
    }
}
