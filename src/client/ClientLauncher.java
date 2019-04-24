package client;

import client.network.ConnectionHandler;
import res.Out;

public class ClientLauncher {

    public ClientLauncher() {
        Out.printInfo( getClass().getSimpleName(), "Starting relay...");
        new ConnectionHandler();
    }
    public static void main( String [] args ){
        new ClientLauncher();
    }
}
