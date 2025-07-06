package com.chess.gui;

import com.chess.JChess;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 400);

    GameHistoryPanel(){
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        final JPanel swapPanel = createMoveSwapPanel();
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(swapPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private JPanel createMoveSwapPanel(){

        final JPanel panel = new JPanel();
        final JButton moveBackButton = new JButton();
        final JButton moveForwardButton = new JButton();

        moveBackButton.setText("<");
        moveBackButton.setFocusable(false);
        moveForwardButton.setText(">");
        moveForwardButton.setFocusable(false);

        moveBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Table table = JChess.getTable();
                if(!table.getMovelog().getBoards().isEmpty()){
                    table.getMovelog().decreaseIndex();
                    table.setBoardAfterMovelogChange();
                    if(table.getMovelog().getCurrentBoardIndex() != -1){
                        redo( table.getMovelog().getMovelogByIndex());
                    } else{
                        redo(new Table.Movelog());
                    }

                }
            }
        });

        moveForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Table table = JChess.getTable();
                table.getMovelog().increaseIndex();
                table.setBoardAfterMovelogChange();
                if(table.getMovelog().getCurrentBoardIndex() != -1){
                    redo(table.getMovelog().getMovelogByIndex());
                } else{
                    redo(table.getMovelog().getMovelogByIndex());
                }
            }
        });

        panel.add(moveBackButton);
        panel.add(moveForwardButton);

        return panel;
    }

    void redo(final Table.Movelog moveHistory){
        int currentRow = 0;
        this.model.clear();
        for(final Move move : moveHistory.getMoves()){
            final String moveText = move.toString();
            if(move.getMovedPiece().getPieceAlliance().isWhite()){
                this.model.setValueAt(moveText +
                        calculateCheckAndCheckMateHash(moveHistory.getBoards().get(moveHistory.getMoves().indexOf(move))), currentRow, 0);
            } else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
                this.model.setValueAt(moveText +
                        calculateCheckAndCheckMateHash(moveHistory.getBoards().get(moveHistory.getMoves().indexOf(move))), currentRow, 1);
                currentRow++;
            }
        }

        final JScrollBar vertial = scrollPane.getVerticalScrollBar();
        vertial.setValue(vertial.getMaximum());

    }

    private String calculateCheckAndCheckMateHash(Board board) {
        if(board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if(board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {

        private final java.util.List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.values = new ArrayList<>();
        }

        public void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount(){
            if(this.values == null){
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column){
            final Row currentRow = this.values.get(row);
            if(column == 0) {
                return currentRow.getWhiteMove();
            } else if(column == 1) {
                return currentRow.getBlackMove();
            }

            return null;
        }

        @Override
        public void setValueAt(final Object aValue, final int row, final int column){
            final Row currentRow;
            if(this.values.size() <= row){
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if(column == 0) {
                currentRow.setWhiteMove((String) aValue);
                fireTableRowsInserted(row, row);
            } else if (column == 1){
                currentRow.setBlackMove( (String) aValue);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column) {
            return NAMES[column];
        }

    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row(){

        }

        public String getWhiteMove(){return this.whiteMove;}

        public String getBlackMove(){return this.blackMove;}

        public void setWhiteMove(final String move){
            this.whiteMove = move;
        }

        public void setBlackMove(final String move){
            this.blackMove = move;
        }

    }

}
