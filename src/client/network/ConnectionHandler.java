package client.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread {

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ConnectionHandler(){
        System.out.println( "Waiting" );
        try {
            Socket temp = new Socket( "192.168.8.133", 25566 );

            out = new ObjectOutputStream( temp.getOutputStream() );
            in = new ObjectInputStream( temp.getInputStream() );

            while ( true ){
                Object ob = in.readObject();
                if ( ob instanceof String ){
                    if ( ( ( String ) ob ).contains( "name" ) ){
                        write( "Chris!" );
                    }
                }
            }
        } catch( Exception ie ){
            System.err.println( "oof " + ie.getMessage() );
            ie.printStackTrace();
        }
    }

    private void write( String mes ) throws IOException {
        out.writeObject( mes );
        out.flush();
    }
}
