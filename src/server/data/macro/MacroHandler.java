package server.data.macro;

import res.Out;
import server.resources.MacroSubscriber;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
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

    // Singleton, so only use one instance
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

    /**
     * Add a macro into the system
     * It will be accessible by the drop down menu
     * @param m The macro to add
     */
    public void addMacroToCollection( Macro m ) {
        if ( isInCollection( m.getMacroName() ) )
            changeMacroInCollection( m.getMacroName(), m.getMacroSteps() );
        else
            macros.add( m );

        // Alert any elements that are subscribed to this data
        alertSubscribers();
    }

    /**
     * When a macro is edited, it'll call this and change the steps to whatever.
     * Also allows for addition of steps.
     * @param name The macro's name
     * @param steps The array of steps to add / change
     */
    private void changeMacroInCollection( String name, String[] steps ) {
        for ( Macro m: macros ) {
            if ( m.getMacroName().equals( name ) ) {
                int p = 0;
                for ( String s : steps )
                    m.modifyStep( p++, s );
                alertSubscribers();
                return;
            }
        }
    }

    /**
     * A simple way to see if a macro is already in the collection
     * @param name The name to search for
     * @return Whether or not the macro was found
     */
    private boolean isInCollection( String name ){
        for ( Macro m: macros ){
            if ( m.getMacroName().equals( name ) )
                return true;
        }
        return false;
    }

    /**
     * Remove a macro from the system
     * The delete function when viewing a macro calls this
     * @param m The macro to annihilate
     */
    public void remove( Macro m ){
        macros.remove( m );
        alertSubscribers();
    }

    /**
     * Called by export, saves all the macros as custom .xml
     */
    public void saveAllToFile(){
        JFileChooser fc = new JFileChooser( System.getProperty( "user.home" ) );
        fc.setDialogType( JFileChooser.SAVE_DIALOG );

        int exitStatus = fc.showSaveDialog( null );
        if ( exitStatus == JFileChooser.APPROVE_OPTION ){

            Macro[] toSave = new Macro[ macros.size() ];

            for ( int place = 0; place < macros.size(); place ++ )
                toSave[ place ] = macros.get( place );

            MacroFileHandler.saveMacrosToFile( fc.getSelectedFile(), toSave );
        }
    }

    /**
     * Called by import, reads in the custom xml.
     * Note: This only loads in unique macros, as in if there's a macro
     * with the same name already loaded, it won't add the macro that's in the
     * file, even if they are very different. Naming macros something handy
     * is VERY important
     *
     * For the future I might allow it, but it'll just be confusing to look at. I dunno.
     */
    public void getMacrosFromFile(){
        JFileChooser fc = new JFileChooser( System.getProperty( "user.home" ) );
        fc.setDialogType( JFileChooser.OPEN_DIALOG );
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        fc.setFileFilter( new FileNameExtensionFilter( "XML", "xml" ) );

        int exitStatus = fc.showOpenDialog( null );
        if ( exitStatus == JFileChooser.APPROVE_OPTION ){

            List< Macro > macsFromFile = MacroFileHandler.loadMacrosFromFile( fc.getSelectedFile() );
            if ( macsFromFile != null ) {
                for ( Macro m: macsFromFile ) {
                    if ( !isInCollection( m.getMacroName() ) )
                        macros.add( m );
                }
                alertSubscribers();
            }
        }
    }

    /**
     * A way for Objects to add themselves to the list of subscribers that
     * receive instant updates regarding macros
     * @param sub The Object subscribing
     */
    public void subscribe( MacroSubscriber sub ) {
        subscribers.add( sub );
    }

    /**
     * The way for Objects to remove themselves from the list
     * @param sub The Object un-subscribing
     * @return A boolean confirmation
     */
    public boolean unsubscribe( MacroSubscriber sub ) {
        return subscribers.remove( sub );
    }

    /**
     * Called whenever a macro is edited / added / removed / changed in any way.
     * Alerts subscribers that SOMETHING is different.
     */
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
