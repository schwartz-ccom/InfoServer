package client.network;

import client.data.DataRepository;
import client.data.ScreenImager;
import res.Out;
import server.data.macro.Macro;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket client;
    private final Object m = new Object();

    private String classId = this.getClass().getSimpleName();
    private boolean acceptingConnections = true;

    public ConnectionHandler() {
        // One connection at a time!
        ServerSocket ss = null;
        try {
            ss = new ServerSocket( 25566, 50 );
            Out.printInfo( classId, "Started Server Socket on port " + ss.getLocalPort() );
        } catch ( IOException ioe ){
            Out.printError( classId, "Could not start server socket: " + ioe.getMessage() );
            acceptingConnections = false;
        }
        while ( acceptingConnections ) {
            try {
                Out.printInfo( classId, "Listening on " + InetAddress.getLocalHost() + ":" + 25566 );
                Out.printInfo( classId, "Waiting for connection..." );
                client = ss.accept();

                out = new ObjectOutputStream( client.getOutputStream() );

                in = new ObjectInputStream( client.getInputStream() );
                Out.printInfo( classId, "InfoServer has connected and streams created" );

            } catch ( IOException ie ) {
                Out.printError( classId, "Error accepting remote connection: " + ie.getMessage() );
            }

            boolean transactionCompleted = false;
            while ( !transactionCompleted ) {
                // Receive request for data, and then the image request
                try {
                    Out.printInfo( classId, "Waiting for request..." );
                    Object ob = in.readObject();
                    if ( ob instanceof String ) {
                        String mes = ( String ) ob;
                        Out.printInfo( classId, "Message: " + mes );
                        if ( mes.contains( "DETAILS" ) ) {
                            // Client requests details, send them and a screenshot
                            write( DataRepository.getInstance().getData() );

                            // Get the screenshot from ScreenImager
                            RenderedImage im = ScreenImager.getScreenshot();

                            // Send the InfoServer the size for the byte array
                            ImageIO.write( im, "png", client.getOutputStream() );

                            Out.printInfo( classId, "Successfully sent InfoServer details" );
                        }
                        else if ( mes.equalsIgnoreCase( "LOAD MACRO" ) ) {
                            // We want to load the macro that is coming through the pipeline
                            Object read = in.readObject();
                            if ( read instanceof Macro ) {
                                DataRepository.getInstance().loadMacro( ( Macro ) read );
                            }
                            else
                                Out.printError( classId, "Unexpected object sent through pipe." );
                        }
                        else if ( mes.startsWith( "RUN MACRO" ) ) {
                            // Run whichever macro was specified after MACRO ( 1 -> x )
                            String nameOfMacroToRun = mes.substring( 11 );
                            Out.printInfo( classId, "Running macro: " + nameOfMacroToRun );
                            //DataRepository.getInstance().runMacro( nameOfMacroToRun );
                        }
                        else if ( mes.startsWith( "RUN" ) ){
                            if ( !mes.substring( 4 ).isEmpty() )
                                Runtime.getRuntime().exec( mes.substring( 4 ) );
                        }
                        else if ( mes.contains( "GOODBYE" ) ) {
                            // Client is disconnecting ( Server is switching computers )
                            transactionCompleted = true;
                        }
                    }
                } catch ( ClassNotFoundException cnfe ) {
                    Out.printError( classId, "ClassNotFound Exception: " + cnfe.getMessage() );
                } catch ( IOException ioe ){
                    ioe.printStackTrace();
                    if ( ioe.getMessage().contains( "reset" ) )
                        transactionCompleted = true;
                    Out.printError( classId, "IO Exception: " + ioe.getMessage() );
                }
            }
            Out.printInfo( classId, "Transaction completed." );
            try {
                out.close();
                in.close();
                client.close();
                Out.printInfo( classId, "CLOSED STREAMS" );
            } catch ( IOException ioe ){
                Out.printError( classId, "Could not close streams: " + ioe.getMessage() );
            }
        }
    }

    private void write( Object mes ){
        try {
            out.writeObject( mes );
            out.flush();
        } catch ( IOException ioe ){
            Out.printError( classId, "write() could not send message: " + ioe.getMessage() );
        }
    }

    private void wait( int ms ) {
        try {
            Thread.sleep( ms );
        } catch ( InterruptedException ie ) {
            Out.printError( classId, "How the heck did I fail sleeping?" );
        }
    }
}
