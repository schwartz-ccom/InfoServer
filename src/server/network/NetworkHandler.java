package server.network;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.resources.ComputerSubscriber;
import server.resources.NetworkStatusSubscriber;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private ImageInputStream is;

    final private Queue< String > messageList = new LinkedList<>();

    private boolean keepConnectionAlive = true;
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
            is = ImageIO.createImageInputStream( client.getInputStream() );

            keepConnectionAlive = true;
            Out.printInfo( classId, "Connected." );
        } catch ( IOException ioe ) {
            Out.printInfo( classId, "Could not connect." );
            alertStatSubscribers( compName + " is not available!" );
            return;
        }
        alertStatSubscribers( compName + " is connected!" );
        try {
            amAlive = true;
            Out.printInfo( classId, "Sending request..." );

            write( "DETAILS" );
            Object received = in.readObject();

            HashMap< String, String > fromClient = null;
            if ( received instanceof HashMap )
                //noinspection unchecked
                fromClient = ( HashMap< String, String > ) received;
            else
                Out.printError( classId, "Unexpected type from client" );

            BufferedImage img = ImageIO.read( is );

            DataHandler.getInstance().getCurrentComputer().setDetails( fromClient );
            DataHandler.getInstance().getCurrentComputer().setImage( img );

            // Wait on the user to send any commands they want
            while ( keepConnectionAlive ) {
                Out.printInfo( classId, "Waiting for next message..." );
                synchronized ( messageList ) {
                    if ( messageList.isEmpty() )
                        messageList.wait();
                }
                String mes = messageList.remove();
                Out.printInfo( classId, "Message to send: " + mes );
                if ( mes.equals( "GOODBYE" ) ){
                    write( mes );
                    disconnect();
                    keepConnectionAlive = false;
                }
                else if ( mes.startsWith( "RUN MACRO" ) ){

                }
                else if ( mes.startsWith( "RUN" ) ){
                    write( mes );
                }
            }

            Out.printInfo( classId, "Info from client received!" );

        } catch ( Exception e ) {
            e.printStackTrace();
            Out.printError( classId, "Error: " + e.getMessage() );
        } finally {
            disconnect();
        }
    }

    private void write( String message ) {
        try {
            out.writeObject( message );
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
    public void sendCommand( String mes ) {
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
