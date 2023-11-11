
import org.junit.jupiter.api.Test;

import Client.ClientPlayer;
import Server.TicTacToeAImpl;

import static org.junit.jupiter.api.Assertions.*;


public class TicTacToeAImplTest {
    /*
     * 
    @Test
    public void  testGameOverHorizontal() {
        assertTrue(TicTacToeAImpl.isGameOvertest("0,0|1,1|0,1|1,2|0,2")); // First player wins horizontally in the first row.
        assertTrue(TicTacToeAImpl.isGameOvertest("1,0|0,0|1,1|0,1|2,2|0,2")); // Second player wins horizontally in the first row.
        assertFalse(TicTacToeAImpl.isGameOvertest("0,0|1,1|0,1|1,2")); // No player wins horizontally.
    }

    @Test
    public void testGameOverVertical() {
        assertTrue(TicTacToeAImpl.isGameOvertest("0,0|1,1|1,0|1,2|2,0")); // First player wins vertically in the first column.
        assertTrue(TicTacToeAImpl.isGameOvertest("1,0|0,0|1,1|0,1|2,1|0,2")); // Second player wins vertically in the first column.
        assertFalse(TicTacToeAImpl.isGameOvertest("0,0|1,1|0,1|1,2")); // No player wins vertically.
    }

    @Test
    public void testGameOverDiagonal() {
        assertTrue(TicTacToeAImpl.isGameOvertest("0,0|1,0|1,1|1,2|2,2")); // First player wins diagonally.
        assertTrue(TicTacToeAImpl.isGameOvertest("1,0|0,0|1,1|1,1|2,1|2,2")); // Second player wins diagonally.
        assertFalse(TicTacToeAImpl.isGameOvertest("0,0|1,1|0,1|1,2")); // No player wins diagonally.
    }

    @Test
    public void testGameOverMixed() {
        assertTrue(TicTacToeAImpl.isGameOvertest("0,0|1,1|1,0|1,2|0,1|2,1|0,2")); // First player wins with a mix of moves.
        assertTrue(TicTacToeAImpl.isGameOvertest("2,0|0,0|2,1|0,2|1,1|1,2|1,0|2,2")); // Second player wins with a mix of moves.
        assertFalse(TicTacToeAImpl.isGameOvertest("0,0|1,1|0,1|1,2|2,0|2,2")); // No player wins with a mix of moves.
    }

    @Test
    public void testGameNotOver() {
        assertFalse(TicTacToeAImpl.isGameOvertest("0,0|1,1|0,1|1,2|2,0|2,2")); // No player wins, but the game is not over.
        assertFalse(TicTacToeAImpl.isGameOvertest("")); // Empty moves, the game is not over.
    }
/*
 *
    @Test
    public void testInvalidMove() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();

        // Create a sample game state with some moves
        String moves = "0,0,1,1,2,2";

        // Test an out-of-bounds move
        assertFalse(ticTacToe.isMoveValid(-1, 1, moves));

        // Test a move to an already occupied cell
        assertFalse(ticTacToe.isMoveValid(0, 0, moves));

        // Test an invalid move for the wrong player's turn
        assertFalse(ticTacToe.isMoveValid(1, 0, moves)); // Player 1's turn, but Player 2 is making a move
    }

     @Test
    public void testNoWinnerNoDraw() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();
        assertFalse(ticTacToe.isGameOver(0, 0, ""));
        assertFalse(ticTacToe.isGameOver(1, 1, "XOXOXOOXO"));
    }

    @Test
    public void testHorizontalWin() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();
        assertTrue(ticTacToe.isGameOver(0, 0, "XXXOOO"));
    }

    @Test
    public void testVerticalWin() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();
        assertTrue(ticTacToe.isGameOver(2, 0, "XOXOXOXOX"));
    }

    @Test
    public void testDiagonalWin() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();
        assertTrue(ticTacToe.isGameOver(1, 1, "XOXOXOOXO"));
    }

    @Test
    public void testDraw() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();
        assertTrue(ticTacToe.isGameOver(0, 0, "XOXOXOOX"));
    }
 */


}
 

