package client.data;

import server.data.macro.Macro;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles storage of Macros data
 */
public class MacroRepository {

    // Create a mapped list for the Macros.
    private Map< String, Macro > loadedMacros = new HashMap<>();

    private static MacroRepository instance;

    public static MacroRepository getInstance() {
        if ( instance == null )
            instance = new MacroRepository();
        return instance;
    }

    private MacroRepository() {

    }

    public void loadMacro( Macro toLoad ) {
        loadedMacros.put( toLoad.getMacroName(), toLoad );
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
