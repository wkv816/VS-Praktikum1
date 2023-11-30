package Client;


import java.util.Scanner;

public class Client {





    public static void main(String[] args) {
        Scanner scanner= new Scanner(System.in);
        System.out.println(scanner);
        //ClientPlayer client1 = new ClientPlayer("Max1");
        ClientPlayer client1 = new ClientPlayer(scanner.next());
    }

}