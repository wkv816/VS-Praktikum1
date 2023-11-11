package Client;

import Regestry.TicTacToeAService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

public class GUITicTacToe implements ActionListener {

    private TicTacToeAService tttAService;
    private String gameID;
    private String playerName;
    private String opponentName;
    private String lastMove;

    private String moves;
    private String winner;
    private boolean myturn;
    private String currentPlayerTurn;
    boolean amIPlayerA;

    private JFrame frame = new JFrame();
    private JPanel titlePanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JLabel statusLabel = new JLabel();
    private JButton[][] buttons = new JButton[3][3];

    ArrayList<String> fullupdateArrayList = new ArrayList<>();

    public GUITicTacToe(TicTacToeAService ticTacToeAService, String playerName, String gameID) {
        this.tttAService = ticTacToeAService;
        this.playerName = playerName;
        this.gameID = gameID;
        this.moves = "";
        this.opponentName = "";
        this.myturn = false;
        this.lastMove = "";

        createGUI();
        Thread t = new Thread(()-> {
            while (true) {
                try {
                    fullupdateArrayList = tttAService.fullUpdate(gameID);
                    clientfullupdate();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        //handleFirstMove();
        //handleLastMove();

        frame.setVisible(true);
    }

    private void createGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(new BorderLayout());

        statusLabel.setBackground(new Color(25, 25, 25));
        statusLabel.setForeground(new Color(255, 192, 203));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setText("Tic Tac Toe");
        statusLabel.setOpaque(true);

        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBounds(0, 0, 800, 100);

        buttonPanel.setLayout(new GridLayout(3, 3));
        buttonPanel.setBackground(new Color(150, 150, 150));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttonPanel.add(buttons[i][j]);
                buttons[i][j].setFont(new Font("MV Boli", Font.BOLD, 40));
                buttons[i][j].setFocusable(false);
                buttons[i][j].addActionListener(this);
            }
        }

        titlePanel.add(statusLabel);
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
    }

    private void handleFirstMove() {
        if (myturn) {
            statusLabel.setText(playerName + "'s turn");
        } else {
            statusLabel.setText(opponentName + "'s turn");
            setAllButtonsEnabled(false);
        }
    }

    private void handleLastMove() {
        if (!lastMove.isEmpty()) {
            int x = Character.getNumericValue(lastMove.charAt(0));
            int y = Character.getNumericValue(lastMove.charAt(2));
            playerMove(x, y, !myturn);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (e.getSource() == buttons[i][j] && buttons[i][j].getText().isEmpty()) {
                    handlePlayerMove(i, j);
                }
            }
        }
    }

    private void handlePlayerMove(int i, int j) {
        setAllButtonsEnabled(false);
        new Thread(() -> makeMoveAndUpdateUI(i, j)).start();
    }

    private void makeMoveAndUpdateUI(int i, int j) {
        
        System.out.println(i+" "+j);
        try {
            String response = tttAService.makeMove(i, j, gameID);

            switch (response) {
                case "opponent_gone":
                    handleGameEnd("Opponent has left the game.");
                    break;
                case "you_win":
                    //handleGameEnd("Congratulations! You win!");
                    break;
                case "you_lose":
                    handleGameEnd("Sorry, you lose. Try again!");
                    break;
                case "invalid_move":
                    handleInvalidMove();
                    break;
                default:
                    //int x = Character.getNumericValue(response.charAt(0));
                    //int y = Character.getNumericValue(response.charAt(2));
                    //SwingUtilities.invokeLater(() -> playerMove(x, y, myturn));
                    break;
            }
        } catch (RemoteException ex) {
            handleGameEnd("Error communicating with the server.");
        } finally {
            setAllButtonsEnabled(false);
        }
    }

    private void handleInvalidMove() {
        JOptionPane.showMessageDialog(frame, "Invalid move. Try again.", "Invalid Move", JOptionPane.WARNING_MESSAGE);
        setAllButtonsEnabled(true);
    }

    private void handleGameEnd(String message) {
        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void playerMove(int i, int j, boolean player_A) {
        JButton button = buttons[i][j];

        if(amIPlayerA && myturn){
            button.setForeground(new Color(255, 0, 0));
            button.setText("X");
        }
        else if(amIPlayerA && !myturn){
            button.setForeground(new Color(0, 0, 255));
            button.setText("O");
        }
        else if(!amIPlayerA && myturn){
            button.setForeground(new Color(0, 0, 255));
            button.setText("O");
        }else if(!amIPlayerA && !myturn){
            button.setForeground(new Color(255, 0, 0));
            button.setText("X");
        }
        //button.setForeground(player_A ? new Color(255, 0, 0) : new Color(0, 0, 255));
        //button.setText(player_A ? "X" : "O");
        //statusLabel.setText((player_A ? opponentName : playerName) + "'s turn");
    }

    private void setAllButtonsEnabled(boolean isEnabled) {
        for (JButton[] row : buttons) {
            for (JButton button : row) {
                button.setEnabled(isEnabled);
            }
        }
    }

    private void clientfullupdate() {


        this.winner= fullupdateArrayList.get(6);



        String player_A= fullupdateArrayList.get(4);
        String player_B= fullupdateArrayList.get(5);
        String gameStatus = fullupdateArrayList.get(0);

        //System.out.println("player_A: "+player_A+" player_B: "+player_B+" playerName: "+playerName);
        this.opponentName = (playerName.equals(player_A)) ? player_B : player_A;
        this.winner = fullupdateArrayList.get(6);

        this.currentPlayerTurn = fullupdateArrayList.get(7);
        this.myturn = currentPlayerTurn.equals(playerName)? true: false;

        boolean isMoveChanged= !this.moves.equals(fullupdateArrayList.get(3));
        if (isMoveChanged) {
            moves=fullupdateArrayList.get(3);
            String opponentmovedetected =moves.substring(moves.length() - 3);
            int x = Character.getNumericValue(opponentmovedetected.charAt(0));
            int y = Character.getNumericValue(opponentmovedetected.charAt(2));
            amIPlayerA= this.playerName.equals(player_A);
            playerMove(x, y, amIPlayerA);
        }

        if(winner.isEmpty()){

            statusLabel.setText("PN="+ playerName + " "+"ON= "+ opponentName +
                    " Turn"+ (myturn ? playerName : opponentName)+ " Status= "+ gameStatus);
            //System.out.println("myturn: "+myturn);

            if (myturn) {
                setAllButtonsEnabled(true);
            }else {
                setAllButtonsEnabled(false);
            }
        } else{

            if(!myturn){
                statusLabel.setText("You won the game. "+ winner);
                setAllButtonsEnabled(false);


            }else{
                //Loser
                String loser= (winner == player_A)? player_B: player_A;
                statusLabel.setText("You lost the game. " + loser);
                setAllButtonsEnabled(false);
            }
        }

        // enable / disable buttons on myturn
    }
    
}