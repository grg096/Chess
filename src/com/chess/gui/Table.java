package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.chess.engine.player.ai.MiniMax;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.*;

public class Table {

    private final MiniMax miniMax;
    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final TakenPiecesColorPanel.WhiteTakenPiecesColorPanel whiteTakenPieces;
    private final TakenPiecesColorPanel.BlackTakenPiecesColorPanel blackTakenPieces;
    private final BoardPanel boardPanel;
    private final Movelog movelog;

    private Board chessBoard;
    private BoardDirection boardDirection;
    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension OPTIONS_DIALOG_DIMENSION = new Dimension(300, 500);
    private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private final static Dimension END_GAME_DIALOG_WINDOW_DIMENSION = new Dimension(200, 100);

    private boolean highlightLegalMoves = true;
    private String positionOfWhiteTakenPanel = BorderLayout.SOUTH;
    private String positionOfBlackTakenPanel = BorderLayout.NORTH;

    private static String HIGHLITE_ICON_PATH = "src/com/chess/gui/art/misc/green_dot.png";
    //    private static String texturePack = GuiUtils.texturePack;
//    private static String defaultPieceImagesPath = GuiUtils.defaultPieceImagesPath;
    private static String texturePack = "fancy";
    private static String defaultPieceImagesPath = "src/com/chess/gui/art/" + texturePack + "/";

    private final static Color lightTileColor = Color.white;
    private final static Color darkTileColor = Color.darkGray;


    public Table() {

        this.miniMax = new MiniMax(4);
        this.gameFrame = new JFrame("JChess");
        this.gameFrame.setLayout(new BorderLayout());

        this.chessBoard = Board.createStandardBoard();

        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();

        this.boardDirection = BoardDirection.NORMAL;

        final JMenuBar tableMenuBar = createTableMenuBar();

        whiteTakenPieces = new TakenPiecesColorPanel.WhiteTakenPiecesColorPanel();
        blackTakenPieces = new TakenPiecesColorPanel.BlackTakenPiecesColorPanel();

        this.boardPanel = new BoardPanel();

        this.movelog = new Movelog();

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.add(this.whiteTakenPieces, positionOfWhiteTakenPanel);
        this.gameFrame.add(this.blackTakenPieces, positionOfBlackTakenPanel);

        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.gameFrame.setVisible(true);

    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open up that pgn");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        abstract BoardDirection opposite();

    }


