package server.resources;

import res.Out;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Macro {

    private String classId = this.getClass().getSimpleName();
    private List< String > events;

    private Robot r;

    public Macro() {
        events = new ArrayList<>();

    }

    public void addAction( String cmd ) {
        events.add( cmd );
    }

    public void runMacro() {
        try {
            r = new Robot();
        } catch ( Exception e ) {
            Out.printError( classId, "Error starting Robot: " + e.getMessage() );
        }

        for ( String cmd : events ) {
            String[] parts = cmd.split( " ", -1 );
            switch ( parts[ 0 ].toLowerCase() ) {
                case "mouse":
                    switch ( parts[ 1 ].toLowerCase() ) {
                        case "move":
                            move( parts[ 2 ], parts[ 3 ] );
                            break;
                        case "press":
                            pressMouse( parts[ 2 ] );
                            break;
                        default:
                            throwError( "Unknown secondary command: " + parts[ 1 ] );
                            break;
                    }
                case "key":
                    pressKey( parts[ 2 ] );
                    break;
                case "run":
                    run( parts[ 1 ] );
                    break;
                case "delay":
                    delay( parts[ 1 ] );
                    break;
                default:
                    throwError( "Unknown primary command: " + parts[ 0 ] );
            }
        }
    }

    private void move( String x, String y ) {
        try {
            int locX = Integer.valueOf( x );
            int locY = Integer.valueOf( y );

            r.mouseMove( locX, locY );
        } catch ( Exception e ) {
            throwError( "Error moving mouse: " + e.getMessage() );
        }
    }

    private void pressMouse( String button ) {

        // Button 1 = left click
        // Button 2 = Right click
        // Button 3 = Scroll wheel click
        try {
            r.mousePress( Integer.valueOf( button ) );
            r.delay( 4 );
            r.mouseRelease( Integer.valueOf( button ) );
        } catch ( Exception e ) {
            throwError( "Error pressing mouse: " + e.getMessage() );
        }
    }

    private void pressKey( String button ) {

        // Button 1 = left click
        // Button 2 = Right click
        // Button 3 = Scroll wheel click
        try {
            r.keyPress( Integer.valueOf( button ) );
            r.delay( 4 );
            r.keyRelease( Integer.valueOf( button ) );
        } catch ( Exception e ) {
            throwError( "Error pressing key: " + e.getMessage() );
        }
    }

    private void run( String path ) {
        try {
            Runtime.getRuntime().exec( path );
        } catch ( Exception e ) {
            throwError( "Error running program: " + e.getMessage() );
        }
    }

    private void delay( String time ) {
        try {
            r.delay( Integer.valueOf( time ) );
        } catch ( Exception e ) {
            throwError( "Error delaying exec: " + e.getMessage() );
        }
    }

    private void throwError( String mes ) {
        Out.printError( classId, mes );
    }
}
