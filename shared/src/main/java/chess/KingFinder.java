package chess;

public class KingFinder {

    ChessBoard board;

    public KingFinder(ChessBoard board) {
        this.board = board;
    }

    public ChessPosition getKing(ChessGame.TeamColor teamColor) {
        return findKing(teamColor);
    }

    private ChessPosition findKing(ChessGame.TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currPosition = new ChessPosition(row, col);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece != null && currPiece.getPieceType() == ChessPiece.PieceType.KING && currPiece.getTeamColor() == teamColor) {
                    return currPosition;
                }
            }
        }
        return null;
    }
}
