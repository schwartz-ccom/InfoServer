package server.ui.components;

import server.types.Computer;
import server.data.DataHandler;
import server.network.NetworkHandler;
import server.network.info.Message;
import server.resources.NetworkStatusSubscriber;

import javax.swing.*;
public class ConnectButton extends JButton implements NetworkStatusSubscriber {

    // 0 = Disconnected
    // 1 = Connecting
    // 2 = Successfully connected
    private int status = 0;
    public ConnectButton( String title ) {
        super( title );
        NetworkHandler.getInstance().subscribeToStats( this );
        this.addActionListener( actionEvent -> {
            setStatus( ++status );
            if ( status == 1 )
                connectToSelectedComputer();
            else if ( status == 2 )
                disconnectFromComputer();
        } );
    }
    private void setStatus( int s ) {
        this.status = s;
        switch ( status ) {
            case 0:
                setText( "Connect" );
                break;
            case 1:
                setText( "Connecting..." );
                break;
            case 2:
                setText( "Disconnect" );
                break;
            case 3:
                status = 2;
        }
    }
    private void connectToSelectedComputer() {
        Computer toConnect = DataHandler.getInstance().getCurrentComputer();
        if ( toConnect != null) {
            NetworkHandler.getInstance().setTarget( toConnect.getIP() );
            new Thread( NetworkHandler.getInstance() ).start();
        }
        else
            setStatus( 0 );
    }

    private void disconnectFromComputer() {
        NetworkHandler.getInstance().sendCommand( new Message( "GOODBYE" ) );
        setStatus( 0 );
    }
    @Override
    public void updateStatus( String mes ) {
        // if the connecting computer is not available, reset.
        if ( mes.contains( "not avail" ) )
            setStatus( 0 );
        else if ( mes.contains( "is connected" ) )
            setStatus( 2 );
        else if ( mes.contains( "NO ACTIVITY" ) )
            setStatus( 0 );
    }
}
