package com.chess.engine.pieces;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public abstract class Piece {

    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private final int cacheHashCode;

    Piece(final PieceType pieceType, final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        //TODO more work
        this.isFirstMove = isFirstMove;
        this.cacheHashCode = computeHashCode();
    }

    private int computeHashCode(){
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return this.piecePosition == otherPiece.getPiecePosition() && this.pieceType == otherPiece.getPieceType() &&
                this.pieceAlliance == otherPiece.getPieceAlliance() && this.isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode(){
        return this.cacheHashCode;
    }

    public int getPiecePosition(){
        return this.piecePosition;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getPieceValue(){
        return this.pieceType.getPieceValue();
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public enum PieceType {

        PAWN("P", 100) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public ImageIcon getImage(String texturePack, Alliance pieceAlliance) {
                String url = "src/com/chess/gui/art/" + texturePack + "/" + pieceAlliance + this.getPieceName() + ".gif";
                try {
                    return new ImageIcon(ImageIO.read(new File(url)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        KNIGHT("N", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public ImageIcon getImage(String texturePack, Alliance pieceAlliance) {
                String url = "src/com/chess/gui/art/" + texturePack + "/" + pieceAlliance + this.getPieceName() + ".gif";
                try {
                    return new ImageIcon(ImageIO.read(new File(url)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        BISHOP("B", 300) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public ImageIcon getImage(String texturePack, Alliance pieceAlliance) {
                String url = "src/com/chess/gui/art/" + texturePack + "/" + pieceAlliance + this.getPieceName() + ".gif";
                try {
                    return new ImageIcon(ImageIO.read(new File(url)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        ROOK("R", 500) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public ImageIcon getImage(String texturePack, Alliance pieceAlliance) {
                String url = "src/com/chess/gui/art/" + texturePack + "/" + pieceAlliance + this.getPieceName() + ".gif";
                try {
                    return new ImageIcon(ImageIO.read(new File(url)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        QUEEN("Q", 900) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public ImageIcon getImage(String texturePack, Alliance pieceAlliance) {
                String url = "src/com/chess/gui/art/" + texturePack + "/" + pieceAlliance + this.getPieceName() + ".gif";
                try {
                    return new ImageIcon(ImageIO.read(new File(url)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },
        KING("K", 1000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public ImageIcon getImage(String texturePack, Alliance pieceAlliance) {
                String url = "src/com/chess/gui/art/" + texturePack + "/" + pieceAlliance + this.getPieceName() + ".gif";
                try {
                    return new ImageIcon(ImageIO.read(new File(url)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };


        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue) {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public int getPieceValue(){
            return this.pieceValue;
        }
        public String getPieceName(){
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();
        public abstract ImageIcon getImage(final String texturePack, final Alliance pieceAlliance);
    }
}
