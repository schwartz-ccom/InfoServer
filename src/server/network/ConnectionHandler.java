package server.network;

import res.Out;
import server.data.Computer;
import server.resources.ComputerSubscriber;
import server.resources.NetworkSubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * The top level networking part.
 * I'm only doing a single seat server, so this'll be easier than say a chat client.
 */
public class ConnectionHandler extends Thread implements ComputerSubscriber {

    private boolean isUp = false;
    private int port = 25566;
    private ServerSocket ss;

    private List< NetworkSubscriber > subs;

    private String classId = "ConnectionHandler";
    private static ConnectionHandler instance;

    public static ConnectionHandler getInstance() {
        if ( instance == null )
            instance = new ConnectionHandler();
        return instance;
    }

    private ConnectionHandler() {
        subs = new ArrayList<>();
    }

    /**
     * Establishes the server socket on a designated port
     *
     * @param newPort The port to listen on
     */
    public void establishServer( int newPort ) {
        try {
            if ( isUp )
                ss.close();
            this.port = newPort;
            ss = new ServerSocket( this.port );
            alertSubscribers( "Listening on port " + this.port );
            Out.printInfo( classId, "Server listening on port " + ss.getLocalPort() );
            isUp = true;
        } catch ( Exception e ) {
            Out.printError( classId, "Could not bind to port: " + e.getMessage() );
        }
    }

    public void activate(){
        establishServer( port );
    }
    /**
     * Shuts down the server.
     */
    public void deactivate(){
        try {
            ss.close();
        } catch( IOException ioe ){
            Out.printError( classId, "Error closing server: " + ioe.getMessage() );
        }
        isUp = false;
        alertSubscribers( "Server Down" );
        Out.printInfo( classId, "Server has gone down!" );
    }
    /**
     * Access method to get the currently bound port
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Allow NetworkSubscribers to get ono the list
     * @param sub The object wanting updates
     */
    public void subscribe( NetworkSubscriber sub ){
        subs.add( sub );
    }

    private void alertSubscribers( String mes ){
        for ( NetworkSubscriber nSub: subs ){
            nSub.updateStatus( mes );
        }
    }

    // Update the connection's IP address and retry connection
    @Override
    public void updateComputer( Computer data ) {

    }
}
