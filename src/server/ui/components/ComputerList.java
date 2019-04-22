package server.ui.components;

import res.Out;
import server.data.Computer;

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

    public ComputerList() {
        super();

        setLayout( new BorderLayout( 4, 4 ) );

        pnlComp = new JPanel();
        pnlComp.setLayout( new BoxLayout( pnlComp, BoxLayout.X_AXIS ) );
        pnlComp.setBorder( new TitledBorder( "Registered Systems" ) );

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

        // Testing computers
        test( 123 );
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

    public void addComputer( String computerName ) {
        // Create a computer icon / entity
        Computer c = new Computer( computerName, "IP HERE" );

        // Add it to our list of tracked computers
        comps.add( c );

        maxRows = comps.size() / screensPerRow;
        if ( comps.size() % screensPerRow == 0 )
            maxRows -= 1;

        if ( maxRows == 0 )
            sb.setEnabled( false );
        else
            sb.setEnabled( true );
        sb.setMaximum( maxRows );

        Out.printInfo( "Asd", "Row Count: " + maxRows );
        repaint();
    }

    public ArrayList< JLabel > getComputers() {
        return comps;
    }

    private void test( int c ) {

        for ( int x = 0; x < c; x++ ) {
            addComputer( "Computer " + x );
        }
    }

}
