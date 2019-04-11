package server.network;

import server.data.Computer;
import server.resources.Subscriber;

/**
 * The top level networking part.
 * I'm only doing a single seat server, so this'll be easier than say a chat client.
 *
 * Although, that means I won't be able to batch control more than one computer
 * in the future, which I think I'm okay with.
 *
 * This set up could become an issue if I have multiple clients trying to send me
 * data, but I believe that while I can only work with one client at a time, I could
 * have a queue of sorts that holds everyone until the server is ready to handle them.
 *
 * The client is set up so it's not supposed to connect to the server. The server is
 * supposed to be the one initiating the connection to the client, who then retrieves information
 * from the client, and then drops the connection, forcing the client to wait until the next
 * command.
 *
 * As I write that, it seems like it would waste cpu cycles, so I'll have to implement a state
 * machine that re-enables the client when it gets told to wake up.
 */
public class ConnectionHandler extends Thread implements Subscriber {

    private boolean isConnected = false;

    public ConnectionHandler( String ip ){

    }

    /**
     * Handles destruction of current connection
     */
    public void kill(){
        if ( !isConnected )
            return;
        else {
            // Carry on?
        }
    }
    // Update the connection's IP address and retry connection
    @Override
    public void update( Computer data ) {
        this.kill();
        new ConnectionHandler( data.getIP() );
    }
}
