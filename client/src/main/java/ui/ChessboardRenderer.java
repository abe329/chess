package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessboardRenderer {
    private final ChessGame game;
    private final boolean whitePerspective;
    private static final String[] LETTERS = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private final Set<ChessPosition> highlightedSquares = new HashSet<>();


    public ChessboardRenderer(ChessGame game, String playerColor) {
        this.game = game;
        this.whitePerspective = playerColor.equalsIgnoreCase("WHITE");
    }

    public void displayBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println();
        drawHeaders(out);

        int start = whitePerspective ? 8 : 1;
        int end = whitePerspective ? 1 : 8;
        int step = whitePerspective ? -1 : 1;

        for (int row = start; row != end + step; row += step) {
            drawPieces(out, row);
        }

        drawHeaders(out);
        out.print(RESET);
    }

    public void highlight(ChessPosition pos, Collection<ChessMove> moves) {
        highlightedSquares.clear();

        // Highlight the starting square
        highlightedSquares.add(pos);

        // Highlight all move destinations
        for (ChessMove m : moves) {
            highlightedSquares.add(m.getEndPosition());
        }

        displayBoard();

        // Remove highlighting after display (optional)
        highlightedSquares.clear();

    }

    private void drawHeaders(PrintStream out) {
        out.print("    ");
        if (whitePerspective) {
            for (int col = 0; col < 8; col++) {
                out.print(" " + SET_TEXT_COLOR_WHITE + LETTERS[col] + " ");
            }
        } else {
            for (int col = 7; col >= 0; col--) {
                out.print(" " + SET_TEXT_COLOR_WHITE + LETTERS[col] + " ");
            }
        }

        out.println(RESET);
    }

    private void drawPieces(PrintStream out, int row) {
        out.print("  " + SET_TEXT_COLOR_WHITE + row + " " + RESET);

        if (whitePerspective) {
            for (int col = 1; col <= 8; col++) {
                drawSquare(out, row, col);
            }
        } else {
            for (int col = 8; col >= 1; col--) {
                drawSquare(out, row, col);
            }
        }

        out.print(" " + SET_TEXT_COLOR_WHITE + row + RESET);
        out.println();
    }

    private void drawSquare(PrintStream out, int row, int col) {
        ChessPosition position = new ChessPosition(row, col);

        if (highlightedSquares.contains(position)) {
            out.print(SET_BG_COLOR_BLUE);
        } else {
            boolean darkSquare = (row + col) % 2 == 0;
            String bg = darkSquare ? SET_BG_COLOR_DARK_BROWN : SET_BG_COLOR_LIGHT_BROWN;
            out.print(bg);
        }

        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = game.getBoard().getPiece(pos);

        if (piece == null) {
            out.print(EMPTY);
        } else {
            String txt = piece.getTeamColor() == WHITE ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_DARK_GREY;
            out.print(txt + codedPiece(piece));
        }

        out.print(RESET);
    }

    private String codedPiece(ChessPiece piece) {
        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        };
    }
}


// Accept a ChessGame object
// Accept perspective: WHITE or BLACK
// Print the board in ASCII (light/dark squares)