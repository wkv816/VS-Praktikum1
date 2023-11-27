package Client;
import Regestry.TicTacToeAService;

import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.*;
public class GuiGame implements ActionListener{


    private String firstPlayer;
    private String secondPlayer;
    private TicTacToeAService tttAService;
    private String gameID;
    private String name;
    private String opponent_name;

    private String firstMove;
    private String move;

    private Boolean scannerOn = true;

    private Random random =  new Random();
    private JFrame frame = new JFrame();
    private JPanel title_panel = new JPanel();
    private JPanel button_panel = new JPanel();
    private JLabel textfield = new JLabel();
    private JButton[][] buttons = new JButton[3][3];
    private boolean isFirstPlayer;


    public GuiGame(TicTacToeAService ticTacToeAService,String name, String gameID, String opponentName, String firstMove, String move) throws RemoteException {

        tttAService = ticTacToeAService;
        this.name = name;
        this.gameID = gameID;
        this.opponent_name = opponentName;
        this.firstMove = firstMove;
        this.move = move;
        creatGUI();


        System.out.println("firstMove: " + firstMove);
        firstPlayer(firstMove);
        ArrayList<String> currentGameField = ticTacToeAService.fullUpdate(gameID);
        if(firstMove.equals("your_move")){
            firstPlayer = name;
            secondPlayer = opponent_name;
        } else{
            firstPlayer = opponent_name;
            secondPlayer = name;
        }

        ArrayList<String> update = tttAService.fullUpdate(gameID);

        if(1 < update.size()){
            waitForMyTurn(update);
        }else if(!move.equals("")) {
            int x = Integer.parseInt(move.substring(0,1));
            int y = Integer.parseInt(move.substring(2,3));
            //System.out.println(" x: " + x + " und y: " +y);
            playerMove(x,y,!isFirstPlayer);
        }

    }

