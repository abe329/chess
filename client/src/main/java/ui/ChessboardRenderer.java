package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessboardRenderer {
    private final ChessGame game;
    private final boolean whitePerspective;
    private static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};

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

        for (int rank = start; rank != end + step; rank += step) {
            drawPieces(out, rank);
        }

        drawHeaders(out);
        out.print(RESET);
    }

    private void drawHeaders(PrintStream out) {
        out.print("   ");
        int start = whitePerspective ? 0 : 7;
        int end = whitePerspective ? 7 : 0;
        int step = whitePerspective ? 1 : -1;

        for (int col = start; col != end + step; col += step) {
            out.print("  " + SET_TEXT_COLOR_WHITE + letters[col] + "  ");
        }

        out.println(RESET);
    }

    private void drawPieces(PrintStream out, int rank) {
        out.print(" " + SET_TEXT_COLOR_WHITE + rank + " " + RESET);

        int start = whitePerspective ? 8 : 1;
        int end = whitePerspective ? 1 : 8;
        int step = whitePerspective ? -1 : 1;

        for (int col = start; col != end + step; col += step) {
            drawSquare(out, rank, col);
        }

        out.print(" " + SET_TEXT_COLOR_WHITE + rank + RESET);
        out.println();
    }

    private void drawSquare(PrintStream out, int rank, int col) {
        out.print("*");
        out.print(RESET);
    }
}


// Accept a ChessGame object
// Accept perspective: WHITE or BLACK
// Print the board in ASCII (light/dark squares)