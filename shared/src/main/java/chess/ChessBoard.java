package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard(){

    }
    public ChessBoard(ChessBoard other) {
        this.squares = new ChessPiece[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = other.squares[row][col];
                if (piece != null) {
                    this.squares[row][col] = new ChessPiece(piece); // calls ChessPiece copy constructor
                }
            }
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

    public void movePiece(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = getPiece(start);
        if (piece == null) {
            throw new IllegalArgumentException("No piece at start position: " + start);
        }
        ChessPosition end = move.getEndPosition();
        squares[start.getRow() - 1][start.getColumn() - 1] = null;
        squares[end.getRow() - 1][end.getColumn() - 1] = piece;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            squares[end.getRow() - 1][end.getColumn() - 1] = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];

        // Order of back row
        ChessPiece.PieceType[] backRank = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.ROOK
        };

        // Place white (row 1) and black (row 8)
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(1, col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, backRank[col - 1]));
            addPiece(new ChessPosition(8, col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, backRank[col - 1]));
        }

        // Place pawns (row 2 = white, row 7 = black)
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(2, col),
                    new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col),
                    new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
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
}
