package server.network;

import res.Out;
import server.data.DataHandler;
import server.network.info.Message;
import server.resources.NetworkStatusSubscriber;
import server.types.MessageQueue;
import server.ui.App;

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
public class NetworkHandler extends Thread {

    private int port = 25566;

    private List< NetworkStatusSubscriber > statSubs;

    private String ipToConnect = "";

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    final private MessageQueue messageList = new MessageQueue();

    private boolean amAlive = false;

    private String classId = this.getClass().getSimpleName();
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
     * Called by ConnectButton
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

        // Declare the boolean so we can access it throughout the loop.
        // Long in short, it's the while loop that checks the messages going and
        // coming from the client
        boolean keepConnectionAlive = true;

        // Get the computer name so we can tell the StatusBar what's up
        String compName = DataHandler.getInstance().getCurrentComputer().getComputerName();

        try {
            Out.printInfo( classId, "Attempting connection..." );

            // Create the socket and then connect to the ipToConnect, specified when
            // a new computer is clicked.
            client = new Socket();
            client.connect( new InetSocketAddress( ipToConnect, port ), 2000 );
            client.setSoTimeout( 2000 );

            // Create the object streams
            in = new ObjectInputStream( client.getInputStream() );
            out = new ObjectOutputStream( client.getOutputStream() );

            Out.printInfo( classId, "Succesfully connected." );
        } catch ( IOException ioe ) {
            Out.printInfo( classId, "Could not connect." );
            alertStatSubscribers( compName + " is not available! Is it running?" );
            return;
        }
        alertStatSubscribers( compName + " is connected!" );

        // Set the flag that other objects in the app can check, so they know if we are connected or not
        amAlive = true;

        // Queue up the first Details message.
        messageList.addMessage( new Message( "DETAILS" ) );

        // Wait on the user to send any commands they want
        while ( keepConnectionAlive ) {
            synchronized ( messageList ) {
                // If the messageList is empty, wait on the list to be filled
                // Doing this reduces CPU cycles, as opposed to a while loop.
                if ( messageList.getSize() == 0 ) {
                    try {
                        messageList.wait();
                    } catch ( InterruptedException ie ) {
                        Out.printError( classId, "Waiting for message was interrupted: " + ie.getMessage() );
                    }
                }
            }

            // Get the next Message to be sent from App.java
            Message mes = messageList.pop();

            // Then go through each special case and do what is necessary, or just send it
            if ( mes.getPrimaryCommand().equalsIgnoreCase( "DETAILS" ) ) {
                // Write the request
                write( mes );

                // Get the next object coming through the stream via helper method.
                Message read = getNextInStream();

                //Check the message if it is null, since that is unlikely, but possible
                if ( read != null ) {
                    DataHandler.getInstance().getCurrentComputer().setDetails( read.getInfo() );
                    DataHandler.getInstance().getCurrentComputer().setImage( read.getImg() );
                    App.getInstance().updateLoadedMacros( read.getSecondaryCommand() );
                    DataHandler.getInstance().alertSubscribers();
                }
            }
            else if ( mes.getPrimaryCommand().equalsIgnoreCase( "GET MACROS" ) ) {
                // Write out the message, since we need to handle input later.
                write( mes );

                // Get the next object coming through the stream via helper method.
                Message read = getNextInStream();

                // Check if it is null, since that is possible, but unlikely
                if ( read != null )
                    App.getInstance().updateLoadedMacros( read.getSecondaryCommand() );
            }
            else if ( mes.getPrimaryCommand().equalsIgnoreCase( "GOODBYE" ) ) {
                write( mes );
                disconnect();
                amAlive = false;
                keepConnectionAlive = false;
            }
            else
                write( mes );
        }
        disconnect();
    }

    /**
     * Helper method to get the upcoming item in the stream
     * Called when details are requested, or a list of macros.
     *
     * @return The Message being returned by client
     */
    private Message getNextInStream() {
        Object read = null;
        try {
            read = in.readObject();
        } catch ( StreamCorruptedException sce ) {
            Out.printError( classId, "Broken Stream: " + sce.getMessage() );
        } catch ( IOException ioe ) {
            Out.printError( classId, "IO Error reading obj: " + ioe.getMessage() );
        } catch ( ClassNotFoundException cnfe ) {
            Out.printError( classId, "Class not found: " + cnfe.getMessage() );
        }

        // Just to verify. Never hurts to have error detection
        if ( read instanceof Message ) {
            return ( Message ) read;
        }

        // Else if it isn't a message for some weird reason
        Out.printError( classId, "Unknown type from client" );
        return null;
    }

    private void write( Message m ) {
        try {
            out.writeObject( m );
            out.flush();
        } catch ( IOException ioe ) {
            Out.printError( classId, "Could not send message: " + ioe.getMessage() );
        }
    }

    private void disconnect() {
        try {
            out.close();
        } catch ( Exception e ) {
            Out.printError( classId, "Couldn't close out. Already closed?" );
        }
        try {
            in.close();
        } catch ( Exception e ) {
            Out.printError( classId, "Couldn't close in. Already closed?" );
        }
        try {
            client.close();
        } catch ( Exception e ) {
            Out.printError( classId, "Couldn't close client socket. Already closed?" );
        }
        alertStatSubscribers( "NO ACTIVITY" );
        amAlive = false;
        Out.printInfo( classId, "Done!" );
    }

    public boolean isConnectedToComputer() {
        return amAlive;
    }

    public void sendCommand( Message mes ) {
        // Get the lock for message list and notify anything waiting on it
        synchronized ( messageList ) {
            messageList.addMessage( mes );
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
     * Allow NetworkSubscribers to get onto the list
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
}
