package server.network;

import res.Out;
import server.data.DataHandler;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionHandler extends Thread {

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ImageInputStream is;

    private String classId = this.getClass().getSimpleName();

    ConnectionHandler( Socket c ) {
        client = c;

        try {
            in = new ObjectInputStream( c.getInputStream() );
            out = new ObjectOutputStream( c.getOutputStream() );
            is = ImageIO.createImageInputStream( c.getInputStream() );

        } catch ( Exception e ) {
            Out.printError( classId, "Could not open streams: " + e.getMessage() );
        }
    }

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
}
