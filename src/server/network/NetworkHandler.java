package server.network;

import res.Out;
import server.data.Computer;
import server.resources.ComputerSubscriber;
import server.resources.NetworkCommandSubscriber;
import server.resources.NetworkStatusSubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The top level networking part.
 * I'm only doing a single seat server, so this'll be easier than say a chat client.
 */
public class NetworkHandler extends Thread implements ComputerSubscriber {

    private boolean isUp = false;
    private int port = 25566;
    private ServerSocket ss;

    private List< NetworkStatusSubscriber > statSubs;
    private List< NetworkCommandSubscriber > commSubs;
    private List< ConnectionHandler > clients;

    private String classId = "NetworkHandler";
    private static NetworkHandler instance;

    public static NetworkHandler getInstance() {
        if ( instance == null )
            instance = new NetworkHandler();
        return instance;
    }

    private NetworkHandler() {
        statSubs = new ArrayList<>();
        commSubs = new ArrayList<>();
        clients = new ArrayList<>();
    }

    public void setPort( int newPort ) {
        this.port = newPort;
        if ( isUp )
            // Soon this should send out a message to all clients, but oh well.
            establishServer();
    }

    /**
     * Establishes the server socket on a designated port
     */
    private void establishServer() {
        try {
            if ( isUp )
                ss.close();
            ss = new ServerSocket( this.port );
            alertStatSubscribers( "Ready on port " + this.port );
            Out.printInfo( classId, "Server ready on port " + ss.getLocalPort() );
            start();
            isUp = true;
        } catch ( Exception e ) {
            Out.printError( classId, "Could not bind to port: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    /**
     * Inherited from Thread
     */
    public void run() {
        Out.printInfo( classId, "Running" );
        while ( isUp ) {
            Socket client;
            try {
                client = ss.accept();
                ConnectionHandler con = new ConnectionHandler( client );
                con.start();
                clients.add( con );
                Out.printInfo( classId, "Accepted" );
            } catch ( Exception e ) {
                Out.printError( classId, "Error accepting client: " + e.getMessage() );
            }
        }
    }

    public void activate() {
        establishServer();
    }

    /**
     * Shuts down the server.
     */
    public void deactivate() {
        try {
            ss.close();
        } catch ( IOException ioe ) {
            Out.printError( classId, "Error closing server: " + ioe.getMessage() );
        }
        isUp = false;
        alertStatSubscribers( "Server Down" );
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


    public void subscribeToCommands( NetworkCommandSubscriber sub ){
        commSubs.add( sub );
    }
    public void alertCommSubscribers( String mes ){
        for ( NetworkCommandSubscriber ncs: commSubs ){
            ncs.sendCommand( mes );
        }
    }

    /**
     * Allow NetworkSubscribers to get ono the list
     *
     * @param sub The object wanting updates
     */
    public void subscribeToStats( NetworkStatusSubscriber sub ) {
        statSubs.add( sub );
    }

    private void alertStatSubscribers( String mes ) {
        for ( NetworkStatusSubscriber nSub : statSubs ) {
            nSub.updateStatus( mes );
        }
    }

    // Update the connection's IP address and retry connection
    @Override
    public void updateComputer( Computer data ) {

    }
}
