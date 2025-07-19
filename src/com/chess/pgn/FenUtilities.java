package com.chess.pgn;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.pieces.Pawn;

public class FenUtilities {

    private FenUtilities(){
        throw new RuntimeException("not instantiable");
    }

    public static Board createGameFormFen(final String st){
        return null;
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
