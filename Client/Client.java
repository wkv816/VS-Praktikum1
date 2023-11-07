package Client;

import Regestry.TicTacToeAService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Client {

    /**
     * ipaddress is used if the host and client are not in the same network.
     */
    private static final String ipaddress = "31.16.156.35";
    private static final int port = 1099;

    public static void main(String[] args) {

        //TicTacToeGame ticTacToe = new TicTacToeGame();
        Boolean scannerOn = false;
        String opponent_name;

        try {
            // Client start
            // Enter Player Name
            String clientNumbere;
            Scanner idScanner = new Scanner(System.in);  // Create a Scanner object
            do{
                System.out.println("Which Client are you? (Enter 1 oder 2)");
                clientNumbere = idScanner.nextLine();
            } while(!clientNumbere.equals("1") && !clientNumbere.equals("2"));


            if(clientNumbere.equals("1")){
                String name = "Max";
                if(scannerOn){
                    Scanner scanner = new Scanner(System.in);  // Create a Scanner object
                    System.out.println("Enter Client 1 Name");
                    name = scanner.nextLine();
                }
                startClient1(name);

            }else if (clientNumbere.equals("2")){
                String name = "Otto";
                if(scannerOn){
                    Scanner scanner = new Scanner(System.in);  // Create a Scanner object
                    System.out.println("Enter Client 2 Name");
                    name = scanner.nextLine();
                }
                startClient2(name);

            } else {
                System.out.println("Fehler Clientnummer konnte nicht zugeordnent werden");
            }

        } catch (Exception e) {
            System.out.println("client side error: " + e);
        }
    }




    private static void startClient1(String name) throws RemoteException, NotBoundException {


        System.out.println("client started");
        // Locate the registry with the server's IP address
        // Get the reference of the exported object from the RMI registry
        Registry registry = LocateRegistry.getRegistry("localhost", port);
        TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");
        //TicTacToeAService ticTacToeAService=null;
        GuiGame tmpTest = new GuiGame(ticTacToeAService, "gameID", "opponentName", "firstMove", "move");
            System.out.println("client started");

            // Locate the registry with the server's IP address
            // Get the reference of the exported object from the RMI registry

            //Registry registry = LocateRegistry.getRegistry("localhost", port);
            //TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");


            // invoking the methode findGame just for test purpose
            System.out.println(ticTacToeAService.findGame("client_1"));
            //System.out.println(ticTacToeAService.test("1"));
        // invoking the methode findGame just for test purpose
        Map<String, String> gameMap = ticTacToeAService.findGame(name);
        String gameID = gameMap.get("Game ID");
        String opponentName = gameMap.get("Opponent Name");
        String firstMove = gameMap.get("First Move");       // ["your_move", "opponent_move", "no_opponent_found"]
        String move = gameMap.get("Move");


        // wait for conaction to the server
        // if Server Connention succsesful -> start TTT Game
        GuiGame ticTacToe = new GuiGame(ticTacToeAService, gameID, opponentName, firstMove, move);
        System.out.println("client ended");

    }

    private static void startClient2(String name) throws RemoteException, NotBoundException {

        System.out.println("client started");
        // Locate the registry with the server's IP address
        // Get the reference of the exported object from the RMI registry
        Registry registry = LocateRegistry.getRegistry("localhost", port);
        TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");

        // invoking the methode findGame just for test purpose
        Map<String, String> gameMap = ticTacToeAService.findGame(name);
        String gameID = gameMap.get("Game ID");
        String opponentName = gameMap.get("Opponent Name");
        String firstMove = gameMap.get("First Move");       // ["your_move", "opponent_move", "no_opponent_found"]
        String move = gameMap.get("Move");


        // wait for conaction to the server
        // if Server Connention succsesful -> start TTT Game
        GuiGame ticTacToe = new GuiGame(ticTacToeAService, gameID, opponentName, firstMove, move);

        System.out.println("client ended");
    }
}