package server.ui.components;

import server.data.Computer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * ComputerList is the scroll-able computer list thing
 */
public class ComputerList extends JPanel {

    private ArrayList< JLabel > comps = new ArrayList<>();
    private JScrollBar b;
    private JPanel pnlComp;
    private MouseAdapter ma;

    private int startX = 0;

    public ComputerList() {
        super();

        setLayout( new BorderLayout( 4, 4 ) );

        ma = new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                startX = e.getXOnScreen();
            }

            @Override
            public void mouseDragged( MouseEvent e ) {
                adjustLocations( e.getXOnScreen() - startX, 1 );
                startX = e.getXOnScreen();
            }
        };

        addMouseListener( ma );
        addMouseMotionListener( ma );

        pnlComp = new JPanel();
        pnlComp.setLayout( new BoxLayout( pnlComp, BoxLayout.X_AXIS ) );
        pnlComp.setBorder( new TitledBorder( "Known Systems" ) );
        pnlComp.addMouseListener( ma );
        pnlComp.addMouseMotionListener( ma );
        pnlComp.addMouseWheelListener( mouseWheelEvent -> adjustLocations( mouseWheelEvent.getUnitsToScroll(), 0 ) );

        add( pnlComp, BorderLayout.CENTER );

        // Testing computers
        test( 4 );

    }

    public void addComputer( String computerName ) {
        // Create a computer icon / entity
        Computer c = new Computer( computerName, "IP HERE" );
        c.addMouseListener( ma );
        c.addMouseMotionListener( ma );
        // Add it to our list of tracked computers in case we need to access them
        // Which, we will.
        comps.add( c );

        // Then add it to the Computer JPanel.
        pnlComp.add( c );
        pnlComp.add( Box.createRigidArea( new Dimension( 24, this.getHeight() ) ) );
        repaint();
    }

    private void adjustLocations( int howMuch, int mode ) {
        for ( JLabel lbl : getComputers() ) {

            int factor = 8;

            if ( mode == 1 )
                factor = 1;

            lbl.setLocation( lbl.getX() + ( howMuch * factor ), lbl.getY() );

            // This handles the bounds of the scroll area.
            // Pretty much, detect if the first is too far right or the last is too far left
            // and then adjust each one accordingly.
            // This should never need to be changed. Ever.
            int diff = 0;
            int buffer = 8;

            // Location of the last computer's edge
            int lastLocEnd =
                    getComputers().get( comps.size() - 1 ).getX() +
                            getComputers().get( comps.size() - 1 ).getWidth();

            // If the first computer start x() is too far right, adjust everyone back by however much
            // If the last computer's end width() + x() is too far left, adjust forward
            boolean adjust = false;
            if ( getComputers().get( 0 ).getX() > getX() ) {
                diff = getComputers().get( 0 ).getX() * -1 + buffer;
                adjust = true;
            }
            else if ( lastLocEnd < ( getWidth() + getX() ) ) {
                diff = ( getWidth() + getX() ) - lastLocEnd - buffer;
                adjust = true;
            }

            // If we need to adjust, do it.
            if ( adjust ) {
                for ( JLabel lbls : getComputers() )
                    lbls.setLocation( lbls.getX() + diff, lbls.getY() );
            }
        }
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
