package server.ui.components;

import server.data.Computer;
import server.data.DataHandler;

import javax.swing.*;
public class ConnectButton extends JButton {

    // 0 = Disconnected
    // 1 = Connecting
    // 2 = Successfully connected
    private int status = 0;

    public ConnectButton( String title ) {
        super( title );

        this.addActionListener( actionEvent -> {
            setStatus( 1 );

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
                setText( "Connected" );
                break;
        }
    }

    private void connectToSelectedComputer() {
        Computer toConnect = DataHandler.getInstance().getCurrentComputer();

    }
}
