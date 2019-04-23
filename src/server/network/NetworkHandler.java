package server.network;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.resources.ComputerSubscriber;
import server.resources.NetworkStatusSubscriber;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The top level networking part.
 * I'm only doing a single seat server, so this'll be easier than say a chat client.
 */
public class NetworkHandler extends Thread implements ComputerSubscriber {

    private boolean isUp = false;
    private int port = 25566;

    private List< NetworkStatusSubscriber > statSubs;

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ImageInputStream is;

    private String[] messageList = new String[ 256 ];

    private boolean keepConnectionAlive = true;

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
     * Establishes the server socket on a designated port
     */
    private void establishConnection() {

    }

    /**
     * Run. Overloaded from Thread
     * It initially asks for details and a screenshot first, but then you can
     * ask it anything else afterwards
     */
    public void run() {

        try {
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

            while ( keepConnectionAlive ){
                while ( messageList.length == 0);
                String mes =  messageList[ 0 ];
                Out.printInfo( classId, "Message to send: " + mes );

            }

            Out.printInfo( classId, "Info from client received!" );

        } catch ( Exception e ) {
            Out.printError( classId, "Error: " + e.getMessage() );
        } finally {
            try {
                out.close();
                in.close();
                client.close();
            } catch ( IOException ioe ) {
                Out.printError( classId, "Error closing streams: " + ioe.getMessage() );
            }
        }
    }

    private void write( String message ) throws IOException {
        out.writeObject( message );
        out.flush();
    }

    public void sendCommand( String mes ) {
        for ( int place = 0; place < 256; place++ ){
            if ( messageList[ place ].equals( "" ) ){
                messageList[ place ] = mes;
                return;
            }
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
