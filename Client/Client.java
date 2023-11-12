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
        ClientPlayer client1 = new ClientPlayer("Max1");
    }
}