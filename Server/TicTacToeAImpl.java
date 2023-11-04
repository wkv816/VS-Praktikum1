package Server;


import Regestry.TicTacToeAService;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TicTacToeAImpl implements TicTacToeAService {

    private HashMap<String, String> gameStates = new HashMap<>();
    private HashMap<String, Boolean> currentPlayerTurn = new HashMap<>();
    private Random random = new Random();


    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException {
        // Todo
        for (String gameId : gameStates.keySet()) {
            if (gameStates.get(gameId).equals("waiting-for-player")) {
                // Assign player to the game
                gameStates.put(gameId, "playing");
                // Randomly choose who begins
                boolean thisPlayerFirst = random.nextBoolean();
                if (thisPlayerFirst) {
                    String gameMoves = clientName + ": no_opponent_found";
                    return createGameState(gameId, clientName, "your_move", gameMoves);
                } else {
                    String gameMoves = clientName + ": no_opponent_found";
                    return createGameState(gameId, clientName, "opponent_move", gameMoves);
                }
            }
        }

        // No game found, create a new one in "waiting-for-player" state
        String gameId = generateGameId();
        gameStates.put(gameId, "waiting-for-player");
        String gameMoves = clientName + ": no_opponent_found";
        return createGameState(gameId, clientName, "no_opponent_found", gameMoves);

    }

    @Override
    public String makeMove(int x, int y, String gameId) throws RemoteException {
        if (!gameStates.containsKey(gameId)) {
            return "game_does_not_exist";
        }

        String moves = gameStates.get(gameId);
        boolean isPlayer1Turn = currentPlayerTurn.getOrDefault(gameId, true);

        if (isMoveValid(x, y, moves, isPlayer1Turn)) {
            moves = moves + "," + x + "," + y;

            if (isGameOver(x, y, moves)) {
                return "you_win: " + x + "," + y;
            } else {
                currentPlayerTurn.put(gameId, !isPlayer1Turn);
                signalOtherPlayer(gameId);
                return x + "," + y;
            }
        } else {
            return "invalid_move";
        }
    }

    @Override
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException {
        // Todo
        System.out.println("invoked fullUpdate()");
        return null;
    }



    private String generateGameId() {
        return "Game" + random.nextInt(1000);
    }

    private HashMap<String, String> createGameState(String gameId, String clientName, String firstMove, String gameMoves) {
        HashMap<String, String> gameState = new HashMap<>();
        gameState.put("Game ID", gameId);
        gameState.put("Opponent Name", clientName);
        gameState.put("First Move", firstMove);
        gameState.put("Move", gameMoves);
        return gameState;
    }

    public boolean isMoveValid(int x, int y, String moves, boolean isPlayer1Turn) {
        // Split the moves string into individual moves.
        String[] moveArray = moves.split(",");

        // Check if the specified cell (x, y) is within bounds.
        if (x < 0 || x >= 3 || y < 0 || y >= 3) {
            return false; // Invalid move, out of bounds.
        }

        // Convert the move coordinates into a single integer for easy checking.
        int moveIndex = x * 3 + y;

        // Check if the specified cell is already occupied.
        if (moveArray.length > moveIndex) {
            return false; // Invalid move, cell is already occupied.
        }

        return isPlayer1Turn; // Check if it's the correct player's turn.
    }

    private boolean isGameOver(int x, int y, String moves) {

        // Split the moves string into individual moves.
        String[] moveArray = moves.split(",");
        
        // Convert the move coordinates into a single integer for easy checking.
        int moveIndex = x * 3 + y;
        
        // Check for a win condition after a move (horizontal, vertical, or diagonal).
        int[] winConditions = { 0b111000000, 0b000111000, 0b000000111, 0b100100100, 0b010010010, 0b001001001, 0b100010001, 0b001010100 };
        
        for (int condition : winConditions) {
            if ((Integer.parseInt(moveArray[moveIndex], 2) & condition) == condition) {
                return true; // Win condition met.
            }
        }
        
        // Check for a draw condition if all cells are filled.
        if (moveArray.length == 9) {
            return true; // The game is a draw.
        }
        
        return false; // Game can continue.
    }

    private void signalOtherPlayer(String gameId) {
        // Implement logic to signal the other player that it's their turn.
        
    }
    
    @Override
    public String test(String str) throws  RemoteException{
        System.out.println("test ausgabe" + str);
        if(str.equals("1")){
            System.out.println("befoore 10 sec");

            try {
                Thread.sleep(10000);
                System.out.println("waiting 10 sec");
            } catch(InterruptedException e){
                e.printStackTrace();
            }


        }else if (str=="2"){

        }
        return "done done";
    }

}
