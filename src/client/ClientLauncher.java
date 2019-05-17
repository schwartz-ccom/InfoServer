package client;

import client.network.ConnectionHandler;
import res.Out;

public class ClientLauncher {

    private ClientLauncher() {
        new ClientLauncher( 25566 );
    }
    
    private ClientLauncher( int port ){
        Out.printInfo( getClass().getSimpleName(), "Starting relay...");
        new ConnectionHandler( port );
    }
    
    public static void main( String [] args ){
        // If the user has specified a port to run this on
        if ( args.length == 2 )
           new ClientLauncher( Integer.valueOf( args[ 1 ] ) );
        new ClientLauncher();
    }
}
