package server.ui.components.macrocomps.events;

import res.Out;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Handles all of the mouse events on the frame, and compiles a list of what happens
 * for later macro creation
 */
public class MouseEventHandler extends MouseAdapter implements MouseMotionListener {

    // Declare the graphics vars to use throughout the methods.
    private Graphics2D g;

    private Point startLocation;
    private Point lastLocation;
    private boolean dragged = false;

    // Set different strokes
    Stroke dashed = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{ 9 }, 0 );
    Stroke solid = new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );

    // I'm so tired.
    private String classId = this.getClass().getSimpleName();

    public MouseEventHandler( Component source ) {
        g = ( Graphics2D ) source.getGraphics();
        g.setStroke( solid );
    }

    @Override
    public void mouseClicked( MouseEvent e ) {
        drawX( e.getPoint(), 0 );
        g.drawString( "Click", e.getX() - 20, e.getY() - 15 );
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        if ( lastLocation == null )
            lastLocation = e.getPoint();
        startLocation = e.getPoint();

        g.setStroke( dashed );
        g.drawLine( lastLocation.x, lastLocation.y, e.getX(), e.getY() );
        g.setStroke( solid );
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        if ( dragged ) {
            // If I drew the line to the right
            if ( startLocation.x < e.getX() )
                drawX( startLocation, 2 );
            else if ( startLocation.x > e.getX() )
                drawX( startLocation, 1 );

            g.drawLine( startLocation.x, startLocation.y, e.getX(), e.getY() );
            g.drawString( "Drag Event", e.getX() - 20, e.getY() - 15 );
        }

        lastLocation = e.getPoint();
        dragged = false;
    }

    @Override
    public void mouseMoved( MouseEvent e ) {

    }

    @Override
    public void mouseDragged( MouseEvent e ) {
        dragged = true;
    }

    /**
     * Draws an x on the screen.
     *
     * Mode = 0, a full X
     * Mode = 1, Arrow tip pointed <
     * Mode = 2, Arrow tip point >
     *
     * @param loc the Point to draw it at
     * @param mode what mode to use
     */
    private void drawX( Point loc, int mode ){
        // Formatting vars
        int clickDrawSize = 8;

        if ( mode == 1 ){
            g.drawLine(
                    loc.x,
                    loc.y,
                    loc.x + clickDrawSize,
                    loc.y - clickDrawSize );

            g.drawLine(
                    loc.x,
                    loc.y,
                    loc.x + clickDrawSize,
                    loc.y + clickDrawSize );
        }
        else {
            // Draw two lines to make an X
            g.drawLine(
                    loc.x - clickDrawSize,
                    loc.y - clickDrawSize,
                    loc.x + clickDrawSize,
                    loc.y + clickDrawSize );

            g.drawLine(
                    loc.x + clickDrawSize,
                    loc.y - clickDrawSize,
                    loc.x - clickDrawSize,
                    loc.y + clickDrawSize );
        }
    }
}
