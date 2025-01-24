package chess;

import java.util.Collection;

public abstract class PieceMovesCalculator {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
