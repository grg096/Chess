package com.chess.gui;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.Piece;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class TakenPiecesColorPanel extends JPanel {

    private final JPanel playerTitlePanel;
    private final JPanel takenPiecesPanel;
    private final JLabel advantage;
    private final Color titleColor;
    private final Alliance panelPieceAlliance;

    private static final Dimension PLAYER_TITLE_PANEL_DIMENSION = new Dimension(60, 60);

    TakenPiecesColorPanel(final Color color, Alliance alliance){
        this.titleColor = color;
        this.panelPieceAlliance = alliance;

        this.setLayout(new BorderLayout());
        playerTitlePanel = createPlayerTitlePanel();
        takenPiecesPanel = createTakenPiecesPanel();
        advantage = new JLabel();

        final JPanel panelTakenPiecesAndAdvantage = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTakenPiecesAndAdvantage.add(takenPiecesPanel);
        panelTakenPiecesAndAdvantage.add(advantage);

        this.add(playerTitlePanel, BorderLayout.WEST);
        this.add(panelTakenPiecesAndAdvantage);

    }

    private JPanel createTakenPiecesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(true);

        return panel;
    }

    public void redo(final Table.Movelog movelog, Board board){

        takenPiecesPanel.removeAll();

        int allyValue = 0;
        int opponentValue = 0;
        int allyAdvantage = 0;

        final java.util.List<Piece> takenAllyPieces = new ArrayList<>();
        final java.util.List<Piece> takenOpponentPieces = new ArrayList<>();

        for (final Move move : movelog.getMoves()){
            if(move.isAttack() && move.getAttackedPiece().getPieceAlliance() == panelPieceAlliance){
                takenAllyPieces.add(move.getAttackedPiece());
            } else if (move.isAttack() && move.getAttackedPiece().getPieceAlliance() == panelPieceAlliance.getOpponentAlliance()) {
                takenOpponentPieces.add(move.getAttackedPiece());
            }
        }

        Collections.sort(takenOpponentPieces, new Comparator<Piece>() {
            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceValue(), o2.getPieceValue());
            }
        });

        for(final Piece takenPiece : takenOpponentPieces){
            try {
                final BufferedImage image = ImageIO.read(new File(
                        GuiUtils.defaultPieceImagesPath + takenPiece.getPieceAlliance().toString().substring(0, 1) + "" + takenPiece.toString() + ".gif"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel();
                imageLabel.setIcon(icon);
                this.takenPiecesPanel.add(imageLabel);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(Piece piece : board.getAllPieces()){
            if(piece.getPieceAlliance() == panelPieceAlliance){
                allyValue += piece.getPieceValue() / 100;
            } else if (piece.getPieceAlliance() == panelPieceAlliance.getOpponentAlliance()){
                opponentValue += piece.getPieceValue() / 100;
            }
        }

        allyAdvantage = allyValue - opponentValue;

        if(allyAdvantage > 0){
            advantage.setText(String.valueOf(allyAdvantage));
        } else {
            advantage.setText("");
        }

        validate();
    }

    private JPanel createPlayerTitlePanel(){
        final JPanel playerPanel = new JPanel(new FlowLayout());
        final JLabel block = new JLabel();

        block.setBackground(titleColor);
        block.setPreferredSize(new Dimension((int) PLAYER_TITLE_PANEL_DIMENSION.getWidth() - 10, (int)PLAYER_TITLE_PANEL_DIMENSION.getHeight() - 10));
        block.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10) );
        block.setOpaque(true);

        playerPanel.setBorder(new LineBorder(Color.black));
        playerPanel.setPreferredSize(PLAYER_TITLE_PANEL_DIMENSION);
        playerPanel.add(block);

        return playerPanel;
    };

    public static class WhiteTakenPiecesColorPanel extends TakenPiecesColorPanel {


        WhiteTakenPiecesColorPanel(){
            super(Color.white, Alliance.WHITE);
        }

        protected JPanel createTakenPiecesPanel() {
            return new JPanel();
        }

    }

    public static class BlackTakenPiecesColorPanel extends TakenPiecesColorPanel {

        BlackTakenPiecesColorPanel(){
            super(Color.black, Alliance.BLACK);
        }

        protected JPanel createTakenPiecesPanel() {
            return new JPanel();
        }


    }


}
