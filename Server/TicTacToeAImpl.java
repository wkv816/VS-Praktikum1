package Server;


import Regestry.TicTacToeAService;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
        CURRENTPLAYERSTURN,
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

    private final Object lock = new Object();
    private final Object lock2 = new Object();

    private final Lock mutex = new ReentrantLock();

    private String firstClient = "";
    private String secondClient = "";
    private int playerCounter = 0;
    private boolean firstPlayerMadeMove = false;
    private String lastMove = "";

    private HashMap<Keys, String> map = new HashMap<>();

    private ArrayList<String> allMoves = new ArrayList<>();

    private boolean youLose = false;

    private boolean thisPlayerFirst = false;

    private boolean isYourTurn;
    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException {
        isYourTurn = true; // hier muss irgendwas mit random hin

        try {
            // Bevor die hier hin kommen sollen die von sowas wie einem Semaphor oder so blockiert werden
            synchronized (lock) {
                boolean iAmSecond;

                if (firstClient.isEmpty()) {
                    firstClient = clientName;

                    //boolean thisPlayerFirst = random.nextBoolean();
                    boolean thisPlayerFirst = true;
                    iAmSecond = thisPlayerFirst;
                    generateGameId();

                    do {
                        try {
                            System.out.println("Client 1 macht wait");
                            playerCounter++;
                            lock.notify();
                            lock.wait();  // 1. Client wartet hier
                            System.out.println("");
                        } catch (InterruptedException e) {
                            // Handle InterruptedException
                        }
                    } while (iAmSecond);

                    iAmSecond = !thisPlayerFirst;
                    System.out.println("1 Client ist draußen");
                } else if (secondClient.isEmpty()) {
                    secondClient = clientName;
                    boolean thisPlayerFirst = random.nextBoolean();
                    iAmSecond = thisPlayerFirst;

                    do {
                        try {
                            System.out.println("Client 2 macht wait");
                            playerCounter++;
                            thisPlayerFirst = random.nextBoolean();

                            lock.notify();
                            lock.wait();
                            playerCounter++;
                            System.out.println("");
                        } catch (InterruptedException e) {
                            // Handle InterruptedException
                        }
                    } while (!iAmSecond);

                    iAmSecond = !thisPlayerFirst;
                    System.out.println("2 Client ist draußen");
                }



                // else if --> Clientname gibt es schon
                    // dann nicht blockieren sondern
                    // return returnfindgameHashMap(gameID, firstPlayerName, "your_opponent");


                // Der, der als zweites das wait Verlässt kommt immer hier entlang
                if (!map.isEmpty()) {

                    String gameStatus = "playing";
                    String gameID = map.get(Keys.GAMEID);
                    String firstPlayer = map.get(Keys.FIRSTPLAYER);
                    String secondPlayer = map.get(Keys.SECONDPLAYER);

                    // Randomly choose who begins

                    String currentPlayer = thisPlayerFirst ? firstPlayer : secondPlayer;
                    //String firstMove = thisPlayerFirst ? "your_move" : "opponent_move";
                    String firstMove = "opponent_move";
                    //System.out.println("Jetzt müsste der zweite dran");
                    map.put(Keys.GAMESTATUS, gameStatus);
                    map.put(Keys.SECONDPLAYER, clientName);
                    map.put(Keys.CURRENTPLAYERSTURN, currentPlayer);
                    map.put(Keys.FRISTMOVE, firstMove);

                    return returnfindgameHashMap(gameID, firstClient, "opponent_move");
                }

                //Der, der als erstes das wait verlässt, kommt immer hier entlang
                String gameId = generateGameId();
                String gameStatus= "waiting-for-player";
                String firstMove= "";


                // Create a new Hashmap session with following values
                map.put(Keys.GAMESTATUS, gameStatus);
                map.put(Keys.GAMEID, gameId);
                map.put(Keys.FRISTMOVE, firstMove);
                map.put(Keys.FIRSTPLAYER, clientName);
                map.put(Keys.MOVES, "");
                map.put(Keys.SECONDPLAYER, "");
                map.put(Keys.WINNER, "");
                map.put(Keys.CURRENTPLAYERSTURN,"");
                //System.out.println("Jetzt müsste der Erste dran sein");

                return returnfindgameHashMap(gameId, secondClient, "your_move");


                /*
                String gameID = generateGameId();
                String opponentName = secondPlayerName;
                String firstPlayer = "your_move";
                String move = "";


                HashMap<String, String> tmpMap = new HashMap<>();
                tmpMap.put("Game ID", gameID);
                tmpMap.put("Opponent Name", opponentName);
                tmpMap.put("First Move", firstPlayer);
                tmpMap.put("Move", move);

                return tmpMap;*/

                /*else {
                    String gameID = "tmpTestGameID";
                    String opponentName = firstPlayerName;
                    String firstPlayer = "opponent_move";
                    String move = lastMove;


                    HashMap<String, String> tmpMap = new HashMap<>();
                    tmpMap.put("Game ID", gameID);
                    tmpMap.put("Opponent Name", opponentName);
                    tmpMap.put("First Move", firstPlayer);
                    tmpMap.put("Move", move);
                    return tmpMap;
                }*/

            }
        } finally {
            //TODO hier das finally befüllen
        }
    }

    public HashMap<String, String> returnfindgameHashMap(String gameID,String opponentName,String firstMove) {
        HashMap<String, String> returnMap = new HashMap<>();
        //if (map.get(Keys.GAMEID).equals(gameID)) {


            String firstPlayer = map.get(Keys.FIRSTPLAYER);
            String secondPlayer = map.get(Keys.SECONDPLAYER);
            //String move = map.get(Keys.MOVES);

            returnMap.put("Game ID", gameID);
            returnMap.put("Opponent Name", opponentName);
            returnMap.put("First Move", firstMove);
            returnMap.put("Move", lastMove);

        //}
        return returnMap;
    }

    @Override
    public String makeMove(int x, int y, String gameId) throws RemoteException {

        lastMove = x + "," + y ;
        allMoves.add(lastMove);
        System.out.println("Lastmove [" + x +"," + y + "]");

        synchronized (lock) {
            int i = 0;

            while (i < 1) {
                try {
                    i++;
                    firstPlayerMadeMove=true;
                    lock.notify();
                    lock.wait();
                } catch (InterruptedException e) {
                    // Handle InterruptedException
                }
            }
        }

        if(youLose){
            youLose = false;
            // TODO in der Methode resetGameInformtion()muss alles zurückgesetzt werden
            // resetGameInformtion()
            return "you_lose" + x + "," + y;
        }

        String moves = map.get(Keys.MOVES);
        String currentplayerturn = map.get(Keys.CURRENTPLAYERSTURN);
        String firstPlayer = map.get(Keys.FIRSTPLAYER);
        String secondPlayer = map.get(Keys.SECONDPLAYER);
        //boolean isPlayer1Turn = currentPlayerTurn.getOrDefault(gameId, true);

        if (isMoveValid(x, y, moves)) {
            if (!moves.isEmpty()) {
                moves += "|";
            }
            moves +="," + x + "," + y;


            //if (isGameOver(x, y, moves)) {
            if (isGameOver(lastMove)) {
                youLose = true;
                lock.notify();
                return "you_win: " + x + "," + y;
            } else {
                currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                //return x + "," + y;
            }
            return lastMove;

        } else {
            return "invalid_move";
        }
        //return "game_does_not_exist";
        //return lastMove;
    }



    @Override
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException {
        String player_A=map.get(Keys.FIRSTPLAYER);
        String player_B=map.get(Keys.SECONDPLAYER);
        boolean tmpbool = true;
        String moves=map.get(Keys.MOVES);
        String [] movesparts = moves.split("\\|");

        ArrayList <String>ruckgabe_array= new ArrayList<>();

        for(String str: movesparts){
            if (tmpbool) {
                String str_tmp= player_A+ ": " + str;
                ruckgabe_array.add( str_tmp);
                tmpbool=false;
            }
            else{
                String str_tmp= player_B+ ": " + str;
                ruckgabe_array.add( str_tmp);
                tmpbool=true;
            }

        }
        return ruckgabe_array;
}



    private String generateGameId() {
        return "Game" + random.nextInt(1000);
    }

     public boolean isMoveValid(int x, int y,String moves) {

        // Check if the move is out of bounds (0-2 for both x and y).
        if (x < 0 || x >= 3 || y < 0 || y >= 3) {
            return false; // Invalid move, out of bounds.
        }

        // Convert the move coordinates into a single string for easy checking.
        // Split the "moves" string into individual move parts.
        String playerMove = x + "," + y;
        //String[] moveParts = moves.split("\\|");

        /*
        for (String part : moveParts) {
            // Trim whitespace and extract the last 3 characters (assuming they represent "x,y").
            part = part.trim();
            part = part.substring(part.length() - 3);
            if (part.equals(playerMove)) {
                return false; // Invalid move, playerMove already exists in the list of moves.
            }
        }
        */

        return true; // Move is valid.
    }

    public boolean isGameOver(String moves) {
        String[] moveParts = moves.split("\\|");
        int lenghtofmoves= moveParts.length;
        System.out.println(lenghtofmoves);
        boolean currentPlayerTurn= lenghtofmoves%2==0? false: true;
        List<String> playerMoves = new ArrayList<>();

        for (int i = currentPlayerTurn ? 0 : 1; i < moveParts.length; i += 2) {
            playerMoves.add(moveParts[i]);
        }

        String[][] winConditions = {
                {"0,0", "0,1", "0,2"},
                {"1,0", "1,1", "1,2"},
                {"2,0", "2,1", "2,2"},
                {"0,0", "1,0", "2,0"},
                {"0,1", "1,1", "2,1"},
                {"0,2", "1,2", "2,2"},
                {"0,0", "1,1", "2,2"},
                {"0,2", "1,1", "2,0"}
        };

        for (String[] condition : winConditions) {
            if (playerMoves.containsAll(Arrays.asList(condition))) {
                return true;
            }
        }
        return false;
    }
    /*
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
    }*/


    private void signalOtherPlayer(String gameId) {
        // Implement logic to signal the other player that it's their turn.
        
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

    private void resetGameInformtion(){
        firstClient = "";
        secondClient = "";
        map.clear();
    }

    public static void main(String[] args) {
        System.out.println("hello");
        for (String string : splitStringToList("0,0,0,2,0,1,1,1")) {
            System.out.println(string);
        }
    }
}
