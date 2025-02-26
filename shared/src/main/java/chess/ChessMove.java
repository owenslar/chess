package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    boolean enPassant = false;
    boolean castle = false;
    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;

    public ChessMove (ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public boolean isCastle() {
        return castle;
    }

    public void setEnPassant(boolean bool) {
        enPassant = bool;
    }

    public void setCastle(boolean bool) {
        castle = bool;
    }

    @Override
    public String toString() {
        return String.format("Start at (%d, %d) go to (%d, %d)",
                this.startPosition.getRow(), this.startPosition.getColumn(),
                this.endPosition.getRow(), this.endPosition.getColumn());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) &&
                promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
