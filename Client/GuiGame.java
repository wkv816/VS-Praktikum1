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
    private boolean player1_turn;

    public GuiGame(TicTacToeAService ticTacToeAService,String name, String gameID, String opponentName, String firstMove, String move){

        tttAService = ticTacToeAService;
        this.name = name;
        this.gameID = gameID;
        this.opponent_name = opponentName;
        this.firstMove = firstMove;
        this.move = move;

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

        firstPlayer();
        //buttons[2][2].doClick();

    }


    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (e.getSource() == buttons[i][j]) {
                    if (player1_turn) {
                        if (buttons[i][j].getText().equals("")) {
                            buttons[i][j].setForeground(new Color(255, 0, 0));
                            buttons[i][j].setText("X");
                            player1_turn = false;
                            textfield.setText((opponent_name + "'s turn"));
                            getCordinate(i, j);
                            //check();
                        }
                    } else {
                        if (buttons[i][j].getText().equals("")) {
                            buttons[i][j].setForeground(new Color(0, 0, 255));
                            buttons[i][j].setText("O");
                            player1_turn = true;
                            textfield.setText((name + "'s turn"));
                            getCordinate(i, j);
                            //check();
                        }
                    }
                }
            }
        }
    }

    private void getCordinate(int i, int j) {
        String x = String.valueOf(i);
        String y = String.valueOf(j);

        try {
            String opponentAwnser = "tmp";
            // tttAService.makeMove();
            tttAService.test2("1",x,y);

                    //makeMove(x,y, gameID);
            setEnabledAllButtons(false);


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
                    char xCordinate = opponentAwnser.charAt(0);
                    char yCordinate = opponentAwnser.charAt(2);
            }
            //buttons[2][2].doClick();
            setEnabledAllButtons(true);

            // hier muss dann auf die antwort des des gegners gewartet werden
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void setEnabledAllButtons(boolean bool) {
        for(int i= 0; i < 3;i++){
            for(int j=0; j < 3; j++){
                buttons[i][j].setEnabled(bool);
            }
        }

    }

    public void firstPlayer (){
        try {
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        if(random.nextInt(2)==0){
            player1_turn=true;
            textfield.setText(name + "'s turn");
        } else{
            player1_turn=false;
            textfield.setText(opponent_name + "'s turn");
        }
    }
/*
    public void check() {


        for(int i=0; i < 9;i=i+3){
            int a = 0 + i;
            int b = 1 + i;
            int c = 2 + i;
            if(     (buttons[a].getText()=="X")&&
                    (buttons[b].getText()=="X")&&
                    (buttons[c].getText()=="X")){
                xWins(a,b,c);
                return;
            }
        }

        for(int i=0; i < 3;i++){
            int a = 0 + i;
            int b = 3 + i;
            int c = 6 + i;
            if(     (buttons[a].getText()=="X")&&
                    (buttons[b].getText()=="X")&&
                    (buttons[c].getText()=="X")){
                xWins(a,b,c);
                return;
            }
        }

        if(     (buttons[0].getText()=="X")&&
                (buttons[4].getText()=="X")&&
                (buttons[8].getText()=="X")){
            xWins(0,4,8);
            return;
        }

        if(     (buttons[2].getText()=="X")&&
                (buttons[4].getText()=="X")&&
                (buttons[6].getText()=="X")){
            xWins(2,4,6);
            return;
        }

        for(int i=0; i < 9;i=i+3){
            int a = 0 + i;
            int b = 1 + i;
            int c = 2 + i;
            if(     (buttons[a].getText()=="O")&&
                    (buttons[b].getText()=="O")&&
                    (buttons[c].getText()=="O")){
                oWins(a,b,c);
                return;
            }
        }

        for(int i=0; i < 3;i++){
            int a = 0 + i;
            int b = 3 + i;
            int c = 6 + i;
            if(     (buttons[a].getText()=="O")&&
                    (buttons[b].getText()=="O")&&
                    (buttons[c].getText()=="O")){
                oWins(a,b,c);
                return;
            }
        }

        if(     (buttons[0].getText()=="O")&&
                (buttons[4].getText()=="O")&&
                (buttons[8].getText()=="O")){
            oWins(0,4,8);
            return;
        }

        if(     (buttons[2].getText()=="O")&&
                (buttons[4].getText()=="O")&&
                (buttons[6].getText()=="O")){
            oWins(2,4,6);
            return;
        }
    }

    public void xWins(int a, int b,int c){
        buttons[a].setBackground(Color.GREEN);
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);

        for(int i =0; i < 9; i++){
            buttons[i].setEnabled(false);
        }
        textfield.setText(name + " wins");
    }

    public void oWins(int a, int b,int c){
        buttons[a].setBackground(Color.GREEN);
        buttons[b].setBackground(Color.GREEN);
        buttons[c].setBackground(Color.GREEN);

        for(int i =0; i < 9; i++){
            buttons[i].setEnabled(false);
        }
        textfield.setText(opponent_name + " wins");

    }

 */

}