    private JMenu createPreferencesMenu() { // later will make it create different page with all settings probably

        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = positionOfWhiteTakenPanel;

                boardDirection = boardDirection.opposite();
                positionOfWhiteTakenPanel = positionOfBlackTakenPanel;
                positionOfBlackTakenPanel = temp;
                gameFrame.remove(whiteTakenPieces);
                gameFrame.remove(blackTakenPieces);
                gameFrame.add(whiteTakenPieces, positionOfWhiteTakenPanel);
                gameFrame.add(blackTakenPieces, positionOfBlackTakenPanel);
                gameFrame.validate();
                boardPanel.drawBoard(chessBoard);
            }
        });

        preferencesMenu.add(flipBoardMenuItem);

        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckBox = new JCheckBoxMenuItem("Highlight legal moves", false);
        legalMoveHighlighterCheckBox.setState(true);

        legalMoveHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = legalMoveHighlighterCheckBox.isSelected();
            }
        });

        preferencesMenu.add(legalMoveHighlighterCheckBox);

        return preferencesMenu;
    }

    private JMenuItem createOptionsMenu() {
        //TODO make it so that it cant be highlighted

        final JMenuItem optionsMenuItem = new JMenuItem("options");

        optionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OptionsDialogWindow(gameFrame);
            }
        });


        return optionsMenuItem;

    }

    private class OptionsDialogWindow extends JDialog {
        // texturePack, players, map regime,

        final private JFrame frame;
        final private JPanel texturePanel;
        final private JPanel playerPanel;
        final private JPanel uiRegim;
        final private JButton saveButton;

        private boolean blackAI;
        private boolean whiteAI;

        private String tempTexturePack = texturePack;

        public OptionsDialogWindow(JFrame frame) {

            this.frame = frame;

            final Container dialogWindowContainer = new Container();
            dialogWindowContainer.setLayout(new BoxLayout(dialogWindowContainer, BoxLayout.Y_AXIS));

            texturePanel = createTexturePackPanel();
            playerPanel = createPlayerPanel();
            uiRegim = null; // later change
            saveButton = createSaveButton();

            this.setModal(true);
            this.setLayout(new FlowLayout());
            this.setTitle("Options");
            this.setSize(OPTIONS_DIALOG_DIMENSION);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            texturePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            playerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            dialogWindowContainer.add(texturePanel);
            dialogWindowContainer.add(playerPanel);

            this.add(dialogWindowContainer);
            this.add(saveButton);

            this.setVisible(true);
        }

        private JButton createSaveButton() {

            final JButton saveButton = new JButton("save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    texturePack = tempTexturePack;
                    defaultPieceImagesPath = "src/com/chess/gui/art/" + texturePack + "/";
                    boardPanel.drawBoard(chessBoard);

                    chessBoard.blackPlayer().setAI(blackAI);
                    chessBoard.whitePlayer().setAI(whiteAI);

                    dispose();
                }
            });


            return saveButton;
        }

        private JPanel createTexturePackPanel() {
            JPanel panel = new JPanel();
            JLabel textureLabel = new JLabel("texture");
            String[] textures = {"fancy", "fancy2", "holywarriors", "simple"};
            JComboBox<String> comboBox = new JComboBox<>(textures);

            comboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tempTexturePack = (String) comboBox.getSelectedItem();
                }
            });

            panel.add(textureLabel);
            panel.add(comboBox);

            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

            return panel;
        }

        private JPanel createPlayerPanel() {

            final JPanel playerPanel = new JPanel();
            final JPanel whitePlayerPanel = new JPanel();
            final JPanel whiteRadioPanel = new JPanel();
            final JPanel blackPlayerPanel = new JPanel();
            final JPanel blackRadioPanel = new JPanel();

            final JLabel whitePlayerText = new JLabel("white player");
            whitePlayerText.setLayout(new BoxLayout(whitePlayerText, BoxLayout.X_AXIS));
            final JRadioButton whitePlayerRadioButton = new JRadioButton("Player");
            final JRadioButton whiteAIRadioButton = new JRadioButton("AI");

            whitePlayerRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    whiteAI = false;
                }
            });

            whiteAIRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    whiteAI = true;
                }
            });

            whiteRadioPanel.add(whitePlayerRadioButton);
            whiteRadioPanel.add(whiteAIRadioButton);

            final JLabel blackPlayerText = new JLabel("black player");
            final JRadioButton blackPlayerRadioButton = new JRadioButton("Player");
            final JRadioButton blackAIRadioButton = new JRadioButton("AI");

            blackPlayerRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    blackAI = false;
                }
            });

            blackAIRadioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    blackAI = true;
                }
            });

            blackRadioPanel.add(blackPlayerRadioButton);
            blackRadioPanel.add(blackAIRadioButton);

            //groups all radio buttons
            ButtonGroup whiteRadioButtonGroup = new ButtonGroup();
            whiteRadioButtonGroup.add(whitePlayerRadioButton);
            whiteRadioButtonGroup.add(whiteAIRadioButton);
            ButtonGroup blackRadioButtonGroup = new ButtonGroup();
            blackRadioButtonGroup.add(blackPlayerRadioButton);
            blackRadioButtonGroup.add(blackAIRadioButton);

            //add text and radioButtons into one panel
            whitePlayerPanel.add(whitePlayerText);
            whitePlayerPanel.add(whiteRadioPanel);
            blackPlayerPanel.add(blackPlayerText);
            blackPlayerPanel.add(blackRadioPanel);

            playerPanel.add(whitePlayerPanel);
            playerPanel.add(blackPlayerPanel);

            playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

            return playerPanel;
        }

    }

    private class CheckMateDialogWindow extends JDialog {

        public CheckMateDialogWindow(){
            this.setTitle("Checkmate");
            this.setSize(END_GAME_DIALOG_WINDOW_DIMENSION);
            this.setModal(true);
            this.setLayout(new FlowLayout());

            JLabel text = new JLabel("The game is over. " + chessBoard.getPlayer().getOpponent() + " won :)");
            JButton restartButton = new JButton("new game");
            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessBoard = Board.createStandardBoard();
                    movelog.clear();
                    gameHistoryPanel.redo(chessBoard, movelog);
                    takenPiecesPanel.redo(movelog);
                    blackTakenPieces.redo(movelog, chessBoard);
                    whiteTakenPieces.redo(movelog, chessBoard);

                    boardPanel.drawBoard(chessBoard);

                    dispose();
                }
            });

            this.add(text);
            this.add(restartButton);
            this.setVisible(true);

        }

    }

    private class StaleMateDialogWindow extends JDialog {

        public StaleMateDialogWindow(){
            this.setTitle("Stalemate");
            this.setSize(END_GAME_DIALOG_WINDOW_DIMENSION);
            this.setModal(true);
            this.setLayout(new FlowLayout());

            JLabel text = new JLabel("The game is over - it's a tie (:");
            JButton restartButton = new JButton("new game");
            restartButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessBoard = Board.createStandardBoard();
                    movelog.clear();
                    gameHistoryPanel.redo(chessBoard, movelog);
                    takenPiecesPanel.redo(movelog);
                    blackTakenPieces.redo(movelog, chessBoard);
                    whiteTakenPieces.redo(movelog, chessBoard);

                    boardPanel.drawBoard(chessBoard);

                    dispose();
                }
            });

            this.add(text);
            this.add(restartButton);
            this.setVisible(true);

        }

    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }

            validate();
            repaint();

        }

    }

    public static class Movelog {

        private final List<Move> moves;

        Movelog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(final int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }

    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel,
                  final int tileId) {

            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {

//                    if(isBlackAI && chessBoard.currentPlayer().getAlliance() == Alliance.BLACK){
//                        System.out.println("WORKS");
//                        final Move move = miniMax.execute(chessBoard);
//                        final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
//                        if(transition.getMoveStatus().isDone()) {
//                            chessBoard = transition.getTransitionBoard();
//                            System.out.println(chessBoard);
//                            movelog.addMove(move);
//                        }
//                    }

                    // dont really know how to do it well, probably will need to use multithreading
                    // the problem is that I either need to check if it's an AI move first in the order and then work with user move
                    // which lead to the problem that AI move executes only after additional clicking(let say that black is AI,
                    // then program checks if current player(white) is AI, then lets user make a move and that's it, the mouseListener ends. Then you need to click again and that will trigger AI check,
                    // which will be true in this case, because player now is black, and program will execute the move)
                    // Second case is that you put AI check after the user's move code, but in this case the program itself will draw the board only after all moves, white and black, are done
                    // which is obviously bad and I dont really understand why it happens. I suppose that program can work only on one task at a time, so when I make an AI move it completely
                    //switches to calculating all AI possibilities and cant even validate and repaint the board(although code for that is before the AI, so I dont understand why it happens, almost like it ignores that code)
                    if (chessBoard.isAI()) {
                        executeAiMove();
                    }

                    if (isRightMouseButton(e)) {

                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;

                    } else if (isLeftMouseButton(e)) {

                        // first click
                        if (sourceTile == null) {

                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        }
                        // second click
                        else {

                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                System.out.println(chessBoard);
                                movelog.addMove(move);
                                //TODO add made move to move list
                            }

                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;

                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, movelog);
                                takenPiecesPanel.redo(movelog);

                                whiteTakenPieces.redo(movelog, chessBoard);
                                blackTakenPieces.redo(movelog, chessBoard);

                                boardPanel.drawBoard(chessBoard);
                                validate();

                                if(chessBoard.getPlayer().isInCheckMate()){ //later change on check for checkmate
                                    new CheckMateDialogWindow();
                                } else if (chessBoard.getPlayer().isInStaleMate()) {
                                    new StaleMateDialogWindow();
                                }

                            }

                        });

                        validate();


                    }
                }


                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            validate();
        }

        public void executeAiMove() {
            final Move move = miniMax.execute(chessBoard);
            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                chessBoard = transition.getTransitionBoard();
                System.out.println(chessBoard);
                movelog.addMove(move);
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    gameHistoryPanel.redo(chessBoard, movelog);
                    takenPiecesPanel.redo(movelog);

                    whiteTakenPieces.redo(movelog, chessBoard);
                    blackTakenPieces.redo(movelog, chessBoard);

                    boardPanel.drawBoard(chessBoard);
                }
            });
            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {

                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1)
                            + board.getTile(this.tileId).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        private void highlightLegalMoves(final Board board) {
            if (highlightLegalMoves) {
                for (final Move move : pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File(HIGHLITE_ICON_PATH)))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTileColor() {

            if (BoardUtils.EIGHTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                if (tileId % 2 == 0) {
                    setBackground(lightTileColor);
                } else {
                    setBackground(darkTileColor);
                }
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                if (tileId % 2 == 0) {
                    setBackground(darkTileColor);
                } else {
                    setBackground(lightTileColor);
                }
            }

        }

    }

}
