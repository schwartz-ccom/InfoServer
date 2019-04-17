package client;

import client.network.ConnectionHandler;

public class ClientLauncher {

    public ClientLauncher() {
        new ConnectionHandler();
    }
    public static void main( String [] args ){
        new ClientLauncher();
    }
}
