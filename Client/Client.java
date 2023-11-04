package Client;

import Regestry.TicTacToeAService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {



    /**
     * ipaddress is used if the host and client are not in the same network.
     */
    private static final String ipaddress = "31.16.156.35";
    private static final int port = 1099;

    public static void main(String[] args) {
        Boolean scannerOn = false;
        String name;
        String opponent_name;


        try {
            System.out.println("client started");
            // Client start
            // Enter Player Name
            if(scannerOn){
                Scanner scanner = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Enter Player 1 Name");
                name = scanner.nextLine();
            }

            // Locate the registry with the server's IP address
            // Get the reference of the exported object from the RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", port);
            TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");

            // invoking the methode findGame just for test purpose
            ticTacToeAService.findGame("");
            String gameID ="0";

            // wait for conaction to the server
            // if Server Connention succsesful -> start TTT Game
            TicTacToeGame ticTacToe = new TicTacToeGame(ticTacToeAService, gameID);

            System.out.println("client ended");

        } catch (Exception e) {
            System.out.println("client side error: " + e);
        }
    }
}