package chess;

import java.util.ArrayList;
import java.util.Collection;

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
        PieceMovesCalculator calc = new PieceMovesCalculator();
        return calc.getMoves(board,myPosition);
    }

    /**
     * Calculates moves for each piece
     * Still needs:
     *    - all Pawn moves
     *    - Check if move is blocked
     *    - Capturing enemy pieces
     */

    public static class PieceMovesCalculator {
        public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPiece piece = board.getPiece(myPosition);
            // int[][] directions = getDirections(piece);
            PieceDirections pd = getDirections(piece);
            int[][] directions = pd.Directions;
            boolean rider = pd.Rider;
            ArrayList<ChessMove> moves = new ArrayList<>();

            for(int[] i : directions) {
                int dx = i[0];
                int dy = i[1];
                int row = myPosition.getRow() + dx;
                int col = myPosition.getColumn() + dy;

                while(isOnBoard(row, col)) {
                    // System.out.println(String.format("%d, %d -- dx: %d, dy: %d", row, col, dx, dy));
                    ChessPosition newPosition = new ChessPosition(row, col);
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    if (!rider) break;
                    row += dx;
                    col += dy;
                }
            }

            return moves;
        }

        public static class PieceDirections {
            public final int[][] Directions;
            public final boolean Rider;

            public PieceDirections(int[][] Directions, boolean Rider) {
                this.Directions = Directions;
                this.Rider = Rider;
            }
        }

        private static PieceDirections getDirections(ChessPiece piece) {
            int[][] directions;
            boolean rider = true;
            if (piece.getPieceType() == PieceType.BISHOP) {
                directions = new int[][] {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
            } else if (piece.getPieceType() == PieceType.ROOK) {
                directions = new int[][] {{0,1}, {0,-1}, {1,0}, {-1,0}};
            } else if (piece.getPieceType() == PieceType.QUEEN) {
                directions = new int[][] {{0,1}, {1,1}, {0, -1}, {-1,1}, {1,0}, {1,-1}, {-1,0}, {-1,-1}};
            } else if (piece.getPieceType() == PieceType.KING) {
                directions = new int[][] {{0,1}, {1,1}, {0, -1}, {-1,1}, {1,0}, {1,-1}, {-1,0}, {-1,-1}};
                rider = false;
            } else if (piece.getPieceType() == PieceType.KNIGHT) {
                directions = new int[][] {{-1,-2}, {-2,-1}, {1,-2}, {2,-1}, {-2,1}, {-1,2}, {1,2}, {2,1}};
                rider = false;
            } else {
                directions = new int[0][0];
                rider = false;
            }
            return new PieceDirections(directions, rider);
        }

        private boolean isOnBoard(int row, int col) {
            return row >= 1 && row <= 8 && col >= 1 && col <= 8;
        }
    }

}
