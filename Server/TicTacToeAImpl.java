package Server;


import Regestry.TicTacToeAService;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class TicTacToeAImpl implements TicTacToeAService {

    private HashMap <Keys, String> map= new HashMap<>();
    private Random random = new Random();
    private Queue<String> playerQueue = new LinkedList<>();
    private Timer timer;
    public enum Keys {
        GAMESTATUS,
        GAMEID,
        FRISTMOVE,
        MOVES,
        PLAYER_A,
        PLAYER_B,
        WINNER,
        CURRENTPLAYERSTURN
    }
    


    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException {
        boolean gameSesionExists=map.containsKey(Keys.GAMEID);
        
        // A game is found if there is a waiting-for-player game with no opponent yet  

        if (gameSesionExists) {

            boolean secondPlayerExists=!map.get(Keys.GAMESTATUS).equals("waiting-for-player");

            if (!secondPlayerExists) {
                addsecondplayer(clientName);
            }
            String player_A=map.get(Keys.PLAYER_A);
            String player_B = map.get(Keys.PLAYER_B);

            boolean clientAlreadyInGameSession= (player_A.equals(clientName))||(player_B.equals(clientName));

            if (clientAlreadyInGameSession) {
                
            }

            
            
        }
        System.out.println("First client name "+ clientName);
        

        // No game found, create a new one in "waiting-for-player" state
        return addFirstPlayer(clientName);

    }
    private HashMap<String, String>  addsecondplayer(String clientName){

        String gameStatus="playing";
                String gameID= map.get(Keys.GAMEID);

                

                // change game status to Playing and Assign values to the keys
                map.put(Keys.GAMESTATUS, gameStatus);
                map.put(Keys.PLAYER_B, clientName);

                String firstMove=yourOrOpponentMove(clientName);
                map.put(Keys.FRISTMOVE, firstMove);

                startTimer();

                System.out.println("Second client name "+ clientName);

                System.out.println("curent turn player "+ map.get(Keys.CURRENTPLAYERSTURN));

                return returnfindgameHashMap(gameID, clientName, gameStatus, firstMove);

    }

    
    private String yourOrOpponentMove(String clientName){
        // Randomly choose who begins
        boolean thisPlayerFirst = random.nextBoolean();
        String firstMove = thisPlayerFirst ? "your_move" : "your_opponent";
        String player_A= map.get(Keys.PLAYER_A);
        String player_B= map.get(Keys.PLAYER_B);

        String currentPlayer = thisPlayerFirst ? player_A : player_B;

        if (!(currentPlayer==player_A)){
            map.put(Keys.PLAYER_A, player_B);
            map.put(Keys.PLAYER_B, player_A);
            map.put(Keys.CURRENTPLAYERSTURN, player_A);
            
        }

        map.put(Keys.CURRENTPLAYERSTURN, currentPlayer);
        
        return firstMove;
    }


    @Override
    public String makeMove(int x, int y, String gameId) throws RemoteException {
        
        if (map.get(Keys.GAMEID).equals(gameId)) {
            resetTimer();
            System.out.println("makemove invoked reset timer");
            String moves = map.get(Keys.MOVES);
            
            
            String currentplayerturn= map.get(Keys.CURRENTPLAYERSTURN);
            String firstPlayer = map.get(Keys.PLAYER_A);
            String secondPlayer = map.get(Keys.PLAYER_B);
            // check if move is valid.
            if (isMoveValid(x, y, moves)) {
                moves=(moves.isEmpty())?  x + "," + y: moves + "|"+ x + "," + y;
                map.put(Keys.MOVES, moves);
                System.out.println(moves);
                // check if game is over
                if (isGameOver(moves)) {
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
                    return x + "," + y;
                }
                // if game is over and there are 9 moves
                map.put(Keys.WINNER, "draw");
                return "you_lose: " + x + "," + y;

            } else {
                return "invalid_move";
            }
        }
        
            return "game_does_not_exist";
    }

    @Override
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException {

        ArrayList <String> list = new ArrayList<>();
        if(map.get(Keys.GAMEID).equals(gameId)){

            list.add(map.get(Keys.GAMESTATUS));
            list.add(map.get(Keys.GAMEID));
            list.add(map.get(Keys.FRISTMOVE));
            list.add(map.get(Keys.MOVES));
            list.add(map.get(Keys.PLAYER_A));
            list.add(map.get(Keys.PLAYER_B));
            list.add(map.get(Keys.WINNER));
            list.add(map.get(Keys.CURRENTPLAYERSTURN));

            return list;
        }

        return list;
    }



    private String generateGameId() {
        return "Game" + random.nextInt(1000);
    }

    private HashMap<String, String> addFirstPlayer(String clientName) {

        // Create a new gameID
        String gameId = generateGameId();
        String gameStatus= "waiting-for-player";
        String firstMove= "no_opponent_found";
        

        // Create a new Hashmap session with following values
        map.put(Keys.GAMESTATUS, gameStatus);
        map.put(Keys.GAMEID, gameId);
        map.put(Keys.FRISTMOVE, firstMove);
        map.put(Keys.PLAYER_A, clientName);
        map.put(Keys.MOVES, "");
        map.put(Keys.PLAYER_B, "");
        map.put(Keys.WINNER, "");
        map.put(Keys.CURRENTPLAYERSTURN,"");

        return returnfindgameHashMap(gameId, clientName, gameStatus, firstMove);
        
    }

    public HashMap<String, String> returnfindgameHashMap(String gameID,String clientName, String gameStatus,String firstMove) {
        HashMap<String, String> returnMap = new HashMap<>();

        if (map.get(Keys.GAMEID).equals(gameID)) {

            String player_A = map.get(Keys.PLAYER_A);
            String player_B = map.get(Keys.PLAYER_B);
            String opponentName = (player_B.equals("")) ? "no_opponent_found" : (player_A.equals(clientName)) ? player_B : player_A;
            String move = map.get(Keys.MOVES);

            // move = "" then nothing happens otherwise move = substring of last 3 characters "x,y".
            move = (move.isEmpty()) ? "" : move.substring(move.length() - 3);
                            
            returnMap.put("Game ID", gameID);
            returnMap.put("First Move", firstMove);
            returnMap.put("Move", move);
            returnMap.put("Opponent Name", opponentName);
        }
        System.out.println(returnMap);
        return returnMap;	
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
    
    public boolean isGameOver2(String[] moveParts) {
    int lenghtofmoves= moveParts.length;
    System.out.println(lenghtofmoves);
    boolean currentPlayerTurn= lenghtofmoves%2==0? false: true;
    List<String> resultArray;
    List<String> array1 = new ArrayList<>();
    List<String> array2 = new ArrayList<>();

    for (int i = 0; i < moveParts.length; i++) {
        if (i % 2 == 0) {
            array1.add(moveParts[i]);
        } else {
            array2.add(moveParts[i]);
        }
    }

    if (currentPlayerTurn) {
        resultArray = array1;
    } else {
        resultArray = array2;
    }

    if(resultArray.contains("0,0") && resultArray.contains("0,1") && resultArray.contains("0,2")){
        return true;
    }
    if(resultArray.contains("1,0") && resultArray.contains("1,1") && resultArray.contains("1,2")){
        return true;
    }
    if(resultArray.contains("2,0") && resultArray.contains("2,1") && resultArray.contains("2,2")){
        return true;
    }
    if(resultArray.contains("0,0") && resultArray.contains("1,0") && resultArray.contains("2,0")){
        return true;
    }
    if(resultArray.contains("0,1") && resultArray.contains("1,1") && resultArray.contains("2,1")){
        return true;
    }
    if(resultArray.contains("0,2") && resultArray.contains("1,2") && resultArray.contains("2,2")){
        return true;
    }
    if(resultArray.contains("0,0") && resultArray.contains("1,1") && resultArray.contains("2,2")){
        return true;
    }
    if(resultArray.contains("0,2") && resultArray.contains("1,1") && resultArray.contains("2,0")){
        return true;
    }
        return false;
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
     


    public static void main(String[] args) {

        System.out.println("Server started");
        
    }

    private void startTimer() {
        // Start or reset the timer to clear players if makeMove is not called within 30 seconds.
        if (timer != null) {
            timer.cancel();
        }
        System.out.println("Timer started.");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer expired. Clearing players.");
                clearPlayers();
            }
        }, 10000);
    }
    private void resetTimer() {
        // Reset the timer when makeMove is called.
        if (timer != null) {
            timer.cancel();
            startTimer();  // Restart the timer after canceling it.
        }
    }

    private void stopTimer() {
        // Stop the timer when the second player joins the game.
        if (timer != null) {
            timer.cancel();
        }
    }

    private void clearPlayers() {
        // Clear players and reset the queue.
        map.put(Keys.PLAYER_A, "");
        map.put(Keys.PLAYER_B, "");
        playerQueue.clear();
        System.out.println("Players cleared.");
    }




}
