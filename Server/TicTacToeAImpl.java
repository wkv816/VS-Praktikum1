package Server;


import Regestry.TicTacToeAService;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

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
        PLAYER_A,
        PLAYER_B,
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

    private String firstClient = "";
    private String secondClient = "";
    private String lastMove = "";

    private HashMap<Keys, String> map = new HashMap<>();

    private ArrayList<String> allMoves = new ArrayList<>();

    private boolean youLose = false;

    private boolean thisPlayerFirst = false;

    private boolean isYourTurn;

    private static final Semaphore semaphore = new Semaphore(2, true);
    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException {
        isYourTurn = true; // hier muss irgendwas mit random hin

        try {
            // Bevor die hier hin kommen sollen die von sowas wie einem Semaphor oder so blockiert werden
            semaphore.acquire();
            synchronized (lock) {
                boolean iAmSecond;

                if (firstClient.isEmpty()) {
                    firstClient = clientName;

                    //boolean thisPlayerFirst = random.nextBoolean();
                    thisPlayerFirst = true;

                    doWhile(false);
                    System.out.println(firstClient+ " started Gui");

                } else if (secondClient.isEmpty()) {
                    secondClient = clientName;
                    boolean randomBoolean= random.nextBoolean();
                    randomBoolean=false;

                    if(randomBoolean){
                        map.put(Keys.PLAYER_A, firstClient);
                        map.put(Keys.PLAYER_B, secondClient);
                        System.out.println(secondClient+ " gewartet");
                        doWhile(false);


                    }else {
                        map.put(Keys.PLAYER_A, secondClient);
                        map.put(Keys.PLAYER_B, firstClient);
                    }
                    System.out.println(secondClient+ " started Gui");

                }

                // Der, der als zweites das wait Verlässt kommt immer hier entlang
                if (map.containsKey(Keys.GAMEID)) {
                    return addSecondPlayer(clientName);
                }
                //Der, der als erstes das wait verlässt, kommt immer hier entlang
                return addfirstPlayer(clientName);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //TODO hier das finally befüllen
        }
    }
private void doWhile(boolean thisPlayerFirst){
    do {
        try {
            lock.notify();
            lock.wait();

            //thisPlayerFirst=!thisPlayerFirst;

        } catch (InterruptedException e) {
            // Handle InterruptedException
        }
    } while (thisPlayerFirst);

}
    private HashMap<String,String> addfirstPlayer(String clientName){
        //Der, der als erstes das wait verlässt, kommt immer hier entlang
        String gameId = generateGameId();
        String gameStatus= "waiting-for-player";
        String firstMove= "";


        // Create a new Hashmap session with following values
        map.put(Keys.GAMESTATUS, gameStatus);
        System.out.println("gameid = "+gameId);
        map.put(Keys.GAMEID, gameId);
        map.put(Keys.FRISTMOVE, firstMove);
        //map.put(Keys.FIRSTPLAYER, clientName);
        map.put(Keys.MOVES, "");
        //map.put(Keys.SECONDPLAYER, "");
        map.put(Keys.WINNER, "");
        map.put(Keys.CURRENTPLAYERSTURN,"");
        //System.out.println("Jetzt müsste der Erste dran sein");

        String opponent = map.get(Keys.PLAYER_B);

        return returnfindgameHashMap(gameId, opponent, "your_move");
    }

    private HashMap<String,String> addSecondPlayer(String clientName){
        String gameStatus = "playing";
        String gameID = map.get(Keys.GAMEID);
        String firstPlayer = map.get(Keys.PLAYER_A);
        String secondPlayer = map.get(Keys.PLAYER_B);

        // Randomly choose who begins

        String currentPlayer = thisPlayerFirst ? firstPlayer : secondPlayer; // TODO brauchen wir das noch
        //String firstMove = thisPlayerFirst ? "your_move" : "opponent_move";
        String firstMove = "opponent_move";
        //System.out.println("Jetzt müsste der zweite dran");
        map.put(Keys.GAMESTATUS, gameStatus); // TODO brauchen wir das noch
        //map.put(Keys.SECONDPLAYER, clientName);
        map.put(Keys.CURRENTPLAYERSTURN, currentPlayer);
        map.put(Keys.FRISTMOVE, firstMove); // TODO brauchen wir das noch

        String opponent = map.get(Keys.PLAYER_A);
        return returnfindgameHashMap(gameID, opponent, "opponent_move");
    }

    public HashMap<String, String> returnfindgameHashMap(String gameID,String opponentName,String firstMove) {
        HashMap<String, String> returnMap = new HashMap<>();
        //if (map.get(Keys.GAMEID).equals(gameID)) {

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
        boolean gameSessionExists=map.get(Keys.GAMEID).equals(gameId);
        String moves = map.get(Keys.MOVES);
        synchronized (lock) {

        if (gameSessionExists) {
            lastMove = x + "," + y ;




            System.out.println("zeile 219");

        System.out.println("moves = "+ moves);
        String currentplayerturn = map.get(Keys.CURRENTPLAYERSTURN);
        String firstPlayer = map.get(Keys.PLAYER_A);
        String secondPlayer = map.get(Keys.PLAYER_B);
        //boolean isPlayer1Turn = currentPlayerTurn.getOrDefault(gameId, true);
            System.out.println("zeile 225");
        if (isMoveValid(x, y, moves)) {
            moves=(moves.isEmpty())?  x + "," + y: moves + "|"+ x + "," + y;
            map.put(Keys.MOVES, moves);
            System.out.println(moves);
            // check if game is over
            if (isGameOver(moves)) {
                System.out.println( "game over lastmove = " + lastMove);
                youLose = true;
                lock.notify();
                map.put(Keys.WINNER, currentplayerturn);
                currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                return "you_win: " + x + "," + y;
            }
            // check if game is not over and there are less moves than 9
            String[] moveArray = moves.split("\\|");
            if(moveArray.length < 9) {
                currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                makeMoveWaiting();
                System.out.println("youtlose = " + youLose);
                if(youLose){
                    youLose = false;
                    //resetGameInformtion();
                    return "you_lose: " + lastMove;
                }
                return lastMove;
                //return x + "," + y;
            }
            // if game is over and there are 9 moves

            map.put(Keys.WINNER, "draw");
            return "you_lose: " + x + "," + y;

        } else {
            return "invalid_move";
        }

        }return "game_does_not_exist";
        }
    }

    private void makeMoveWaiting(){

            int i = 0;

            while (i < 1) {
                try {
                    i++;
                    //firstPlayerMadeMove=true;
                    lock.notify();
                    lock.wait();
                } catch (InterruptedException e) {
                    // Handle InterruptedException
                }
            }


    }



    @Override
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException {
        String player_A=map.get(Keys.PLAYER_A);
        String player_B=map.get(Keys.PLAYER_B);
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
        String[] moveParts = moves.split("\\|");
        if(moveParts.length==0){
            return true;
        }

        // Split the "moves" string into individual move parts.
        String playerMove = x + "," + y;

        for (String part : moveParts) {
            if (part.equals(playerMove)) {
                return false; // Invalid move, playerMove already exists in the list of moves.
            }
        }

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


    private void resetGameInformtion(){
        firstClient = "";
        secondClient = "";
        map.clear();

        semaphore.release();
        semaphore.release();
    }
}
