package Server;


import Regestry.TicTacToeAService;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class TicTacToeAImpl implements TicTacToeAService {

    //private HashMap<String, String> gameMap = new HashMap<>();
    private ArrayList<HashMap<Keys, String>> gameArrayList = new ArrayList<>();
    //private HashMap<String, String> returnfindgameHashMap = new HashMap<>();
    private HashMap<String, Boolean> currentPlayerTurn = new HashMap<>();
    private Random random = new Random();
    private Map<String, String> clientSessionRegistry = new ConcurrentHashMap<>();
    public enum Keys {
        GAMESTATUS,
        GAMEID,
        FRISTMOVE,
        MOVES,
        FIRSTPLAYER,
        SECONDPLAYER,
        WINNER,
        CURRENTPLAYERSTURN
    }
    /*) 
     * public enum Values {
            WAITINGFORPLAYER,
            PLAYING,
            YOURMOVE,
            OPPONENTMOVE,
            NOOPPONENTFOUND,
        }
     */
    


    // Game ID:                 "Game" + random number
    // Status:                  "waiting-for-player" | "playing"
    // First move:              "your_move" | "opponent_move" | "no_opponent_found"
    // Move:                    "playerX = x,y"
    // First player:            "player 1"
    // Second player:           "player 2"
    // Winner:                  "you_win: x,y" | "you_lose: x,y" | "no_winner"
    // Current Player Turn:     "playerX"


    // "Game ID", "Opponent Name", "First Move", "Move"
    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException {
        // A game is found if there is a waiting-for-player game with no opponent yet  
        for (HashMap<Keys, String> map : gameArrayList) {
            if (map.get(Keys.GAMESTATUS).equals("waiting-for-player")) {

                String gameStatus="playing";
                String gameID= map.get(Keys.GAMEID);
                String firstPlayer= map.get(Keys.FIRSTPLAYER);
                String secondPlayer= map.get(Keys.SECONDPLAYER);

                // Randomly choose who begins
                boolean thisPlayerFirst = random.nextBoolean();
                String currentPlayer = thisPlayerFirst ? firstPlayer : secondPlayer;
                String firstMove = thisPlayerFirst ? "your_move" : "your_opponent";

                // change game status to Playing
                // Assign values to the keys
                map.put(Keys.GAMESTATUS, gameStatus);
                map.put(Keys.SECONDPLAYER, clientName);
                map.put(Keys.CURRENTPLAYERSTURN, currentPlayer);
                map.put(Keys.FRISTMOVE, firstMove);

                return returnfindgameHashMap(gameID, clientName, gameStatus, firstMove);
            }
        }

        // No game found, create a new one in "waiting-for-player" state
        return creatGameSession(clientName);

    }


    public void registerClient(String clientID) throws ServerNotActiveException {
        String clientSessionID = RemoteServer.getClientHost();
        clientSessionRegistry.put(clientSessionID, clientID);
    }

    @Override
    public String makeMove(int x, int y, String gameId) throws RemoteException {
        for (HashMap<Keys, String> map : gameArrayList) { 
            if (map.get(Keys.GAMEID).equals(gameId)) {
                
                String moves = map.get(Keys.MOVES);
                String currentplayerturn= map.get(Keys.CURRENTPLAYERSTURN);
                String firstPlayer = map.get(Keys.FIRSTPLAYER);
                String secondPlayer = map.get(Keys.SECONDPLAYER);
                //boolean isPlayer1Turn = currentPlayerTurn.getOrDefault(gameId, true);
        
                if (isMoveValid(x, y, moves)) {
                    if (!moves.isEmpty()) {
                        moves += "|";
                    }
                    moves += cuurentplayerturn + "," + x + "," + y;
        
                    if (isGameOver(x, y, moves)) {
                        return "you_win: " + x + "," + y;
                    } else {

                        currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                        map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                        return x + "," + y;
                    }
                } else {
                    return "invalid_move";
                }
            }
        }
            return "game_does_not_exist";
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

    private HashMap<String, String> creatGameSession(String clientName) {

        HashMap<Keys, String> session = new HashMap<>();
        // Create a new gameID
        String gameId = generateGameId();
        String gameStatus= "waiting-for-player";
        String firstMove= "no_opponent_found";
        

        // Create a new Hashmap session with following values
        session.put(Keys.GAMESTATUS, gameStatus);
        session.put(Keys.GAMEID, gameId);
        session.put(Keys.FRISTMOVE, firstMove);
        session.put(Keys.FIRSTPLAYER, clientName);
        session.put(Keys.MOVES, "");
        session.put(Keys.SECONDPLAYER, "");
        session.put(Keys.WINNER, "");
        session.put(Keys.CURRENTPLAYERSTURN,"");

        gameArrayList.add(session);

        return returnfindgameHashMap(gameId, clientName, gameStatus, firstMove);
        
    }

    public HashMap<String, String> returnfindgameHashMap(String gameID,String clientName, String gameStatus,String firstMove) {
        HashMap<String, String> returnMap = new HashMap<>();

        for (HashMap<Keys, String> map : gameArrayList) {
            if (map.get(Keys.GAMEID).equals(gameID)) {

                
                String firstPlayer = map.get(Keys.FIRSTPLAYER);
                String secondPlayer = map.get(Keys.SECONDPLAYER);
                String opponentName = (secondPlayer.equals("")) ? "no_opponent_found" : (firstPlayer.equals(clientName)) ? secondPlayer : firstPlayer;
                String move = map.get(Keys.MOVES);

                // move = "" then nothing happens otherwise move = substring of last 3 characters "x,y".
                move = (move.isEmpty()) ? "" : move.substring(move.length() - 3);
                                
                returnMap.put("Game ID", gameID);
                returnMap.put("First Move", firstMove);
                returnMap.put("Move", move);
                returnMap.put("Opponent Name", opponentName);
            }        
        }
        return returnMap;	
    }

    public boolean isMoveValid(int x, int y,String moves) {

        // Check if the move is out of bounds (0-2 for both x and y).
        if (x < 0 || x >= 3 || y < 0 || y >= 3) {
            return false; // Invalid move, out of bounds.
        }

        // Convert the move coordinates into a single string for easy checking.
        // Split the "moves" string into individual move parts.
        String playerMove = x + "," + y;
        String[] moveParts = moves.split("\\|");


        for (String part : moveParts) {
            // Trim whitespace and extract the last 3 characters (assuming they represent "x,y").
            part = part.trim();
            part = part.substring(part.length() - 3);
            if (part.equals(playerMove)) {
                return false; // Invalid move, playerMove already exists in the list of moves.
            }
        }

        return true; // Move is valid.
    }

    public boolean isGameOver(int x, int y, String moves) {
        // Split the moves string into individual moves.
        String[] moveArray = moves.split(",");
        
        // Check for a win condition after a move (horizontal, vertical, or diagonal).
        char currentPlayer = moveArray.length % 2 == 0 ? 'X' : 'O';
        
        // Check for horizontal win condition
        for (int row = 0; row < 3; row++) {
            if (moveArray[row * 3].charAt(0) == currentPlayer && 
                moveArray[row * 3 + 1].charAt(0) == currentPlayer && 
                moveArray[row * 3 + 2].charAt(0) == currentPlayer) {
                return true; // Horizontal win
            }
        }
        
        // Check for vertical win condition
        for (int col = 0; col < 3; col++) {
            if (moveArray[col].charAt(0) == currentPlayer && 
                moveArray[col + 3].charAt(0) == currentPlayer && 
                moveArray[col + 6].charAt(0) == currentPlayer) {
                return true; // Vertical win
            }
        }
        
        // Check for diagonal win conditions
        if ((moveArray[0].charAt(0) == currentPlayer && moveArray[4].charAt(0) == currentPlayer && moveArray[8].charAt(0) == currentPlayer) ||
            (moveArray[2].charAt(0) == currentPlayer && moveArray[4].charAt(0) == currentPlayer && moveArray[6].charAt(0) == currentPlayer)) {
            return true; // Diagonal win
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

        String clientSessionID = RemoteServer.getClientHost();
        String clientID = clientSessionRegistry.get(clientSessionID);

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



    

        public static List<String> splitStringToList(String input) {

            List<String> resultList = new ArrayList<>();
            String[] parts = input.split(",");
            int n = parts.length;

            for (int i = 0; i < n; i += 2) {
                if (i + 1 < n) {
                    String pair = parts[i] + "," + parts[i + 1];
                    resultList.add(pair);
                }
            }
            return resultList;
        }

        public static void main(String[] args) {
            System.out.println("hello");
            for (String string : splitStringToList("0,0,0,2,0,1,1,1")) {
                System.out.println(string);
            }
            
        }



}
