package Regestry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface TicTacToeAService extends Remote {
    // Returns a map with four keys:
    // "Game ID", "Opponent Name", "First Move", "Move"
    // Each key maps to a string containing the respective value:
    // * Game ID and Opponent Name can be any reasonable strings.
    // * First Move is a string from:
    // ["your_move", "opponent_move", "no_opponent_found"]
    // * Move is an empty string ("") if this player begins. Otherwise
    // it is the move ("x,y") performed by the player that went first.
    //
    // Steps:
    // - if game is waiting for player:
    // * assign player to game
    // * randomly chose who begins
    // * write info into state
    // * if this player goes first:
    // > return the tuple (see above)
    // * if this player goes second:
    // > signal the other player
    // > wait for the other player to make a move (see `makeMove`)
    // > return the tuple (see above)
    // - else:
    // * create a new game in a "waiting-for-player" state
    // > wait for other player to join
    // > if timeout:
    // - return [0, "", "no_opponent_found", ""]
    // > if opponent joins:
    // - read data from state
    // - if this player goes first:
    // * return the tuple (see above)
    // - if this player goes second:
    // * wait for the other player to make a move (see `makeMove`)
    // * return the tuple (see above)
    public HashMap<String, String> findGame(String clientName)
            throws RemoteException;
    // Returns a String from ["game_does_not_exist", "invalid_move",
// "opponent_gone", "you_win: x,y", "you_lose: x,y", "x,y"]
//
// Grid: 0,0 is on the top left
//
// Steps:
// - if game with `gameId` exists:
// * if move is invalid:
// > "invalid_move"
// * if move ends game:
// > note win
// > signal other player
// > return "you_win: x,y" | "you_lose: x,y"
// * else:
// > signal other player
// > wait for other player to make a move
// > if timeout:
// - return "opponent_gone"
// > if opponent makes a move:
// - if move ends the game:
// * return "you_win: x,y" | "you_lose: x,y"
// - else:
// * return "x,y"
// - else:
// * return "game_does_not_exist"
    public String makeMove(int x, int y, String gameId)
            throws RemoteException;
    // Returns a list with all moves in the game with ID gameId.
//
// Each string has the pattern "name: x,y" or "winner: NAME".
// A winner must be the last element in the array and follows
// the move that won the game.
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException;


// for Test purposis
    //public String test(String str) throws RemoteException;

}
