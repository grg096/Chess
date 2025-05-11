package com.chess.engine.board;

import com.google.common.collect.ImmutableMap;

import java.util.*;

public class BoardUtils {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] THIRD_COLUMN = initColumn(2);
    public static final boolean[] FOURTH_COLUMN = initColumn(3);
    public static final boolean[] FIFTH_COLUMN = initColumn(4);
    public static final boolean[] SIXTH_COLUMN = initColumn(5);
    public static final boolean[] SEVENTH_COLUMN =initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] EIGHTH_RANK = initRow(0); // eighth rank is first black rank
    public static final boolean[] SEVENTH_RANK = initRow(1);
    public static final boolean[] SIXTH_RANK = initRow(2);
    public static final boolean[] FIFTH_RANK = initRow(3);
    public static final boolean[] FOURTH_RANK = initRow(4);
    public static final boolean[] THIRD_RANK = initRow(5);
    public static final boolean[] SECOND_RANK = initRow(6);
    public static final boolean[] FIRST_RANK = initRow(7);

    public static final String[] ALGEBRAIC_NOTATION = initializeAlgebraicNotation();

    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private BoardUtils(){
        throw new RuntimeException("You cannot initialize this class");
    }

    private static String[] initializeAlgebraicNotation() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
    }

    private static Map<String, Integer> initializePositionToCoordinateMap() {

        final Map<String, Integer> positionToCoordinate = new HashMap<>();

        for(int i = 0; i < NUM_TILES; i++){
            positionToCoordinate.put(ALGEBRAIC_NOTATION[i], i);
        }

        return ImmutableMap.copyOf(positionToCoordinate);
    }

    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[64];

        do {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        } while (columnNumber < NUM_TILES);


        return column;
    }

    private static boolean[] initRow(int rowNumber){
        int rowCoordinate = rowNumber * 8;
        final boolean[] row = new boolean[NUM_TILES];

        do{
            row[rowCoordinate] = true;
            rowCoordinate++;
        } while (rowCoordinate % NUM_TILES_PER_ROW != 0);

        return row;
    }

    public static boolean isValidTileCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < 64;
    }

    public static int getAlgebraicCoordinateAtChessPosition(final String position){
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getChessPositionAtAlgebraicCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION[coordinate];
    }


}
