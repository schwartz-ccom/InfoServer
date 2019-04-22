package client.network;

import client.DataRepository;
import client.data.Details;
import client.data.ScreenImager;
import res.Out;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ImageOutputStream outImg;

    private String classId = this.getClass().getSimpleName();
    private boolean acceptingConnections = true;

    public ConnectionHandler() {
        // One connection at a time!
        while ( acceptingConnections ) {
            try {
                ServerSocket ss = new ServerSocket( 25566 );
                Socket client = ss.accept();

                out = new ObjectOutputStream( client.getOutputStream() );
                outImg = ImageIO.createImageOutputStream( client.getOutputStream() );

                in = new ObjectInputStream( client.getInputStream() );

            } catch ( IOException ie ) {
                Out.printError( classId, "Error accepting remote connection: " + ie.getMessage() );
            }

            boolean transactionCompleted = false;
            while ( !transactionCompleted ) {
                // Receive request for data, and then the image request
                try {
                    Object ob = in.readObject();
                    String mes;
                    if ( ob instanceof String ) {
                        mes = ( String ) ob;
                        if ( mes.contains( "DETAILS" ) ) {
                            // Client requests details, send them and a screenshot
                            write( DataRepository.getInstance().getData() );

                            RenderedImage im = ScreenImager.getScreenshot();

                            ImageIO.write( im, "png", outImg );
                            out.flush();
                        }
                        else if ( mes.equalsIgnoreCase( "LOAD MACRO" ) ){
                            // We want to load the macro that is coming throught the pipeline
                        }
                        else if ( mes.startsWith( "RUN MACRO" ) ){
                            // Run whichever macro was specified after MACRO ( 1 -> x )

                        }
                        else if ( mes.contains( "GOODBYE" ) ){
                            // Client is disconnecting ( Server is switching computers )
                            transactionCompleted = true;
                        }
                    }
                } catch ( Exception ie ) {
                    Out.printError( classId, "IO Exception: " + ie.getMessage() );
                }
            }
        }
    }

    private void write( Object mes ) throws IOException {
        out.writeObject( mes );
        out.flush();
    }

    private void wait( int ms ) {
        try {
            Thread.sleep( ms );
        } catch ( InterruptedException ie ) {
            Out.printError( classId, "How the heck did I fail sleeping?" );
        }
    }
}
