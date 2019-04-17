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
    public void addMacroToCollection( Macro m ) {
        if ( isInCollection( m.getMacroName() ) )
            changeMacroInCollection( m.getMacroName(), m.getMacroSteps() );
        else
            macros.add( m );

        // Alert any elements that are subscribed to this data
        alertSubscribers();
    }

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

    private boolean isInCollection( String name ){
        for ( Macro m: macros ){
            if ( m.getMacroName().equals( name ) )
                return true;
        }
        return false;
    }

    public void remove( Macro m ){
        macros.remove( m );
        alertSubscribers();
    }

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
