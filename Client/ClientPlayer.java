package Client;

import Regestry.TicTacToeAService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

public class ClientPlayer {

    private String name;

    private static final int port = 1099;

    private String serverIP = "localhost";
    //private String serverIP = "192.168.18.135";
    //private String serverIP = "141.22.27.111";
    public ClientPlayer(String name) {
        this.name = name;
        startplayer();
    }
// 192.168.0.100
    private void startplayer() {

        try {
            System.out.println("client started");

            // Locate the registry with the server's IP address
            // Get the reference of the exported object from the RMI registry
            Registry registry = LocateRegistry.getRegistry(serverIP, port);
            TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");


            Map<String, String> gameMap = ticTacToeAService.findGame(name);
            //System.out.println(gameMap);
            String gameID = gameMap.get("Game ID");
            String opponentName = gameMap.get("Opponent Name");
            String firstMove = gameMap.get("First Move");       // ["your_move", "opponent_move", "no_opponent_found"]
            String move = gameMap.get("Move");


            // wait for conaction to the server
            // if Server Connention succsesful -> start TTT Game
            GUITicTacToe ticTacToe = new GUITicTacToe(ticTacToeAService, name, gameID);
            System.out.println("client ended");

        } catch (Exception e) {
            System.out.println("client side error: " + e);
        }
    }
}