package Client;
import Regestry.TicTacToeAService;

import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.*;
public class GuiGame implements ActionListener{


    private TicTacToeAService tttAService;
    private String gameID;
    private String name;
    private String opponent_name = "Otto";
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


    public GuiGame(TicTacToeAService ticTacToeAService,String name, String gameID, String opponentName, String firstMove, String move){

        tttAService = ticTacToeAService;
        this.name = name;
        this.gameID = gameID;
        this.opponent_name = opponentName;
        this.firstMove = firstMove;
        this.move = move;

        creatGUI();

        firstPlayer(firstMove);

        if(!move.equals("")) {
            int x = Integer.parseInt(move.substring(0,1));
            int y = Integer.parseInt(move.substring(2,3));
            System.out.println(" x: " + x+ " und y: " +y);
            playerMove(x,y,!isFirstPlayer);
            //buttons[x][y].doClick();
        }

        //buttons[2][2].doClick();

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (e.getSource() == buttons[i][j]) {
                    if (buttons[i][j].getText().equals("")) {
                        playerMove(i,j, isFirstPlayer);
                        getCordinate(i, j);
                    }
                }
            }
        }
    }


    private void playerMove(final int i, final int j, final boolean bool) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateUI(i, j, bool);
            }
        });

        // Hier lock.wait() aufrufen oder die entfernte Methode aufrufen
        // ...
    }

    private void updateUI(int i, int j, boolean bool) {
        if (bool) {
            buttons[i][j].setForeground(new Color(255, 0, 0));
            buttons[i][j].setText("X");
            textfield.setText((opponent_name + "'s turn"));
        } else {
            buttons[i][j].setForeground(new Color(0, 0, 255));
            buttons[i][j].setText("O");
            textfield.setText((name + "'s turn"));
        }
    }

    /*
    private void playerMove(int i, int j,boolean bool){

        if (bool) {
            buttons[i][j].setForeground(new Color(255, 0, 0));
            buttons[i][j].setText("X");
            textfield.setText((opponent_name + "'s turn"));
        } else {
            buttons[i][j].setForeground(new Color(0, 0, 255));
            buttons[i][j].setText("O");
            textfield.setText((name + "'s turn"));
        }
    }*/

    private void getCordinate(int i, int j) {
        try {
            String opponentAwnser;
            opponentAwnser = tttAService.makeMove(i, j,gameID);


            switch (opponentAwnser){
                case "opponent_gone": // Spiel ist zu ende
                    System.out.println("opponentAwnser: 'opponent_gone'");
                    break;
                case "you_win": // Spiel ist zu ende
                case "you_lose": // Spiel ist zu ende
                case "invalid_move":
                    System.out.println("opponentAwnser: 'invalid_move'");
                default:
                    // "x,y;
                    int x = Integer.parseInt(opponentAwnser.substring(0,1));
                    int y = Integer.parseInt(opponentAwnser.substring(2,3));
                    System.out.println("Coordinaten Button[" + x + "][" + y + "] von Opponent bekommen");
                    playerMove(x,y,!isFirstPlayer);
            }
            //setEnabledAllButtons(true);
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
        frame.setSize(800,800);
        frame.getContentPane().setBackground(new Color(50,50,50));
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);

        textfield.setBackground(new Color(25,25,25));
        textfield.setForeground(new Color(255,192,203));
        textfield.setFont(new Font("Arial",Font.BOLD,75));
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
                buttons[i][j].setFont(new Font("MV Boli", Font.BOLD, 120));
                buttons[i][j].setFocusable(false);
                buttons[i][j].addActionListener(this);
            }
        }

        title_panel.add(textfield);
        frame.add(title_panel, BorderLayout.NORTH);

        frame.add(button_panel);
    }
}