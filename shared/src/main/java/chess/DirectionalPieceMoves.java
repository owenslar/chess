package chess;

import java.util.Collection;

public abstract class DirectionalPieceMoves implements PieceMovesCalculator {

    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);

    public void addDirectionalMoves(ChessBoard board, Collection<ChessMove> moves,
                                    ChessPosition myPosition, ChessPiece myPiece,
                                    int xIncrement, int yIncrement) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        while (x + xIncrement > 0 && x + xIncrement <= 8 && y + yIncrement > 0 && y + yIncrement <= 8) {
            x += xIncrement;
            y += yIncrement;
            ChessPosition nextPosition = new ChessPosition(x, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);

            if (nextPiece == null) {
                moves.add(move);
            } else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(move);
                }
                break;
            }
        }
    }
}
