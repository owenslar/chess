package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && x == 2) {
            ChessPosition nextPosition = new ChessPosition(x + 2, y);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            moves.add(move);
        }
        else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && x == 7) {
            ChessPosition nextPosition = new ChessPosition(x - 2, y);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            moves.add(move);
        }
        return moves;
    }
}