    private void waitForMyTurn(ArrayList<String> update) throws RemoteException {
        setEnabledAllButtons(false);
        updateField(update);

        while(true) {

            String lastPlaerMove = update.get(update.size() - 1);
            String playerName = lastPlaerMove.substring(0, lastPlaerMove.length() - 5);


            if (update.get(update.size() - 1).startsWith("winner")) {
                lastPlaerMove = update.get(update.size() - 2);
                playerName = lastPlaerMove.substring(0, lastPlaerMove.length() - 5);

                String cordinate = lastPlaerMove.substring(lastPlaerMove.length() - 3);
                int x = Integer.parseInt(cordinate.substring(0, 1));
                int y = Integer.parseInt(cordinate.substring(2, 3));

                if (name.equals(playerName)) {
                    textfield.setText("you_win: " + x + "," + y);
                } else {
                    textfield.setText("you_lose: " + x + "," + y);
                }
                break;
            } else if (opponent_name.equals(playerName)) {
                String cordinate = lastPlaerMove.substring(lastPlaerMove.length() - 3);
                int x = Integer.parseInt(cordinate.substring(0, 1));
                int y = Integer.parseInt(cordinate.substring(2, 3));
                playerMove(x,y,!isFirstPlayer);
                setEnabledAllButtons(true);
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            update = tttAService.fullUpdate(gameID);
            if(update.isEmpty()){
                textfield.setText("opponent_gone");
                break;
            }
        }
    }

    private void updateField(ArrayList<String> playMoveList){

        for(String playMove: playMoveList){
            String cordinate = playMove.substring(playMove.length() - 3);
            int x = Integer.parseInt(cordinate.substring(0,1));
            int y = Integer.parseInt(cordinate.substring(2,3));

            String playerName = playMove.substring(0, playMove.length() - 5);
            System.out.println("PlayerName: " + playerName);
            if(firstPlayer.equals(playerName)){
                playerMove(x,y,true);
            } else{
                playerMove(x,y,false);
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (e.getSource() == buttons[i][j]) {
                    if (buttons[i][j].getText().equals("")) {
                        int finalI = i;
                        int finalJ = j;
                        Thread thread = new Thread(() -> {
                            playerMove(finalI, finalJ,isFirstPlayer);
                            gameMoves(finalI, finalJ);
                        });
                        thread.start();
                        //getCordinate(i, j);
                    }
                }
            }
        }
    }

    private void playerMove(int i, int j,boolean bool){
        String playerturn="";

        if (bool) {
            buttons[i][j].setForeground(new Color(255, 0, 0));
            buttons[i][j].setText("X");
            playerturn=secondPlayer;
        } else {
            buttons[i][j].setForeground(new Color(0, 0, 255));
            buttons[i][j].setText("O");
            playerturn=firstPlayer;
        }
        textfield.setText("PN="+ name + " "+"ON= "+ opponent_name +
                " Turn= "+ playerturn);
    }

    private void gameMoves(int i, int j) {
        try {
            setEnabledAllButtons(false);
            String opponentAwnser;
            opponentAwnser = tttAService.makeMove(i, j,gameID);
            System.out.println("opponent Answer = " + opponentAwnser);

            if (opponentAwnser.startsWith("you") ){
                if (opponentAwnser.startsWith("you_lose:")){
                    int lose_X=Character.getNumericValue(opponentAwnser.charAt(10));
                    int lose_Y=Character.getNumericValue(opponentAwnser.charAt(12));
                    playerMove(lose_X, lose_Y, !isFirstPlayer);
                }
                
                textfield.setText(opponentAwnser);
                return;
            }
            switch (opponentAwnser){
                case "opponent_gone": // Spiel ist zu ende
                    System.out.println("opponentAwnser: 'opponent_gone'");
                    textfield.setText("opponent_gone");
                    return;
                    //frame.dispose(); // Beendet die GUI
                    //break;
                //case "you_win": // Spiel ist zu ende
                //case "you_lose": // Spiel ist zu ende
                case "invalid_move":
                    System.out.println("opponentAwnser: 'invalid_move'");
                    return;
                case "game_does_not_exist":
                    System.out.println("game_does_not_exist");
                    textfield.setText("game_does_not_exist - maybe timeout");
                    return;
                default:
                    int x=Character.getNumericValue(opponentAwnser.charAt(0));
                    int y=Character.getNumericValue(opponentAwnser.charAt(2));
                   // System.out.println(Character.getNumericValue(opponentAwnser.charAt(0))+" hihi"+Character.getNumericValue(opponentAwnser.charAt(2))+"  " + opponentAwnser);
                    playerMove(x,y,!isFirstPlayer);
            }
            setEnabledAllButtons(true);
            // hier muss dann auf die antwort des des gegners gewartet werden
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void firstPlayer (String firstPlayer ){
        if(firstPlayer.equals("your_move")){
            isFirstPlayer =true;
            textfield.setText(name + "'s turn");
        } else if(firstPlayer.equals("opponent_move")){
            isFirstPlayer =false;
            textfield.setText(opponent_name + "'s turn");
        } else {
            System.out.println( "no_opponent_found for Gui");
        }
    }

    private void setEnabledAllButtons(boolean bool) {
        for(int i= 0; i < 3;i++){
            for(int j=0; j < 3; j++){
                buttons[i][j].setEnabled(bool);
            }
        }

    }


    private void creatGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350,400);
        frame.getContentPane().setBackground(new Color(50,50,50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(new Color(25,25,25));
        textfield.setForeground(new Color(255,192,203));
        textfield.setFont(new Font("Arial",Font.BOLD,15));
        textfield.setHorizontalAlignment(JLabel.CENTER);
        textfield.setText("Tic Tac Toe");
        textfield.setOpaque(true);

        title_panel.setLayout(new BorderLayout());
        title_panel.setBounds(0,0,800,100);

        button_panel.setLayout((new GridLayout(3,3)));
        button_panel.setBackground(new Color(150,150,150));


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                button_panel.add(buttons[i][j]);
                buttons[i][j].setFont(new Font("MV Boli", Font.BOLD, 50));
                buttons[i][j].setFocusable(false);
                buttons[i][j].addActionListener(this);
            }
        }

        title_panel.add(textfield);
        frame.add(title_panel, BorderLayout.NORTH);

        frame.add(button_panel);
    }
}