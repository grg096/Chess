package com.chess.pgn;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class FenUtilities {

    private FenUtilities(){
        throw new RuntimeException("not instantiable");
    }

    public static Board createGameFromFen(String st){
        // r4bnr/p1p1pppp/8/8/1P6/4P3/P4PPP/2k1K2R b K - 0 17   67 starts
        st = st.replaceAll("8", "--------").
                replaceAll("7","-------").
                replaceAll("6", "------").
                replaceAll("5", "-----").
                replaceAll("4", "----").
                replaceAll("3", "---").
                replaceAll("2", "--").
                replaceAll("1", "-").
                replaceAll("/", "");

        boolean whiteKingSideCastling = st.substring(64).contains("K");
        boolean whiteQueenSideCastling = st.substring(64).contains("Q");;
        boolean blackKingSideCastling = st.substring(64).contains("k");;
        boolean blackQueenSideCastling = st.substring(64).contains("q");;

        Board.Builder builder = new Board.Builder();

        for (int i = 0; i < st.substring(0, 64).length(); i++) {

            Piece piece = createPieceFromFENChar(""+st.charAt(i), i, whiteKingSideCastling, whiteQueenSideCastling, blackKingSideCastling, blackQueenSideCastling);
            if(piece != null) builder.setPiece(piece);

        }

        if(st.substring(64).contains("b")){
            builder.setMoveMaker(Alliance.BLACK);
        } else builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    private static Piece createPieceFromFENChar(final String st, final int pos,
                                                final boolean whiteKingSideCastling, final boolean whiteQueenSideCastling,
                                                final boolean blackKingSideCastling, final boolean blackQueenSideCastling){
        Piece piece = null;
        if(st.equals("p")) piece = new Pawn(Alliance.BLACK, pos, pos <=15);
        if(st.equals("r")) piece = new Rook(Alliance.BLACK, pos, pos == 0 || pos == 7);
        if(st.equals("k")) piece = new King(pos, Alliance.BLACK, blackKingSideCastling, blackQueenSideCastling);
        if(st.equals("q")) piece = new Queen(Alliance.BLACK, pos, pos == 3);
        if(st.equals("n")) piece = new Knight(pos, Alliance.BLACK);
        if(st.equals("b")) piece = new Bishop(pos, Alliance.BLACK);

        if(st.equals("P")) piece = new Pawn(Alliance.WHITE, pos, 48 <= pos && pos <= 55);
        if(st.equals("R")) piece = new Rook(Alliance.WHITE, pos, pos == 56 || pos == 63);
        if(st.equals("K")) piece = new King(pos, Alliance.WHITE, whiteKingSideCastling, whiteQueenSideCastling);
        if(st.equals("Q")) piece = new Queen(pos, Alliance.WHITE);
        if(st.equals("N")) piece = new Knight(pos, Alliance.WHITE);
        if(st.equals("B")) piece = new Bishop(pos, Alliance.WHITE);

        return piece;
    }

    public static String createFenFromGame(final Board board){
        return calculateBoardText(board) + " " +
                calculateCurrentPlayerText(board) + " " +
                calculateCastleText(board) + " " +
                calculateEnPassantSquare(board) + " " +
                "0 1";
    }

    private static String calculateBoardText(final Board board) {

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            final String tileText = board.getTile(i).toString();
            builder.append(tileText);
            if((i+1)%8 == 0 && i != 63){
                builder.append("/");
            }
        }
        return builder.toString().
                replaceAll("--------", "8").
                replaceAll("-------", "7").
                replaceAll("------", "6").
                replaceAll("-----", "5").
                replaceAll("----", "4").
                replaceAll("---", "3").
                replaceAll("--", "2").
                replaceAll("-", "1");
    }

    private static String  calculateEnPassantSquare(final Board board) {

        final Pawn enPassantPawn = board.getEnPassantPawn();

        if(enPassantPawn != null){
            return BoardUtils.getChessPositionAtAlgebraicCoordinate(enPassantPawn.getPiecePosition() + 8* enPassantPawn.getPieceAlliance().getOppositeDirection());
        }

        return "-";

    }

    private static String  calculateCastleText(final Board board) {

        final StringBuilder builder = new StringBuilder();

        if(board.whitePlayer().isKingSideCastleCapable()){
            builder.append("K");
        }
        if(board.whitePlayer().isQueenSideCastleCapable()){
            builder.append("Q");
        }
        if(board.blackPlayer().isKingSideCastleCapable()){
            builder.append("k");
        }
        if(board.blackPlayer().isQueenSideCastleCapable()){
            builder.append("q");
        }

        final String result = builder.toString();

        return result.isEmpty() ? "-" : result;

    }

    private static String calculateCurrentPlayerText(final Board board) {

        if(board.currentPlayer().getAlliance().isWhite()){
            return "w";
        }

        return "b";

    }

}
