package Client;

import Regestry.TicTacToeAService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

public class Client2 {

    // ipaddress is used if the host and client are not in the same network.
     
    private static final String ipaddress = "31.16.156.35";
    private static final int port = 1099;

    public static void main(String[] args) {

        ClientPlayer client2 = new ClientPlayer("Tommy2");
    }

}
