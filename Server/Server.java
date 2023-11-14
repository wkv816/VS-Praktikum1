package Server;

import Regestry.TicTacToeAService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;

public class Server{

    /**
     * ipaddress is used if the host and client are not in the same network.
     */
    private static final String ipaddress = "192.168.xxx.xxx"; //TODO IP-Adresse vom anderen Rechner
    private static final int port = 1099;

    public static void main(String[] args) {


        //System.setProperty("java.rmi.server.hostname",ipaddress);

        try{
            System.out.println("server started");

            // Set hostname for the server using java Property.
            //System.setProperty("java.rmi.server.hostname", ipaddress);

            TicTacToeAImpl ticTacToeAImpl_obj= new TicTacToeAImpl();
            TicTacToeAService stubTicTacToeA= (TicTacToeAService) UnicastRemoteObject.exportObject(ticTacToeAImpl_obj,0);
            Registry registry= LocateRegistry.createRegistry(port);
            registry.rebind("bindedstub",stubTicTacToeA);
            System.out.println("server running");
        }
        catch (Exception e){
            System.out.println("Some server error: " + e);
        }
    }

    
}
