package server.ui.components.macrocomps.events;

import res.Out;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

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
    private Stroke dashed = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{ 9 }, 0 );
    private Stroke solid = new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );

    // The scale for translating points
    private double scaleFactorX;
    private double scaleFactorY;


    // String list to keep up with Macro commands
    private ArrayList< String > macroCommands = new ArrayList<>();

    // I'm so tired.
    private String classId = this.getClass().getSimpleName();

    public MouseEventHandler( Component source, Dimension screenSize ) {
        g = ( Graphics2D ) source.getGraphics();
        g.setStroke( solid );

        scaleFactorX = screenSize.width / ( double ) source.getWidth();
        scaleFactorY = screenSize.height / ( double ) source.getHeight();

        Out.printInfo( classId, "Scale X: " + scaleFactorX );
        Out.printInfo( classId, "Scale Y: " + scaleFactorY );

    }

    @Override
    public void mouseClicked( MouseEvent e ) {
        drawX( e.getPoint() );

        String toWrite = "Left Click";
        if ( e.getButton() == 2 ) {
            toWrite = "Middle Click";
            addCommandToMacro( "MOUSE CLICK 3" );
        }
        else if ( e.getButton() == 3 ) {
            toWrite = "Right Click";
            addCommandToMacro( "MOUSE CLICK 2" );
        }
        else {
            addCommandToMacro( "MOUSE CLICK 1" );
        }

        g.drawString( toWrite, e.getX() - 10, e.getY() - 15 );
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        if ( lastLocation == null )
            lastLocation = e.getPoint();
        startLocation = e.getPoint();

        g.setStroke( dashed );
        g.drawLine( lastLocation.x, lastLocation.y, e.getX(), e.getY() );
        g.drawString( "Mouse Move", lastLocation.x - e.getX(), lastLocation.y - e.getY() - 10 );

        // We moved the mouse.
        int[] trans = translatePoints( e.getPoint() );
        addCommandToMacro( "MOUSE MOVE " + trans[ 0 ] + " " + trans[ 1 ] );

        // Reset the stroke
        g.setStroke( solid );
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        if ( dragged ) {
            int ovalRadius = 10;

            g.drawLine( startLocation.x, startLocation.y, e.getX(), e.getY() );
            g.drawOval( e.getX() - ( ovalRadius / 2 ), e.getY() - ( ovalRadius / 2 ), ovalRadius, ovalRadius );
            g.drawString( "Drag", e.getX() - ( ovalRadius / 2 ) - 10, e.getY() - ( ovalRadius / 2 ) - 10 );

            int[] trans = translatePoints( e.getPoint() );

            // Dragged the mouse
            // Note, as of 5/23 this is not implemented
            addCommandToMacro( "MOUSE DRAG " + trans[ 0 ] + " " + trans[ 1 ] );
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
     * Mode = 0, a full X
     * Mode = 1, Arrow tip pointed <
     * Screw this method.
     *
     * @param loc  the Point to draw it at
     */
    private void drawX( Point loc ) {
        // Formatting vars
        int length = 8;

        // Draw two lines to make an X
        g.drawLine(
                loc.x - length,
                loc.y - length,
                loc.x + length,
                loc.y + length );

        g.drawLine(
                loc.x + length,
                loc.y - length,
                loc.x - length,
                loc.y + length );

    }

    /**
     * Translates the points from a 1240x720 to 1920x1080 ( or whatever ) screen
     * @param e The point to translate
     * @return The translated points, 0 = x, 1 = y
     */
    private int[] translatePoints( Point e ){

        int[] translated = new int[ 2 ];

        translated[ 0 ] = ( int ) ( e.getX() * scaleFactorX );
        translated[ 1 ] = ( int ) ( e.getY() * scaleFactorY );

        return translated;
    }

    /**
     * Helper method that adds a delay of 50 between every step to account
     * for animations and whatnot
     * @param cmd The command to add
     */
    private void addCommandToMacro( String cmd ){
        addCommandToMacro( cmd, 10 );
    }

    /**
     * Same as above, but allows customizable delay ( if waiting for app to launch )
     * @param cmd The command to add
     * @param delay The delay to add in milliseconds
     */
    private void addCommandToMacro( String cmd, int delay ){
        macroCommands.add( cmd );
        macroCommands.add( "DELAY " + delay );
    }
}
