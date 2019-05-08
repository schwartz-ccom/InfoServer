package server.network;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.network.info.Message;
import server.resources.ComputerSubscriber;
import server.resources.NetworkStatusSubscriber;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * The top level networking part.
 * I'm only doing a single seat server, so this'll be easier than say a chat client.
 */
public class NetworkHandler extends Thread implements ComputerSubscriber {

    private int port = 25566;

    private List< NetworkStatusSubscriber > statSubs;

    private String ipToConnect = "";

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    final private Queue< Message > messageList = new LinkedList<>();

    private boolean amAlive = false;

    private String classId = "NetworkHandler";
    private static NetworkHandler instance;

    public static NetworkHandler getInstance() {
        if ( instance == null )
            instance = new NetworkHandler();
        return instance;
    }

    private NetworkHandler() {
        statSubs = new ArrayList<>();
    }

    public void setPort( int newPort ) {
        this.port = newPort;
    }

    /**
     * Tell this client to connect to the other servers
     */
    public void setTarget( String ip ) {
        ipToConnect = ip;
    }

    /**
     * Run. Overloaded from Thread
     * It initially asks for details and a screenshot first, but then you can
     * ask it anything else afterwards
     */
    public void run() {

        boolean keepConnectionAlive = true;

        if ( ipToConnect.equalsIgnoreCase( "" ) ) {
            JOptionPane.showMessageDialog( null, "There is no target" );
            return;
        }

        String compName = DataHandler.getInstance().getCurrentComputer().getComputerName();

        try {
            Out.printInfo( classId, "Connecting..." );

            // Create the socket and then connect to the ipToConnect, specified when
            // a new computer is clicked.
            client = new Socket();
            client.connect( new InetSocketAddress( ipToConnect, port ), 2000 );
            client.setSoTimeout( 2000 );

            // Create the object streams
            in = new ObjectInputStream( client.getInputStream() );
            out = new ObjectOutputStream( client.getOutputStream() );

            Out.printInfo( classId, "Connected." );
        } catch ( IOException ioe ) {
            Out.printInfo( classId, "Could not connect." );
            alertStatSubscribers( compName + " is not available!" );
            return;
        }
        alertStatSubscribers( compName + " is connected!" );
        try {
            amAlive = true;
            Out.printInfo( classId, "Sending initial request..." );

            messageList.add( new Message( "DETAILS" ) );

            // Wait on the user to send any commands they want
            while ( keepConnectionAlive ) {
                synchronized ( messageList ) {
                    // If the messageList is empty, wait on the list to be filled
                    // Doing this reduces CPU cycles, as opposed to a while loop.
                    if ( messageList.isEmpty() ) {
                        Out.printInfo( classId, "Waiting for next message..." );
                        messageList.wait();
                    }
                }
                Message mes = messageList.remove();
                Out.printInfo( classId, "Message to send: " + mes );
                if ( mes.getPrimaryCommand().equalsIgnoreCase( "GOODBYE" ) ){
                    write( mes );
                    disconnect();
                    keepConnectionAlive = false;
                }
                else if ( mes.getPrimaryCommand().equalsIgnoreCase( "RUN MACRO" ) ){

                }
                else if ( mes.getPrimaryCommand().equalsIgnoreCase( "RUN" ) ){
                    write( mes );
                }
                else if ( mes.getPrimaryCommand().equalsIgnoreCase( "DETAILS" ) ){
                    write( mes );
                    Object read = null;
                    try {
                        read = in.readObject();
                    } catch ( StreamCorruptedException sce ){
                        Out.printError( classId, "Broken Stream" );
                    }
                    if ( read instanceof Message ) {
                        DataHandler.getInstance().getCurrentComputer().setDetails( ( ( Message ) read ).getInfo() );
                        DataHandler.getInstance().getCurrentComputer().setImage( ( ( Message ) read ).getImg() );
                        DataHandler.getInstance().alertSubscribers();
                        Out.printInfo( classId, "Info from client received!" );
                    }
                    else {
                        Out.printError( classId, "Unexpected type from client" );
                    }
                }
            }
        } catch ( Exception e ) {
            Out.printError( classId, "Error: " + e.getMessage() );
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private void write( Message m ) {
        try {
            out.writeObject( m );
            out.flush();
        } catch ( IOException ioe ){
            Out.printError( classId, "Could not send message: " + ioe.getMessage() );
        }
    }
    private void disconnect(){
        try {
            out.close();
        } catch ( Exception e ){
            Out.printError( classId, "Couldn't close out. Already closed?" );
        }
        try {
            in.close();
        } catch ( Exception e ){
            Out.printError( classId, "Couldn't close in. Already closed?" );
        }
        try {
            client.close();
        } catch ( Exception e ){
            Out.printError( classId, "Couldn't close client socket. Already closed?" );
        }
        alertStatSubscribers( "NO ACTIVITY" );
        amAlive = false;
        Out.printInfo( classId, "Done!" );
    }

    public boolean isConnectedToComputer(){
        return amAlive;
    }
    public void sendCommand( Message mes ) {
        // Get the lock for message list and notify anything waiting on it
        synchronized ( messageList ) {
            messageList.add( mes );
            messageList.notify();
        }
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
