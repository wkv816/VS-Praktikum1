package Client;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
public class TicTacToeGame implements ActionListener{

    String name = "Max";
    String opponent_name = "Otto";

    Boolean scannerOn = true;

    private Random random =  new Random();
    JFrame frame = new JFrame();
    JPanel title_panel = new JPanel();
    JPanel button_panel = new JPanel();
    JLabel textfield = new JLabel();
    JButton[] buttons = new JButton[9];
    boolean player1_turn;

    public TicTacToeGame(){
        if(scannerOn){
            Scanner scanner = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter Player 1 Name");
            name = scanner.nextLine();

            System.out.println("Enter Player 2 Name");
            opponent_name = scanner.nextLine();
        }

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

        for(int i = 0; i < 9; i++){
            buttons[i] =new JButton();
            button_panel.add(buttons[i]);
            buttons[i].setFont(new Font("MV Boli", Font.BOLD,120));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(this);
        }
        title_panel.add(textfield);
        frame.add(title_panel, BorderLayout.NORTH);

        frame.add(button_panel);


        firstPlayer();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(int i =0; i < 9; i++){
            if(e.getSource()==buttons[i]){
                if(player1_turn){
                    if(buttons[i].getText().equals("")){
                        buttons[i].setForeground(new Color(255,0,0));
                        buttons[i].setText("X");
                        player1_turn = false;
                        textfield.setText((opponent_name + "'s turn"));
                        check();
                    }
                } else{
                    if(buttons[i].getText().equals("")){
                        buttons[i].setForeground(new Color(0,0,255));
                        buttons[i].setText("O");
                        player1_turn = true;
                        textfield.setText((name + "'s turn"));
                        check();
                    }

                }
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

}