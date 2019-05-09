package client.data;

import res.Out;
import server.data.macro.Macro;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles storage of Macros / Computer data
 */
public class DataRepository {

    // Get computer details initially.
    private HashMap< String, String > data;

    // Create a mapped list for the Macros.
    private Map< String, Macro > loadedMacros = new HashMap<>();

    private static DataRepository instance;

    public static DataRepository getInstance() {
        if ( instance == null )
            instance = new DataRepository();
        return instance;
    }

    private DataRepository() {

    }

    public void loadMacro( Macro toLoad ) {
        loadedMacros.put( toLoad.getMacroName(), toLoad );
        Out.printInfo( getClass().getSimpleName(), "I have a new macro: " + toLoad.getMacroName() );
    }
    public void runMacro( String nameToRun ) {
        loadedMacros.get( nameToRun ).runMacro();
    }
    public void unloadMacro( String macro ) {
        // Remove macro via name
        loadedMacros.remove( macro );
    }
    public String getLoadedMacros(){
        StringBuilder toRet = new StringBuilder();

        for ( Macro m: loadedMacros.values() ){
            toRet.append( m.getMacroName() );
            toRet.append( "," );
        }
        return toRet.toString();
    }
}
