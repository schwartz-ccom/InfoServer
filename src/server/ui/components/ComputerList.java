package server.ui.components;

import server.data.Computer;
import server.data.ComputerAdder;
import server.data.DataHandler;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * ComputerList is the scroll-able computer list thing
 */
public class ComputerList extends JPanel {

    private ArrayList< JLabel > comps = new ArrayList<>();
    private JScrollBar sb;
    private JPanel pnlComp;

    private int gapBetweenObjects = 18;
    private int screensPerRow = 3;
    private int currentRow = 1;
    private int maxRows = 1;
    private int lastRow = 0;

    private static ComputerList instance;

    public static ComputerList getInstance(){
        if ( instance == null )
            instance = new ComputerList();
        return instance;
    }

    private ComputerList() {
        super();

        setLayout( new BorderLayout( 4, 4 ) );

        pnlComp = new JPanel();
        pnlComp.setLayout( new BoxLayout( pnlComp, BoxLayout.X_AXIS ) );
        pnlComp.setBorder( new TitledBorder( "Registered Systems" ) );

        pnlComp.add( new ComputerAdder() );

        add( pnlComp, BorderLayout.CENTER );

        sb = new JScrollBar();
        sb.setOrientation( JScrollBar.VERTICAL );
        sb.setMaximum( 0 );
        sb.addAdjustmentListener( adjustmentEvent -> {
            if ( lastRow != sb.getValue() ) {
                updateDisplay( sb.getValue() );
                lastRow = sb.getValue();
            }
        } );

        lastRow = sb.getValue();

        add( sb, BorderLayout.EAST );

        updateDisplay( 0 );
    }

    // Currently only showing three computers to a row by default
    private void updateDisplay( int row ) {

        // Remove all computers on the thing now
        pnlComp.removeAll();
        pnlComp.revalidate();

        for ( int x = ( row * screensPerRow ); x < ( row * screensPerRow ) + screensPerRow; x++ ) {
            if ( x < comps.size() ) {
                if ( comps.get( x ) != null ) {
                    pnlComp.add( comps.get( x ) );
                    if ( x != ( row * screensPerRow ) + screensPerRow - 1 )
                        pnlComp.add( Box.createHorizontalStrut( gapBetweenObjects ) );
                }
            }
        }

        if ( comps.size() == 1 ){
            // Kind of a shitty way to do it, but pretty much get the JLabel
            // representing the computer to get the name of the just added comp,
            // and then get the computer through that, and then set that computer as
            // selected.
            DataHandler.getInstance().getComputer( comps.get( 0 ).getText() ).setDataComputer();
        }

        pnlComp.add( new ComputerAdder() );

        currentRow = row;
        pnlComp.repaint();
    }

    public void updateFrameSize( int newSize ) {
        int old = screensPerRow;
        screensPerRow = newSize / ( 256 + gapBetweenObjects );
        maxRows = comps.size() / screensPerRow;

        if ( old != screensPerRow )
            updateDisplay( currentRow );
    }

    public void addComputerToDisplay( Computer c ) {
        // Add it to our list of tracked computers
        comps.add( c );

        refreshUI();
        updateDisplay( currentRow );
    }

    private void refreshUI(){
        maxRows = comps.size() / screensPerRow;
        if ( comps.size() % screensPerRow == 0 )
            maxRows -= 1;

        if ( maxRows == 0 )
            sb.setEnabled( false );
        else
            sb.setEnabled( true );
        sb.setMaximum( maxRows );

        repaint();
    }
}
