package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor team;
    private boolean gameOver = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.team = ChessGame.TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) { return Collections.emptyList(); }

        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            ChessBoard copy = new ChessBoard(board);
            copy.movePiece(move);

            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(copy);

            if (!tempGame.isInCheck(piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (gameOver) {
            throw new InvalidMoveException("Game is over");
        }

        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position.");
        }

        if (piece.getTeamColor() != team) {
            throw new InvalidMoveException("Not your turn.");
        }

        if (validMoves(start).contains(move)) {
            board.movePiece(move);
            switchTeam();
        } else {
            throw new InvalidMoveException("That move isn't valid.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(pos);

                if (isEnemy(piece, teamColor) && attackingKing(piece, pos, kingPos)) {
                     return true;
                }
            }
        }
        return false;
    }

    private boolean isEnemy(ChessPiece piece, TeamColor teamColor) {
        return piece != null && piece.getTeamColor() != teamColor;
    }

    private boolean attackingKing(ChessPiece piece, ChessPosition pos, ChessPosition kingPos) {
        for (ChessMove move : piece.pieceMoves(board, pos)) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
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
        ChessPosition kingPos = findKing(teamColor);
        boolean killToEscape = false;
        Collection<ChessMove> canHeMove = validMoves(kingPos);
        Collection<ChessMove> anyMoves = teamMoves(teamColor);
        for (ChessMove move : anyMoves) {
            ChessBoard copy = new ChessBoard(board);
            copy.movePiece(move);

            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(copy);

            if (!tempGame.isInCheck(teamColor)) {
                killToEscape = true;
            }
        }

        return isInCheck(teamColor) && canHeMove.isEmpty() && !killToEscape;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> anyMoves = teamMoves(teamColor);
        return !isInCheck(teamColor) && anyMoves.isEmpty();
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

    private void switchTeam() {
        if (team == TeamColor.WHITE) { team = TeamColor.BLACK; }
        else {
            team = TeamColor.WHITE;
        }
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) { continue; }

                if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    private Collection<ChessMove> teamMoves(TeamColor teamColor) {
        Collection<ChessMove> anyMoves = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if (piece == null) { continue; }
                if (piece.getTeamColor().equals(teamColor)) {
                    anyMoves.addAll(validMoves(pos));
                }
            }
        }
        return anyMoves;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", team=" + team +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && team == chessGame.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, team);
    }
}
