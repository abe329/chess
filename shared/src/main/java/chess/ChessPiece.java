package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
            // return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            int[][] options = {{1, 1}, {-1, 1}, {1, -1}, {-1,-1}};
            ArrayList<ChessMove> moves = new ArrayList<>();

            for(int[] i : options) {
                int dx = i[0];
                int dy = i[1];
                int row = myPosition.getRow() + dx;
                int col = myPosition.getColumn() + dy;

                while(isOnBoard(row, col)) {
                    // System.out.println(String.format("%d, %d -- dx: %d, dy: %d", row, col, dx, dy));
                    ChessPosition newPosition = new ChessPosition(row, col);
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    row += dx;
                    col += dy;
                    }
            }

            return moves;
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            // return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            int[][] options = {{0, 1}, {0, -1}, {1, 0}, {-1,0}};
            ArrayList<ChessMove> moves = new ArrayList<>();

            for(int[] i : options) {
                int dx = i[0];
                int dy = i[1];
                int row = myPosition.getRow() + dx;
                int col = myPosition.getColumn() + dy;

                while(isOnBoard(row, col)) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    row += dx;
                    col += dy;
                }
            }

            return moves;
        }

        return List.of();
    }
    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }


}
