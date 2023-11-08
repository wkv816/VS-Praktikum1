package Client;

import Regestry.TicTacToeAService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

public class ClientPlayer {

    private String name;

    private static final int port = 1099;

    public ClientPlayer(String name) {
        this.name = name;
        startplayer();
    }

    private void startplayer() {

        try {
            System.out.println("client started");

            // Locate the registry with the server's IP address
            // Get the reference of the exported object from the RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", port);
            TicTacToeAService ticTacToeAService = (TicTacToeAService) registry.lookup("bindedstub");


            // invoking the methode findGame just for test purpose
            //System.out.println(ticTacToeAService.findGame("client_2"));
            //System.out.println(ticTacToeAService.test("2"));
            //System.out.println(ticTacToeAService.test("1"));
            Map<String, String> gameMap = ticTacToeAService.findGame(name);
            //System.out.println(gameMap);
            String gameID = gameMap.get("Game ID");
            String opponentName = gameMap.get("Opponent Name");
            String firstMove = gameMap.get("First Move");       // ["your_move", "opponent_move", "no_opponent_found"]
            String move = gameMap.get("Move");


            // wait for conaction to the server
            // if Server Connention succsesful -> start TTT Game
            GuiGame ticTacToe = new GuiGame(ticTacToeAService,name, gameID, opponentName, firstMove, move);
            System.out.println("client ended");

        } catch (Exception e) {
            System.out.println("client side error: " + e);
        }
    }
}
