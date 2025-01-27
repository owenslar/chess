package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        Collection<ChessMove> moves = new ArrayList<>();
        while (x < 8 && y < 8) {
//            System.out.println("Current Moves: " + moves);
            ChessPosition nextPosition = new ChessPosition(x + 1, y + 1);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                moves.add(move);
            }
            else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(move);
                }
                break;
            }
            x += 1;
            y += 1;
        }
        x = myPosition.getRow();
        y = myPosition.getColumn();
        while (x < 8 && y > 1) {
            ChessPosition nextPosition = new ChessPosition(x + 1, y - 1);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                moves.add(move);
            }
            else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(move);
                }
                break;
            }
            x += 1;
            y -= 1;
        }
        x = myPosition.getRow();
        y = myPosition.getColumn();
        while (x > 1 && y < 8) {
            ChessPosition nextPosition = new ChessPosition(x - 1, y + 1);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                moves.add(move);
            }
            else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(move);
                }
                break;
            }
            x -= 1;
            y += 1;
        }
        x = myPosition.getRow();
        y = myPosition.getColumn();
        while (x > 1 && y > 1) {
            ChessPosition nextPosition = new ChessPosition(x - 1, y - 1);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                moves.add(move);
            }
            else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(move);
                }
                break;
            }
            x -= 1;
            y -= 1;
        }

        return moves;
    }
}
