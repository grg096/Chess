package com.chess.engine.player.ai;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.player.MoveTransition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFoolsMate {

    @Test
    public void testFoolsMate(){

        Board foolsMateBoard = createFoolsMateBoard();

        final MoveStrategy strategy = new MiniMax(4);

        final Move aiMove = strategy.execute(foolsMateBoard);

        final Move bestMove = Move.MoveFactory.createMove(foolsMateBoard, BoardUtils.getAlgebraicCoordinateAtChessPosition("d8"), BoardUtils.getAlgebraicCoordinateAtChessPosition("h4"));

        assertEquals(aiMove, bestMove);
    }

    private static final Board createFoolsMateBoard(){
        final Board board = Board.createStandardBoard();
        final MoveTransition t1 = board.currentPlayer().makeMove(
                Move.MoveFactory.createMove(board, BoardUtils.getAlgebraicCoordinateAtChessPosition("f2"),
                        BoardUtils.getAlgebraicCoordinateAtChessPosition("f3")));

        assertTrue(t1.getMoveStatus().isDone());

        final MoveTransition t2 = t1.getTransitionBoard().currentPlayer().makeMove(
                Move.MoveFactory.createMove(t1.getTransitionBoard(), BoardUtils.getAlgebraicCoordinateAtChessPosition("e7"),
                        BoardUtils.getAlgebraicCoordinateAtChessPosition("e5")));

        assertTrue(t2.getMoveStatus().isDone());

        final MoveTransition t3 = t2.getTransitionBoard().currentPlayer().makeMove(
                Move.MoveFactory.createMove(t2.getTransitionBoard(), BoardUtils.getAlgebraicCoordinateAtChessPosition("g2"),
                        BoardUtils.getAlgebraicCoordinateAtChessPosition("g4")));

        assertTrue(t3.getMoveStatus().isDone());

        return t3.getTransitionBoard();
    }

}
