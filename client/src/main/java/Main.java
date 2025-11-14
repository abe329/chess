import chess.*;
import ui.ChessboardRenderer;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var x = new ChessboardRenderer(new ChessGame(), "WHITE");
        x.displayBoard();
    }
}