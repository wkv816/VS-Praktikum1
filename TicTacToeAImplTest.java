/*
import org.junit.jupiter.api.Test;

//import Client.TicTacToeGame;
import Server.TicTacToeAImpl;

import static org.junit.jupiter.api.Assertions.*;


public class TicTacToeAImplTest {

    @Test
    public void testValidMove() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();

        // Create a sample game state with some moves
        String moves = "0,0,1,1,2,2";

        // Test a valid move for Player 1
        boolean isPlayer1Turn = true;
        assertTrue(ticTacToe.isMoveValid(1, 0, moves));

        // Test a valid move for Player 2
        isPlayer1Turn = false;
        assertTrue(ticTacToe.isMoveValid(2, 1, moves));
    }

    @Test
    public void testInvalidMove() {
        TicTacToeAImpl ticTacToe = new TicTacToeAImpl();

        // Create a sample game state with some moves
        String moves = "0,0,1,1,2,2";

        // Test an out-of-bounds move
        boolean isPlayer1Turn = true;
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



}*/

