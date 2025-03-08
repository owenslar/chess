package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(startPosition);
        Collection<ChessMove> pieceMoves = currPiece.pieceMoves(board, startPosition);
        for (ChessMove move : pieceMoves) {
            if (!move.isCastle()) {
                ChessBoard clonedBoard = new ChessBoard(board);
                clonedBoard.executeMove(move, currPiece);
                ChessGame dummyGame = new ChessGame();
                dummyGame.setBoard(clonedBoard);
                if (!dummyGame.isInCheck(currPiece.getTeamColor())) {
                    validMoves.add(move);
                }
            } else if (move.isCastle() && !isInCheck(currPiece.getTeamColor())) {
                ChessBoard clonedBoard = new ChessBoard(board);
                ChessPosition midCastleStartPos = new ChessPosition(move.startPosition.getRow(), move.startPosition.getColumn());
                ChessPosition midCastleEndPos = new ChessPosition(move.startPosition.getRow(), (move.startPosition.getColumn() +
                                (move.endPosition.getColumn() - move.startPosition.getColumn()) / 2));
                ChessMove midCastle = new ChessMove(midCastleStartPos, midCastleEndPos);
                clonedBoard.executeMove(midCastle, currPiece);
                ChessGame dummyGame = new ChessGame();
                dummyGame.setBoard(clonedBoard);
                if (!dummyGame.isInCheck(currPiece.getTeamColor())) {
                    ChessBoard clonedBoard2 = new ChessBoard(board);
                    clonedBoard2.executeMove(move, currPiece);
                    ChessGame dummyGame2 = new ChessGame();
                    dummyGame.setBoard(clonedBoard2);
                    if (!dummyGame2.isInCheck(currPiece.getTeamColor())) {
                        validMoves.add(move);
                    }
                }
            }
        }
        return validMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece != null && movingPiece.getTeamColor() == this.getTeamTurn()) {
            Collection<ChessMove> validMoves = this.validMoves(move.getStartPosition());
            for (ChessMove validMove : validMoves) {
                if (move.equals(validMove)) {
                    if (movingPiece.getPieceType() == ChessPiece.PieceType.KING &&
                            Math.abs((move.endPosition.getColumn() - move.startPosition.getColumn())) > 1) {
                        move.setCastle(true);
                    }
                    else if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN &&
                            move.startPosition.getColumn() != move.endPosition.getColumn() &&
                            board.getPiece(move.endPosition) == null) {
                        move.setEnPassant(true);
                    }
                    board.executeMove(move, movingPiece);
                    if (this.getTeamTurn() == TeamColor.WHITE) {
                        this.setTeamTurn(TeamColor.BLACK);
                    } else {
                        this.setTeamTurn(TeamColor.WHITE);
                    }
                    return;
                }
            }
            throw new InvalidMoveException("Invalid move for this piece");
        } else {
            throw new InvalidMoveException("Tried to move wrong color or null piece");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        KingFinder kingFinder = new KingFinder(board);
        ChessPosition kingPos = kingFinder.getKing(teamColor);

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currPosition = new ChessPosition(row, col);
                ChessPiece currPiece = board.getPiece(currPosition);

                // Skip empty squares or pieces of the same team
                if (currPiece == null || currPiece.getTeamColor() == teamColor) {
                    continue;
                }

                // Check if any of this piece's moves can reach the king
                for (ChessMove move : currPiece.pieceMoves(board, currPosition)) {
                    if (move.getEndPosition().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && checkForNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && checkForNoValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean checkForNoValidMoves(TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currPosition = new ChessPosition(row, col);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> validMoves = this.validMoves(currPosition);
                    if (!validMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
