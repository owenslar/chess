package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessMove move = tryMove(board, myPosition, 1, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 1, 0);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 0, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, 0);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 0, -1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, -1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, -1, 1);
        if (move != null) {
            moves.add(move);
        }
        move = tryMove(board, myPosition, 1, -1);
        if (move != null) {
            moves.add(move);
        }
        ChessPiece myPiece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE && x == 1 && y == 5 && myPiece.getNumMoves() == 0) {
            ChessPiece leftRook = board.getPiece(new ChessPosition(1,1));
            ChessPiece rightRook = board.getPiece(new ChessPosition(1,8));
            if (leftRook != null && leftRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    leftRook.getTeamColor() == ChessGame.TeamColor.WHITE && leftRook.getNumMoves() == 0) {
                if (board.getPiece(new ChessPosition(1, 2)) == null &&
                        board.getPiece(new ChessPosition(1, 3)) == null &&
                        board.getPiece(new ChessPosition(1, 4)) == null) {
                    ChessMove leftCastle = new ChessMove(myPosition, new ChessPosition(x, y - 2));
                    leftCastle.setCastle(true);
                    moves.add(leftCastle);
                }
            }
            if (rightRook != null && rightRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    rightRook.getTeamColor() == ChessGame.TeamColor.WHITE && rightRook.getNumMoves() == 0) {
                if (board.getPiece(new ChessPosition(1, 6)) == null && board.getPiece(new ChessPosition(1, 7)) == null) {
                    ChessMove rightCastle = new ChessMove(myPosition, new ChessPosition(x, y + 2));
                    rightCastle.setCastle(true);
                    moves.add(rightCastle);
                }
            }
        }
        else if (myPiece.getTeamColor() == ChessGame.TeamColor.BLACK && x == 8 &&
                y == 5 && myPiece.getNumMoves() == 0) {
            ChessPiece leftRook = board.getPiece(new ChessPosition(8,1));
            ChessPiece rightRook = board.getPiece(new ChessPosition(8,8));
            if (leftRook != null && leftRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    leftRook.getTeamColor() == ChessGame.TeamColor.BLACK && leftRook.getNumMoves() == 0) {
                if (board.getPiece(new ChessPosition(8, 2)) == null &&
                        board.getPiece(new ChessPosition(8, 3)) == null &&
                        board.getPiece(new ChessPosition(8, 4)) == null) {
                    ChessMove leftCastle = new ChessMove(myPosition, new ChessPosition(x, y - 2));
                    leftCastle.setCastle(true);
                    moves.add(leftCastle);
                }
            }
            if (rightRook != null && rightRook.getPieceType() == ChessPiece.PieceType.ROOK &&
                    rightRook.getTeamColor() == ChessGame.TeamColor.BLACK && rightRook.getNumMoves() == 0) {
                if (board.getPiece(new ChessPosition(8, 6)) == null &&
                        board.getPiece(new ChessPosition(8,7)) == null) {
                    ChessMove rightCastle = new ChessMove(myPosition, new ChessPosition(x, y + 2));
                    rightCastle.setCastle(true);
                    moves.add(rightCastle);
                }
            }
        }
        return moves;
    }

    private static ChessMove tryMove(ChessBoard board, ChessPosition myPosition, int x, int y) {
        int newX = myPosition.getRow() + x;
        int newY = myPosition.getColumn() + y;
        if (newX <= 8 && newX >= 1 && newY >= 1 && newY <= 8) {
            ChessPiece myPiece = board.getPiece(myPosition);
            ChessPosition nextPosition = new ChessPosition(newX, newY);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessMove move = new ChessMove(myPosition, nextPosition);
            if (nextPiece == null) {
                return move;
            } else {
                if (nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    return move;
                }
            }
        }
        return null;
    }
}
