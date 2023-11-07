package Client;

import Regestry.TicTacToeAService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    // ipaddress is used if the host and client are not in the same network.
    
    private static final String ipaddress = "31.16.156.35";
    private static final int port = 1099;

    public static void main(String[] args) {

        //TicTacToeGame ticTacToe = new TicTacToeGame();

        try {
            System.out.println("client started");

            // Locate the registry with the server's IP address
            // Get the reference of the exported object from the RMI registry

            Registry registry = LocateRegistry.getRegistry("localhost", port);
            TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");


            // invoking the methode findGame just for test purpose
            System.out.println(ticTacToeAService.findGame("client_1"));
            System.out.println(ticTacToeAService.test("1"));


            System.out.println("client ended");

        } catch (Exception e) {
            System.out.println("client side error: " + e);
        }
    }
    
}