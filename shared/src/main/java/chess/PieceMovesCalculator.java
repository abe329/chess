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
        PieceMovement pieceMoves = PieceMovement.valueOf(piece.getPieceType().name());
        int[][] directions = pieceMoves.directions;
        boolean slider = pieceMoves.slider;
        ArrayList<ChessMove> moves = new ArrayList<>();

        // PawnMovement does all pawn moves
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return PawnMovement(board, myPosition);
        }

        for(int[] i : directions) {
            int dx = i[0];
            int dy = i[1];
            int row = myPosition.getRow() + dx;
            int col = myPosition.getColumn() + dy;

            while(isOnBoard(row, col)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                     if (target.getTeamColor() != piece.getTeamColor()) {
                         moves.add(new ChessMove(myPosition, newPosition, null));
                     }
                     break;
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

    private Collection<ChessMove> PawnMovement(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int pawnDirection;
        // real proud of this one :)
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) pawnDirection= 1; else { pawnDirection= -1; }
        int pawnRow = row + pawnDirection;

        if (isOnBoard(pawnRow, col) && board.getPiece(new ChessPosition(pawnRow, col)) == null) {
            // Base move
            moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow, col), null));
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row == 2 || (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row == 7)) {
                // Initial 2 square move
                if (board.getPiece(new ChessPosition(pawnRow + pawnDirection, col)) == null) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(pawnRow + pawnDirection, col), null));
                }
            }
        }
        int[] captureColumns = {col - 1, col + 1};
        for (int cCol : captureColumns) {
            if(isOnBoard(pawnRow, cCol)) {
                ChessPiece target = board.getPiece(new ChessPosition(pawnRow, cCol));
                if(target != null && target.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition,new ChessPosition(pawnRow, cCol), null));
                }
            }
        }

        return moves;
    }

    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}