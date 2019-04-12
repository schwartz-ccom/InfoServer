package server.data;

import res.Out;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gives the user a way to play out events on a foreign ( non host ) computer
 * Commands are limited, but that's intentional for now, since I don't see
 * much use in expanding those.
 */
public class Macro extends Thread {

    // Private things needed all over the class.
    private String classId = this.getClass().getSimpleName();
    private List< String > events;
    private String myName;

    // Responsible for actually carrying out the actions.
    private Robot r;

    /**
     * Constructor of the Macro.
     * Just initializes the list.
     */
    public Macro( String macroName ) {
        this.myName = macroName;
        events = new ArrayList<>();
    }

    /**
     * Gives the name of the macro so users can identify what the macro is.
     *
     * @return The macro name as String
     */
    public String getMacroName() {
        return this.myName;
    }

    /**
     * Get the macro steps.
     * Useful when editing / viewing
     *
     * @return The macro steps as String[]
     */
    public String[] getMacroSteps() {
        String [] toRet = new String[ events.size() ];

        for ( int x = 0; x < events.size(); x++ ){
            toRet[ x ] = events.get( x );
        }
        return toRet;
    }

    /**
     * Modifies a certain step in the macro, so that users don't have to retype the entire
     * macro.
     *
     * @param stepNumber     The step to edit as int. Step 1 is the 0th slot in the macro, so decrement by 1
     * @param modifiedAction The action to replace the step as String
     */
    public void modifyStep( int stepNumber, String modifiedAction ) {
        if ( stepNumber >= events.size() )
            events.add( modifiedAction );
        else
            events.set( stepNumber, modifiedAction );
    }


    /**
     * Adds and action to the macro. Valid actions are currently written on a sticky
     * note at my desk, but I'll get proper documentation on it when I implement the
     * macro builder / computer hotkey action builder.
     *
     * @param cmd The macro command to add as String
     */
    public void addAction( String cmd ) {
        events.add( cmd );
    }

    /**
     * If the user just wants to create a general Macro, s/he can save it.
     * The save function which hasn't been implemented as of right now stores this in
     * DataHandler.
     */
    public void saveMacro() {
        MacroHandler.getInstance().addMacroToCollection( this );
    }

    /**
     * Inherited from Thread, this starts execution of the macro.
     */
    public void run() {
        try {
            r = new Robot();
            r.setAutoDelay( 10 );
            r.setAutoWaitForIdle( true );
        } catch ( Exception e ) {
            Out.printError( classId, "Error starting Robot: " + e.getMessage() );
        }

        // For the sake of readability, a lot of the value processing is done in the methods
        //    so that we aren't converting Strings to ints in the actual switch statement.
        // It would be a nightmare to read, and this allows for error checking.
        for ( String cmd : events ) {

            // Split the command into parts based on the space character, and then use:
            // parts[ 0 ] as primary command ( mouse, key, run, type, delay )
            // parts[ 1 ] as secondary ( mouse press, mouse move ), or as the data
            // parts[ 2 ] and parts[ 3 ] is always data
            String[] parts = cmd.split( " ", -1 );
            switch ( parts[ 0 ].toLowerCase() ) {
                case "mouse":
                    switch ( parts[ 1 ].toLowerCase() ) {
                        case "move":
                            if ( parts.length < 4 )
                                throwError( "You forgot a value" );
                            else
                                move( parts[ 2 ], parts[ 3 ] );
                            return;
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
                case "type":
                    type( parts[ 1 ] );
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

    /**
     * Move the mouse to a certain location on the screen
     *
     * @param x the X coordinate as String. Converted to integer.
     * @param y the Y coordinate as String. Converted to integer.
     */
    private void move( String x, String y ) {
        try {
            int locX = Integer.valueOf( x );
            int locY = Integer.valueOf( y );

            r.mouseMove( locX, locY );
        } catch ( Exception e ) {
            throwError( "Error moving mouse: " + e.getMessage() );
        }
    }

    /**
     * Press whichever mouse button is specified
     *
     * @param button The ID of the mouse button as String. Converted to integer.
     */
    private void pressMouse( String button ) {

        // Button 1 = left click
        // Button 3 = Right click
        // Button 2 = Scroll wheel click
        try {
            // The event to send
            int event = 1024;
            switch ( Integer.valueOf( button ) ) {
                case 1:
                    event = InputEvent.BUTTON1_DOWN_MASK;
                    break;
                case 2:
                    event = InputEvent.BUTTON3_DOWN_MASK;
                    break;
                case 3:
                    event = InputEvent.BUTTON2_DOWN_MASK;
                    break;
            }

            r.mousePress( event );
            r.delay( 4 );
            r.mouseRelease( event );
        } catch ( Exception e ) {
            throwError( "Error pressing mouse: " + e.getMessage() );
        }
    }

    /**
     * Presses and releases a key.
     *
     * @param button The keycode of the button as String. Converted to integer.
     */
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

    /**
     * Type any sentence specified. Calls pressKey() to handle the actual presses.
     *
     * @param toType the String to type.
     */
    private void type( String toType ) {
        for ( char c : toType.toCharArray() ) {
            pressKey( String.valueOf( c ) );
        }
    }

    /**
     * Runs a program or script. You'd use this as if you were in command line, hence why
     * gedit or notepad works.
     *
     * @param path The path to the executable as String
     */
    private void run( String path ) {
        try {
            Runtime.getRuntime().exec( path );
        } catch ( Exception e ) {
            throwError( "Error running program: " + e.getMessage() );
        }
    }

    /**
     * Delays the macro's execution for a certain amount of milliseconds.
     * Handy when waiting for something to load
     *
     * @param time The time in milliseconds to wait
     */
    private void delay( String time ) {
        try {
            r.delay( Integer.valueOf( time ) );
        } catch ( Exception e ) {
            throwError( "Error delaying exec: " + e.getMessage() );
        }
    }

    /**
     * A helper function to cleanly throw an error either in the switch block
     * or in any other functions, since they all throw an error.
     *
     * @param mes The message of the error as String.
     */
    private void throwError( String mes ) {
        Out.printError( classId, mes );
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append( getMacroName() );
        sb.append( " events:\n"  );

        for ( String s: events ){
            sb.append( s );
            sb.append( "\n" );
        }
        return ( sb.toString() );
    }
}
