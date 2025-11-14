//package ui;
//
//import java.io.PrintStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Random;
//
//import static ui.EscapeSequences.*;
//
//public class TicTacToe {
//
//    // Board dimensions.
//    private static final int BOARD_SIZE_IN_SQUARES = 3;
//    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
//    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
//
//    // Padded characters.
//    private static final String EMPTY = "   ";
//    private static final String X = " X ";
//    private static final String O = " O ";
//
//    private static Random rand = new Random();
//
//
//    public static void main(String[] args) {
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//
//        out.print(ERASE_SCREEN);
//
//        drawHeaders(out);
//
//        drawTicTacToeBoard(out);
//
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void drawHeaders(PrintStream out) {
//
//        setBlack(out);
//
//        String[] headers = { "TIC", "TAC", "TOE" };
//        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//            drawHeader(out, headers[boardCol]);
//
//            if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
//            }
//        }
//
//        out.println();
//    }
//
//    private static void drawHeader(PrintStream out, String headerText) {
//        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
//        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
//
//        out.print(EMPTY.repeat(prefixLength));
//        printHeaderText(out, headerText);
//        out.print(EMPTY.repeat(suffixLength));
//    }
//
//    private static void printHeaderText(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_GREEN);
//
//        out.print(player);
//
//        setBlack(out);
//    }
//
//    private static void drawTicTacToeBoard(PrintStream out) {
//
//        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
//
//            drawRowOfSquares(out);
//
//            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw horizontal row separator.
//                drawHorizontalLine(out);
//                setBlack(out);
//            }
//        }
//    }
//
//    private static void drawRowOfSquares(PrintStream out) {
//
//        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
//            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                setWhite(out);
//
//                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
//                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
//                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;
//
//                    out.print(EMPTY.repeat(prefixLength));
//                    printPlayer(out, rand.nextBoolean() ? X : O);
//                    out.print(EMPTY.repeat(suffixLength));
//                }
//                else {
//                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
//                }
//
//                if (boardCol < BOARD_SIZE_IN_SQUARES - 1) {
//                    // Draw vertical column separator.
//                    setRed(out);
//                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
//                }
//
//                setBlack(out);
//            }
//
//            out.println();
//        }
//    }
//
//    private static void drawHorizontalLine(PrintStream out) {
//
//        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
//                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;
//
//        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
//            setRed(out);
//            out.print(EMPTY.repeat(boardSizeInSpaces));
//
//            setBlack(out);
//            out.println();
//        }
//    }
//
//    private static void setWhite(PrintStream out) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_WHITE);
//    }
//
//    private static void setRed(PrintStream out) {
//        out.print(SET_BG_COLOR_RED);
//        out.print(SET_TEXT_COLOR_RED);
//    }
//
//    private static void setBlack(PrintStream out) {
//        out.print(SET_BG_COLOR_BLACK);
//        out.print(SET_TEXT_COLOR_BLACK);
//    }
//
//    private static void printPlayer(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_BLACK);
//
//        out.print(player);
//
//        setWhite(out);
//    }
//}


package ui;

import chess.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class TicTacToe {

    private final ChessGame game;
    private final boolean whitePerspective;

    private static final String[] FILES = {"a","b","c","d","e","f","g","h"};

    public TicTacToe(ChessGame game, String playerColor) {
        this.game = game;
        this.whitePerspective = playerColor.equalsIgnoreCase("WHITE");
    }

    public void displayBoard() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println();
        drawFileHeaders(out);

        int rankStart = whitePerspective ? 8 : 1;
        int rankEnd   = whitePerspective ? 1 : 8;
        int rankStep  = whitePerspective ? -1 : 1;

        for (int rank = rankStart; rank != rankEnd + rankStep; rank += rankStep) {
            drawRank(out, rank);
        }

        drawFileHeaders(out);
        out.print(RESET);
    }

    // ---------------------------
    // DRAW HELPERS
    // ---------------------------

    private void drawFileHeaders(PrintStream out) {
        out.print("   ");
        int fileStart = whitePerspective ? 0 : 7;
        int fileEnd   = whitePerspective ? 7 : 0;
        int step      = whitePerspective ? 1 : -1;

        for (int f = fileStart; f != fileEnd + step; f += step) {
            out.print("  " + SET_TEXT_COLOR_WHITE + FILES[f] + " ");
        }
        out.println(RESET);
    }

    private void drawRank(PrintStream out, int rank) {
        out.print(" " + SET_TEXT_COLOR_WHITE + rank + " " + RESET);

        int fileStart = whitePerspective ? 1 : 8;
        int fileEnd   = whitePerspective ? 8 : 1;
        int step      = whitePerspective ? 1 : -1;

        for (int file = fileStart; file != fileEnd + step; file += step) {
            drawSquare(out, rank, file);
        }

        out.print(" " + SET_TEXT_COLOR_WHITE + rank + RESET);
        out.println();
    }

    private void drawSquare(PrintStream out, int rank, int file) {
        boolean lightSquare = (rank + file) % 2 == 0;

        String bg = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
        out.print(bg);

        ChessPosition pos = new ChessPosition(rank, file);
        ChessPiece piece = game.getBoard().getPiece(pos);

        if (piece == null) {
            out.print(EMPTY);
        } else {
            out.print(pieceToUnicode(piece));
        }

        out.print(RESET);
    }

    private String pieceToUnicode(ChessPiece piece) {
        return switch (piece.getTeamColor()) {
            case WHITE -> switch (piece.getPieceType()) {
                case KING   -> WHITE_KING;
                case QUEEN  -> WHITE_QUEEN;
                case ROOK   -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN   -> WHITE_PAWN;
            };
            case BLACK -> switch (piece.getPieceType()) {
                case KING   -> BLACK_KING;
                case QUEEN  -> BLACK_QUEEN;
                case ROOK   -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN   -> BLACK_PAWN;
            };
        };
    }
}
