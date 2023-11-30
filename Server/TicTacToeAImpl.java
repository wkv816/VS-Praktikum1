package Server;


import Regestry.TicTacToeAService;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TicTacToeAImpl implements TicTacToeAService {

    //private HashMap<String, String> gameMap = new HashMap<>();
    private ArrayList<HashMap<Keys, String>> gameArrayList = new ArrayList<>();
    private Timer timer;
    private boolean timeout = false;

    private HashMap<String, Boolean> currentPlayerTurn = new HashMap<>();
    private HashMap<String, ArrayList<String>> gameHistory = new HashMap<>();
    private Random random = new Random();
    private Map<String, String> clientSessionRegistry = new ConcurrentHashMap<>();
    public enum Keys {GAMESTATUS, GAMEID, FRISTMOVE, MOVES, PLAYER_A, PLAYER_B, WINNER, CURRENTPLAYERSTURN}

    Logger logger = Logger.getLogger(TicTacToeAImpl.class.getName());
    FileHandler fileHandler;


    private final Object lock = new Object();

    private String firstClient = "";
    private String secondClient = "";
    private String lastMove = "";

    private HashMap<Keys, String> map = new HashMap<>();

    private ArrayList<String> timeoutList = new ArrayList<>();

    private boolean youLose = false;

    private boolean thisPlayerFirst = false;

    private boolean isDraw = false;

    public TicTacToeAImpl(){
        // Set up a file handler to write logs to a file named "logfile.log"
        try {
            fileHandler= new FileHandler("logfile.log");
            // Set the formatter for the file handler
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            // Add the file handler to the logger
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("server started");

    }




    private static final Semaphore semaphore = new Semaphore(2, true);
    @Override
    public HashMap<String, String> findGame(String clientName) throws RemoteException {
        logger.info(clientName + " wants to play TicTacToe");

        String tmpGameID = map.get(Keys.GAMEID);
        String firstPlayer = map.get(Keys.PLAYER_A);
        String secondPlayer = map.get(Keys.PLAYER_B);

        if(clientName.equals(firstPlayer)){
            logger.info("Reconnect the player " + clientName );
            return returnfindgameHashMap(tmpGameID, secondPlayer, "your_move");
        } else if(clientName.equals(secondPlayer)){
            logger.info("Reconnect the player " + clientName );
            return returnfindgameHashMap(tmpGameID, firstPlayer, "opponent_move");
        }

        if(clientName.equals(firstClient)){
            logger.info("This name already exists " + clientName );
            return returnfindgameHashMap(tmpGameID, firstPlayer, "no_opponent_found");
        }



        try {
            // Bevor die hier hin kommen sollen die von sowas wie einem Semaphor oder so blockiert werden
            semaphore.acquire();
            synchronized (lock) {
                boolean iAmSecond;

                if (firstClient.isEmpty()) {
                    logger.info(clientName + " is waiting for an opponent to join");
                    firstClient = clientName;
                    //thisPlayerFirst = true;
                    doWhile(false);
                } else if (secondClient.isEmpty()) {
                    secondClient = clientName;
                    thisPlayerFirst = random.nextBoolean();
                    //randomBoolean=false;

                    if(thisPlayerFirst){
                        map.put(Keys.PLAYER_A, firstClient);
                        map.put(Keys.PLAYER_B, secondClient);
                        //System.out.println(secondClient+ " gewartet");
                        doWhile(false);
                    }else {
                        map.put(Keys.PLAYER_A, secondClient);
                        map.put(Keys.PLAYER_B, firstClient);
                    }
                    //System.out.println(secondClient+ " started Gui");
                }


                // Der, der als zweites das wait Verlässt kommt immer hier entlang
                if (map.containsKey(Keys.GAMEID)) {
                    return addSecondPlayer(clientName);
                }

                return addfirstPlayer(clientName);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }
    private void doWhile(boolean thisPlayerFirst){
        do {
            try {
                lock.notify();
                lock.wait();
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
        logger.info("Start new Game : Gameid: " + gameId + " with " + firstClient + " and " + secondClient);
        // Create a new Hashmap session with following values
        map.put(Keys.GAMESTATUS, gameStatus);
        map.put(Keys.GAMEID, gameId);
        map.put(Keys.FRISTMOVE, firstMove);
        map.put(Keys.MOVES, "");
        map.put(Keys.WINNER, "");
        map.put(Keys.CURRENTPLAYERSTURN,"");
        String opponent = map.get(Keys.PLAYER_B);

        return returnfindgameHashMap(gameId, opponent, "your_move");
    }

    private HashMap<String,String> addSecondPlayer(String clientName){

        String gameStatus = "playing";
        String gameID = map.get(Keys.GAMEID);
        //System.out.println("\n ==================== \n  Start new Game : Gameid: " + gameID + " \n==================== \n");
        String firstPlayer = map.get(Keys.PLAYER_A);
        String secondPlayer = map.get(Keys.PLAYER_B);

        String currentPlayer = thisPlayerFirst ? firstPlayer : secondPlayer;
        String firstMove = "opponent_move";
        map.put(Keys.GAMESTATUS, gameStatus);
        map.put(Keys.CURRENTPLAYERSTURN, currentPlayer);
        map.put(Keys.FRISTMOVE, firstMove);

        String opponent = map.get(Keys.PLAYER_A);

        timeout = false;
        startTimer();
        return returnfindgameHashMap(gameID, opponent, "opponent_move");
    }

    public HashMap<String, String> returnfindgameHashMap(String gameID,String opponentName,String firstMove) {
        HashMap<String, String> returnMap = new HashMap<>();
            returnMap.put("Game ID", gameID);
            returnMap.put("Opponent Name", opponentName);
            returnMap.put("First Move", firstMove);
            returnMap.put("Move", lastMove);
        return returnMap;
    }

    @Override
    public String makeMove(int x, int y, String gameId) throws RemoteException {
        logger.info("Game-ID[" + gameId + "] Recived move: " + x + "," + y);

        if(timeoutList.contains(gameId)){
            return "game_does_not_exist";
        }
        
        boolean gameSessionExists=map.get(Keys.GAMEID).equals(gameId);
        String moves = map.get(Keys.MOVES);
        synchronized (lock) {

            if (gameSessionExists) {
                resetTimer();
                lastMove = x + "," + y ;
                String currentplayerturn = map.get(Keys.CURRENTPLAYERSTURN);
                String firstPlayer = map.get(Keys.PLAYER_A);
                String secondPlayer = map.get(Keys.PLAYER_B);
                if (isMoveValid(x, y, moves)) {
                    moves=(moves.isEmpty())?  x + "," + y: moves + "|"+ x + "," + y;
                    map.put(Keys.MOVES, moves);
                    //System.out.println(moves);
                    // check if game is over
                    String[] moveArray = moves.split("\\|");
                    if (isGameOver(moves)) {
                        youLose = true;
                        lock.notify();
                        map.put(Keys.WINNER, currentplayerturn);
                        currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                        logger.info( currentplayerturn + "Wins with the lastmove = " + lastMove);
                        map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                        logger.info("Send you_win to " + currentplayerturn);
                        return "you_win: " + x + "," + y;
                    } else if(8 < moveArray.length ) {
                        isDraw = true;
                        lock.notify();
                        map.put(Keys.WINNER, currentplayerturn);
                        currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                        map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                        return lastMove;
                    }

                    //makeMoveWaiting();
                    makeMoveWaiting();
                    if(timeout){
                        resetGameInformation();
                        logger.info("Send opponent_gone");
                        return "opponent_gone";
                    }
                    if(!isDraw) {
                        currentplayerturn = currentplayerturn.equals(firstPlayer) ? secondPlayer : firstPlayer;
                        map.put(Keys.CURRENTPLAYERSTURN, currentplayerturn);
                        //System.out.println("youtlose = " + youLose);
                        if(youLose){
                            youLose = false;
                            String loseText= "you_lose: " + lastMove;
                            logger.info("Send you_lose to " + currentplayerturn);
                            resetGameInformation();
                            return loseText;
                        }
                        logger.info("Send move to " +currentplayerturn+ ": " + lastMove);
                        return lastMove;
                    } else {
                        // map.put(Keys.WINNER, "draw");
                        logger.info("draw");
                        System.out.println("rrcdefrtvumjio,kp.ldgdggdgddggdgd");
                        String tmpLastMove = lastMove;
                        resetGameInformation();
                        return tmpLastMove;
                    }
                    // return "you_lose: " + x + "," + y;
                } else {
                    logger.info("invalid_move");
                    return "invalid_move";
                }
            }
            logger.info("game_does_not_exist");
            return "game_does_not_exist";
        }
    }

    private void makeMoveWaiting(){
        try {
            lock.notify();
            lock.wait();
            //System.out.println("Wait verlassen");
        } catch (InterruptedException e) {
            // Handle InterruptedException
        }
    }



    @Override
    public ArrayList<String> fullUpdate(String gameId) throws RemoteException {
        if(gameHistory.containsKey(gameId)){
            return gameHistory.get(gameId);
        }
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

        if (x < 0 || x >= 3 || y < 0 || y >= 3) {
            return false; // Invalid move, out of bounds.
        }
        String[] moveParts = moves.split("\\|");
        if(moveParts.length==0){
            return true;
        }
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

    private void resetGameInformation() throws RemoteException {
        String tmpGameID = map.get(Keys.GAMEID);
        if(!timeoutList.contains(tmpGameID)){
            ArrayList<String> gameMoves = fullUpdate(tmpGameID);
            gameHistory.put(tmpGameID, gameMoves);
        }
        //System.out.println(fullUpdate(map.get(Keys.GAMEID)));
        logger.info("Game has endet");
        //System.out.println("Game wurde beendet");
        firstClient = "";
        secondClient = "";
        lastMove="";
        isDraw =false;
        map.clear();
        stopTimer();

        semaphore.release();
        semaphore.release();
        //System.out.println("Anzahl der Semapore ist jetzt: " + semaphore.availablePermits() );
    }


    private void startTimer() {
        // Start or reset the timer to clear players if makeMove is not called within 30 seconds.
        if (timer != null) {
            timer.cancel();
        }
        //System.out.println("Timer started.");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Timer expired. Clearing players.");
                logger.info("******* Timeout ******");
                timeout = true;
                String currentTmpID = map.get(Keys.GAMEID);
                timeoutList.add(currentTmpID);
                synchronized (lock) {

                    lock.notify();
                }
            }
        }, 40000);
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



}
