package server.data.macro;

import java.util.ArrayList;
import java.util.List;

/**
 * Gives the user a way to play out events on a foreign ( non host ) computer
 * Commands are limited, but that's intentional for now, since I don't see
 * much use in expanding those.
 */
public class Macro {

    // Private things needed all over the class.
    private List< String > events;
    private String myName;

    /**
     * Constructor of the Macro.
     * Just initializes the list.
     */
    public Macro( String macroName ) {
        this.myName = macroName;
        events = new ArrayList<>();
    }

    /**
     * Run the macro in a separate thread. This allows
     * us to keep the macro events, but kill and restart
     * the actual execution
     */
    public void runMacro(){
        new MacroWorker( events ).start();
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
