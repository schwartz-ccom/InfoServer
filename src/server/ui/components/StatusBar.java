package server.ui.components;

import server.types.Computer;
import server.data.DataHandler;
import server.data.macro.Macro;
import server.data.macro.MacroHandler;
import server.network.NetworkHandler;
import server.resources.ComputerSubscriber;
import server.resources.MacroSubscriber;
import server.resources.NetworkStatusSubscriber;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class StatusBar extends JLabel implements ComputerSubscriber, MacroSubscriber, NetworkStatusSubscriber {

    private String cMes, mMes, sMes;

    public StatusBar(){
        super();
        NetworkHandler.getInstance().subscribeToStats( this );
        MacroHandler.getInstance().subscribe( this );
        DataHandler.getInstance().subscribe( this );

        setBorder( new CompoundBorder( new EmptyBorder( 4,4,4,4 ), new EtchedBorder() ) );
        setPreferredSize( new Dimension( 300, 30 ) );

        cMes = "None";
        mMes = "0";
        sMes = "Disconnected";
        formatText();
    }

    @Override
    public void updateComputer( Computer data ) {
        cMes = data.getComputerName();
        formatText();
    }

    @Override
    public void updateMacros( Macro[] macros ) {
        mMes = String.valueOf( macros.length );
        formatText();
    }

    @Override
    public void updateStatus( String mes ) {
        sMes = mes;
        if ( mes.contains( "NO ACTIVITY" ) )
            sMes = "No Network Activity / Disconnected";

        formatText();
    }

    private void formatText(){
        String toWrite = String.format( "> %-40s > %-30s > %-40s",
                " Computer: " + cMes,
                "Macro Count: " + mMes,
                "Network Status: " + sMes
        );
        setText( toWrite );
    }

}
