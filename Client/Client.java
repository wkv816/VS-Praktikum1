package Client;

import Regestry.TicTacToeAService;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        ClientPlayer client1 = new ClientPlayer("Max1");
    }

}