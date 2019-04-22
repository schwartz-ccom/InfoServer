package server.network;

import res.Out;
import server.data.DataHandler;
import server.resources.NetworkCommandSubscriber;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public class ConnectionHandler extends Thread implements NetworkCommandSubscriber {

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ImageInputStream is;

    private String[] messageList = new String[ 256 ];

    private boolean keepConnectionAlive = true;

    private String classId = this.getClass().getSimpleName();

    ConnectionHandler( Socket c ) {
        NetworkHandler.getInstance().subscribeToCommands( this );
        client = c;
        Arrays.fill( messageList, "" );
        try {
            in = new ObjectInputStream( c.getInputStream() );
            out = new ObjectOutputStream( c.getOutputStream() );
            is = ImageIO.createImageInputStream( c.getInputStream() );

        } catch ( Exception e ) {
            Out.printError( classId, "Could not open streams: " + e.getMessage() );
        }
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

    @Override
    public void sendCommand( String mes ) {
        for ( int place = 0; place < 256; place++ ){
            if ( messageList[ place ].equals( "" ) ){
                messageList[ place ] = mes;
                return;
            }
        }
    }
}
