package server.data;

import res.Out;
import server.resources.MacroSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all of the macro related items, like telling the UI about all the macros
 * as well as giving data to the UI for the macro it wants
 */
public class MacroHandler {

    // The current list of macros
    private List< Macro > macros;

    // The list of classes that are dependent on the macros
    private List< MacroSubscriber > subscribers = new ArrayList<>();

    // Singleton, so only use one instace
    private static MacroHandler instance;

    public static MacroHandler getInstance() {
        if ( instance == null )
            instance = new MacroHandler();
        return instance;
    }

    private MacroHandler() {
        macros = new ArrayList<>();
    }
    public Macro getMacro( String name ){
        for ( Macro m: macros ){
            if( m.getMacroName().equals( name ) ) {
                Out.printInfo(getClass().getSimpleName(), "Found macro: " + m.getMacroName() );
                return m;
            }
        }
        return null;
    }
    public void addMacroToCollection( Macro m ) {

        macros.add( m );

        // Alert any elements that are subscribed to this data
        alertSubscribers();
    }

    public void changeMacroInCollection( String name, String[] steps ) {

        for ( int place = 0; place < macros.size(); place++ ){
            if ( macros.get( place ).getMacroName().equals( name ) ){
                int p = 0;
                for ( String s: steps ) {
                    macros.get( place ).modifyStep( p++, s );
                }
                alertSubscribers();
                return;
            }
        }

    }

    public void subscribe( MacroSubscriber sub ) {
        subscribers.add( sub );
    }

    public boolean unsubscribe( MacroSubscriber sub ) {
        return subscribers.remove( sub );
    }

    private void alertSubscribers() {

        // Go through the list of subscribers and tell them to update their data with
        // the list of macros
        Macro[] toSend = new Macro[ macros.size() ];
        for ( int x = 0; x < macros.size(); x++ ) {
            toSend[ x ] = macros.get( x );
        }
        for ( MacroSubscriber sub : subscribers )
            sub.updateMacros( toSend );
    }
}
