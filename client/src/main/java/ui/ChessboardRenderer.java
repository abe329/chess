package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class ChessboardRenderer {
    private final ChessGame game;
    private final boolean whitePerspective;
    private static final String EMPTY = " ";
    private static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static Integer start;
    private static Integer end;
    private static Integer step;

    public ChessboardRenderer(ChessGame game, String playerColor) {
        this.game = game;
        this.whitePerspective = playerColor.equalsIgnoreCase("WHITE");
    }

    public void displayBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println();

        int start = whitePerspective ? 8 : 1;
        int end = whitePerspective ? 1 : 8;
        int step = whitePerspective ? -1 : 1;

        drawHeaders(out);
        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {
        setBlack(out);
        out.print(SET_TEXT_COLOR_WHITE);
        drawBuffer(out);

        for (int rank = start; rank != end + step; rank += step) {
            out.print(EMPTY);
            out.print(letters[rank]);
            out.print(EMPTY);
            }
        drawBuffer(out);
    }

    private static void drawBuffer(PrintStream out) {
        out.print(EMPTY.repeat(3));
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}


// Accept a ChessGame object
// Accept perspective: WHITE or BLACK
// Print the board in ASCII (light/dark squares)