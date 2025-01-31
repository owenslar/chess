package chess;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessPiece> {
    private final ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    public void executeMove(ChessMove move, ChessPiece movingPiece) {
        squares[move.startPosition.getRow()][move.startPosition.getColumn()] = null;
        ChessPiece newPiece;
        if (move.promotionPiece != null) {
            newPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        } else {
            newPiece = new ChessPiece(movingPiece.getTeamColor(), movingPiece.getPieceType());
        }
        squares[move.endPosition.getRow()][move.endPosition.getColumn()] = newPiece;
    }

    @Override
    public Iterator<ChessPiece> iterator() {
        return new ChessBoardIterator();
    }

    private class ChessBoardIterator implements Iterator<ChessPiece> {
        private int row = 0;
        private int col = 0;

        @Override
        public boolean hasNext() {
            return row < 8 && col < 8;
        }

        @Override
        public ChessPiece next() {
            ChessPiece piece = squares[row][col];
            col++;
            if (col == 8) {
                col = 0;
                row++;
            }
            return piece;
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder("ChessBoard{\n");
        for (ChessPiece[] row : squares) {
            boardString.append(" [");
            for (ChessPiece piece : row) {
                if (piece == null) {
                    boardString.append("null, ");
                }
                else {
                    boardString.append(piece).append(", ");
                }
            }
            boardString.append("],\n");
        }
        boardString.append("}");
        return boardString.toString();
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }

        // add pawns
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

        // add White pieces
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);

        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);

        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        // add Black Pieces
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);

        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }
}
