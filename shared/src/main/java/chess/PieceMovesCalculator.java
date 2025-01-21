package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType type = board.getPiece(myPosition).getPieceType();
        return switch (type) {
            case BISHOP -> BishopMovesCalculator.pieceMoves(board, myPosition);
            case KING -> KingMovesCalculator.pieceMoves(board, myPosition);
            case KNIGHT -> KnightMovesCalculator.pieceMoves(board, myPosition);
            case PAWN -> PawnMovesCalculator.pieceMoves(board, myPosition);
            case ROOK -> RookMovesCalculator.pieceMoves(board, myPosition);
            default -> new ArrayList<>();
        };
    }
}
