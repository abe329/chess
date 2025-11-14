package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class ChessboardRenderer {
    private final ChessGame game;
    private final boolean whitePerspective;
    private static final String[] Letters = {"a", "b", "c", "d", "e", "f", "g", "h"};

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

    private void drawHeaders(PrintStream out) {
        out.print("    ");
        int start = whitePerspective ? 0 : 7;
        int end = whitePerspective ? 7 : 0;
        int step = whitePerspective ? 1 : -1;

        for (int col = start; col != end + step; col += step) {
            out.print(" " + SET_TEXT_COLOR_WHITE + Letters[col] + " ");
        }

        out.println(RESET);
    }

    private void drawPieces(PrintStream out, int row) {
        out.print("  " + SET_TEXT_COLOR_WHITE + row + " " + RESET);

        int start = whitePerspective ? 8 : 1;
        int end = whitePerspective ? 1 : 8;
        int step = whitePerspective ? -1 : 1;

        for (int col = start; col != end + step; col += step) {
            drawSquare(out, row, col);
        }

        out.print(" " + SET_TEXT_COLOR_WHITE + row + RESET);
        out.println();
    }

    private void drawSquare(PrintStream out, int row, int col) {
        boolean lightSquare = (row + col) % 2 == 0;
        String bg = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
        out.print(bg);

        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece piece = game.getBoard().getPiece(pos);

        if (piece == null) {
            out.print(EMPTY);
        } else {
            String txt = piece.getTeamColor() == WHITE ? SET_TEXT_COLOR_BLUE : SET_TEXT_COLOR_MAGENTA;
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