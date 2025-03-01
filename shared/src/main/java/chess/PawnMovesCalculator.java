package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public void addDoublePawnMove(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, int initialRow, int direction, ChessGame.TeamColor teamColor) {
        ChessPiece myPiece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if (myPiece.getTeamColor() == teamColor && x == initialRow) {
            ChessPosition nextPosition = new ChessPosition(x + 2 * direction, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            ChessPiece middlePiece = board.getPiece(new ChessPosition(x + direction, y));

            if (nextPiece == null && middlePiece == null) {
                ChessMove move = new ChessMove(myPosition, nextPosition);
                moves.add(move);
            }
        }
    }

    public void addBasePawnMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, ChessGame.TeamColor teamColor, int direction, int boundary) {
        ChessPiece myPiece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if (myPiece.getTeamColor() == teamColor && x != boundary) {
            ChessPosition nextPosition = new ChessPosition(x + direction, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if (nextPiece == null) {
                ChessMove move = new ChessMove(myPosition, nextPosition);
                moves.add(move);
            }
            if (y < 8) {
                nextPosition = new ChessPosition(x + direction, y + 1);
                nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, nextPosition);
                    moves.add(move);
                }
            }
            if (y > 1) {
                nextPosition = new ChessPosition(x + direction, y - 1);
                nextPiece = board.getPiece(nextPosition);
                if (nextPiece != null && nextPiece.getTeamColor() != myPiece.getTeamColor()) {
                    ChessMove move = new ChessMove(myPosition, nextPosition);
                    moves.add(move);
                }
            }
        }
    }

    public void addPromotionMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, ChessGame.TeamColor teamColor, int direction, int promotionRow) {
        ChessPiece myPiece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if (myPiece.getTeamColor() == teamColor && x == promotionRow) {
            ChessPosition nextPosition = new ChessPosition(x + direction, y);
            ChessPiece nextPiece = board.getPiece(nextPosition);
            if (nextPiece == null) {
                addPromotionMovesToCollection(moves, myPosition, nextPosition);
            }
            if (y < 8) {
                ChessPosition rightPosition = new ChessPosition(x + direction, y + 1);
                ChessPiece rightPiece = board.getPiece(rightPosition);
                if (rightPiece != null && rightPiece.getTeamColor() != myPiece.getTeamColor()) {
                    addPromotionMovesToCollection(moves, myPosition, rightPosition);
                }
            }
            if (y > 1) {
                ChessPosition leftPosition = new ChessPosition(x + direction, y - 1);
                ChessPiece leftPiece = board.getPiece(leftPosition);
                if (leftPiece != null && leftPiece.getTeamColor() != myPiece.getTeamColor()) {
                    addPromotionMovesToCollection(moves, myPosition, leftPosition);
                }
            }
        }
    }

    private void addPromotionMovesToCollection(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
    }

    public void addEnPassantMoves(ChessBoard board, Collection<ChessMove> moves, ChessPosition myPosition, ChessGame.TeamColor teamColor, int direction, int enPassantRow) {
        ChessPiece myPiece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();

        if (myPiece.getTeamColor() == teamColor && x == enPassantRow) {
            if (y < 8) {
                ChessPiece rightPawn = board.getPiece(new ChessPosition(x, y + 1));
                if (rightPawn != null && rightPawn.getPieceType() == ChessPiece.PieceType.PAWN && rightPawn.getNumMoves() == 1 && rightPawn.getJustMoved()) {
                    ChessMove move = new ChessMove(myPosition, new ChessPosition(x + direction, y + 1));
                    move.setEnPassant(true);
                    moves.add(move);
                }
            }
            if (y > 1) {
                ChessPiece leftPawn = board.getPiece(new ChessPosition(x, y - 1));
                if (leftPawn != null && leftPawn.getPieceType() == ChessPiece.PieceType.PAWN && leftPawn.getNumMoves() == 1 && leftPawn.getJustMoved()) {
                    ChessMove move = new ChessMove(myPosition, new ChessPosition(x + direction, y - 1));
                    move.setEnPassant(true);
                    moves.add(move);
                }
            }
        }
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addDoublePawnMove(board, moves, myPosition, 2, 1, ChessGame.TeamColor.WHITE);
        addDoublePawnMove(board, moves, myPosition, 7, -1, ChessGame.TeamColor.BLACK);

        addBasePawnMoves(board, moves, myPosition, ChessGame.TeamColor.WHITE, 1, 7);
        addBasePawnMoves(board, moves, myPosition, ChessGame.TeamColor.BLACK, -1, 2);

        addPromotionMoves(board, moves, myPosition, ChessGame.TeamColor.WHITE, 1, 7);
        addPromotionMoves(board, moves, myPosition, ChessGame.TeamColor.BLACK, -1, 2);

        addEnPassantMoves(board, moves, myPosition, ChessGame.TeamColor.WHITE, 1, 5);
        addEnPassantMoves(board, moves, myPosition, ChessGame.TeamColor.BLACK, -1, 4);

        return moves;
    }
}

