package com.chess.gui;

import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.gui.Table.Movelog;
import com.google.common.primitives.Ints;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public class TakenPiecesPanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;

    private static String takenPiecesPanelColor = "0xFDF5E6";

    private static final Dimension TAKEN_PIECES_DIMENSION = new Dimension(70, 80);
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

    public TakenPiecesPanel(){
        super(new BorderLayout());
        setBackground(Color.decode(takenPiecesPanelColor));
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 2));
        this.southPanel = new JPanel(new GridLayout(8, 2));

        this.northPanel.setBackground(Color.decode(takenPiecesPanelColor));
        this.southPanel.setBackground(Color.decode(takenPiecesPanelColor));

        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);

        this.setPreferredSize(TAKEN_PIECES_DIMENSION);
    }

    public void redo(final Movelog movelog){

        this.southPanel.removeAll();
        this.northPanel.removeAll();

        final java.util.List<Piece> whiteTakenPieces = new ArrayList<>();
        final java.util.List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : movelog.getMoves()) {
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                } else if (takenPiece.getPieceAlliance().isBlack()) {
                    blackTakenPieces.add(takenPiece);
                }else {
                    throw new RuntimeException("impossible error bro wtf");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for(final Piece takenPiece : whiteTakenPieces){
            try {
                final BufferedImage image = ImageIO.read(new File(
                        GuiUtils.defaultPieceImagesPath + takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                imageLabel.setIcon(icon);
                this.southPanel.add(imageLabel);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(final Piece takenPiece : blackTakenPieces){
            try {
                final BufferedImage image = ImageIO.read(new File(
                        GuiUtils.defaultPieceImagesPath + takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                imageLabel.setIcon(icon);
                this.southPanel.add(imageLabel);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        validate();

    }

}
