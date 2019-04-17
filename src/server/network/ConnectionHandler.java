package server.network;

import res.Out;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String classId = this.getClass().getSimpleName();

    public ConnectionHandler( Socket c ) {
        client = c;

        try {
            in = new ObjectInputStream( c.getInputStream() );
            out = new ObjectOutputStream( c.getOutputStream() );
        } catch ( Exception e ) {
            Out.printError( classId, "Could not open streams: " + e.getMessage() );
        }
    }

    public void run() {
        try {
            write( "Hello! What's your name?" );
            System.out.println( in.readObject() );
            write( "That's cool!" );
            //while ( true ) {

            //}
        } catch( Exception e ){
            Out.printError( classId, "Error: " + e.getMessage() );
        } finally {
            try {
                out.close();
                in.close();
                client.close();
            } catch ( IOException ioe ){
                Out.printError( classId, "Error closing streams: " + ioe.getMessage() );
            }
        }
    }
    private void write( String message ) throws IOException {
        out.writeObject( message );
        out.flush();
    }
}
