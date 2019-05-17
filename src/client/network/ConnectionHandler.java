package client.network;

import client.data.MacroRepository;
import client.data.Details;
import client.data.ScreenImager;
import res.Out;
import server.network.info.Message;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles client connection as a sperate thread.
 */
public class ConnectionHandler extends Thread {
    
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket client;
    
    private boolean transactionCompleted = false;
    private String classId = this.getClass().getSimpleName();

    /**
     * Handles the connection between this and the InfoServer
     * @param port the port to listen on. Specified by -p < port >
     */
    public ConnectionHandler( int port ) {
        // One connection at a time!
        ServerSocket ss = null;
        boolean acceptingConnections = true;
        try {
            ss = new ServerSocket( port, 50 );
        } catch ( IOException ioe ) {
            Out.printError( classId, "Could not start server socket: " + ioe.getMessage() );
            acceptingConnections = false;
        }

        // Declare a details class to use instead of creating a new
        // one every request
        Details details = new Details();
        ScreenImager si = new ScreenImager();

        // While this client is accepting clients, accept clients.
        // I may implement a way for this to shut itself off if it detects abnormal
        // cpu usage.
        while ( acceptingConnections ) {
            try {
                Out.printInfo( classId, "Listening on " + InetAddress.getLocalHost() + ":" + 25566 );
                Out.printInfo( classId, "Waiting for connection..." );
                client = ss.accept();
                
                out = new ObjectOutputStream( client.getOutputStream() );
                
                in = new ObjectInputStream( client.getInputStream() );
                Out.printInfo( classId, "InfoServer has connected and streams created" );
                transactionCompleted = false;
            } catch ( IOException ie ) {
                Out.printError( classId, "Error accepting remote connection: " + ie.getMessage() );
            }
            
            // While the transaction between the server and client is not done,
            // continue to receive messages

            Out.printInfo( classId, "Ready to begin processing commands." );

            while ( !transactionCompleted ) {
                Object ob = null;
                try {
                    ob = in.readObject();
                } catch ( EOFException eofe ) {
                    Out.printError( classId, "EOFException: " + eofe.getMessage() );
                    break;
                } catch ( ClassNotFoundException cnfe ) {
                    Out.printError( classId, "Class not found error: " + cnfe.getMessage() );
                    break;
                } catch ( IOException ioe ) {
                    if ( ioe.getMessage().contains( "Connection reset" ) )
                        transactionCompleted = true;
                    else
                        Out.printError( classId, "IO Exception: " + ioe.getMessage() );
                }
                if ( ob instanceof Message ) {
                    Message mes = ( Message ) ob;
                    if ( mes.getPrimaryCommand().contains( "DETAILS" ) ) {
                        // Client requests details, send them and a screenshot
                        mes.setInfo( details.getDetails() );
                        mes.setImg( si.getScreenshot() );
                        mes.setSecondayCommand( MacroRepository.getInstance().getLoadedMacros() );
            
                        write( mes );
            
                    }
                    else if ( mes.getPrimaryCommand().equalsIgnoreCase( "LOAD MACRO" ) ) {
                        // We want to load the macro that is coming through the pipeline
                        MacroRepository.getInstance().loadMacro( mes.getMacro() );
                    }
                    else if ( mes.getPrimaryCommand().equalsIgnoreCase( "REVOKE MACRO" ) ) {
                        // Unload macro based on string name
                        MacroRepository.getInstance().unloadMacro( mes.getSecondaryCommand() );
                    }
                    else if ( mes.getPrimaryCommand().equalsIgnoreCase( "GET MACROS" ) ) {
                        // Compile a list of what macros we have loaded here, and send it to InfoServer
                        mes.setSecondayCommand( MacroRepository.getInstance().getLoadedMacros() );
                        write( mes );
                    }
                    else if ( mes.getPrimaryCommand().startsWith( "RUN MACRO" ) ) {
                        // Run whichever macro was specified after MACRO ( 1 -> x )
                        String nameOfMacroToRun = mes.getSecondaryCommand();
                        Out.printInfo( classId, "Running macro: " + nameOfMacroToRun );
                        MacroRepository.getInstance().runMacro( nameOfMacroToRun );
                    }
                    else if ( mes.getPrimaryCommand().equalsIgnoreCase( "RUN" ) ) {
                        if ( !mes.getSecondaryCommand().equals( "" ) ) {
                            try {
                                Runtime.getRuntime().exec( mes.getSecondaryCommand() );
                            } catch ( IOException ioe ){
                                Out.printError( classId, "Issue running command from server: " + ioe.getMessage() );
                            }
                        }
                    }
                    else if ( mes.getPrimaryCommand().contains( "GOODBYE" ) ) {
                        // Client is disconnecting ( Server is switching computers )
                        transactionCompleted = true;
                        Out.printInfo( classId, "Goodnight." );
                    }
                }
            }
            Out.printInfo( classId, "No longer processing commands for connection." );
            disconnectFromCurrent();
        }
    }
    
    private void write( Message mes ) {
        try {
            out.writeObject( mes );
            out.flush();
        } catch ( IOException ioe ) {
            Out.printError( classId, "write() could not send message: " + ioe.getMessage() );
        }
    }
    
    /**
     * Disconnects all current streams
     */
    private void disconnectFromCurrent(){
        try {
            out.close();
        } catch ( IOException e ) {
            Out.printError( classId, "Couldn't close out. Already closed?" );
        }
        try {
            in.close();
        } catch ( IOException e ) {
            Out.printError( classId, "Couldn't close in. Already closed?" );
        }
        try {
            client.close();
        } catch ( IOException e ) {
            Out.printError( classId, "Couldn't close server socket. Already closed?" );
        }
        Out.printInfo( classId, "All streams closed succesfully." );
        transactionCompleted = true;
    }
}
