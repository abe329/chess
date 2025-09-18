package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Calculates moves for each piece
 * Still needs:
 *    - all Pawn moves
 *    - Check if move is blocked
 *    - Capturing enemy pieces
 */

public class PieceMovesCalculator {
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        PieceMovement pm = PieceMovement.valueOf(piece.getPieceType().name());
        int[][] directions = pm.directions;
        boolean slider = pm.slider;
        ArrayList<ChessMove> moves = new ArrayList<>();

        for(int[] i : directions) {
            int dx = i[0];
            int dy = i[1];
            int row = myPosition.getRow() + dx;
            int col = myPosition.getColumn() + dy;

            while(isOnBoard(row, col)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                if (emptySquare(board, row, col)) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                if (!slider) break; //Knight and King don't move continuously

                row += dx;
                col += dy;
            }
        }
        return moves;
    }

    public enum PieceMovement {
        BISHOP(new int[][] {{1,1}, {-1,1}, {1,-1}, {-1,-1}}, true),
        ROOK(new int[][] {{0,1}, {0,-1}, {1,0}, {-1,0}}, true),
        QUEEN(new int[][] {{0,1}, {1,1}, {0, -1}, {-1,1}, {1,0}, {1,-1}, {-1,0}, {-1,-1}}, true),
        KING(new int[][] {{0,1}, {1,1}, {0, -1}, {-1,1}, {1,0}, {1,-1}, {-1,0}, {-1,-1}}, false),
        KNIGHT(new int[][] {{-1,-2}, {-2,-1}, {1,-2}, {2,-1}, {-2,1}, {-1,2}, {1,2}, {2,1}}, false),
        PAWN(new int[0][0], false);

        public final int[][] directions;
        public final boolean slider;

        PieceMovement(int[][] directions, boolean slider) {
            this.directions = directions;
            this.slider = slider;
        }
    }

    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private boolean emptySquare(ChessBoard board, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);
        return board.getPiece(position) == null;
    }
}