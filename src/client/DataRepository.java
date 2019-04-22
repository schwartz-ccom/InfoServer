package client;

import client.data.Details;
import server.data.macro.Macro;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles storage of Macros / Computer data
 */
public class DataRepository {

    // Get computer details initially.
    private HashMap< String, String > data = Details.getDetails();

    // Create a mapped list for the Macros.
    private Map< String, Macro > loadedMacros = new HashMap<>();

    private static DataRepository instance;

    public static DataRepository getInstance(){
        if ( instance == null )
            instance = new DataRepository();
        return instance;
    }
    private DataRepository(){

    }
    public void loadMacro( Macro toLoad ){
        loadedMacros.put( toLoad.getMacroName(), toLoad );
    }
    public void runMacro( String nameToRun ){
        loadedMacros.get( nameToRun ).runMacro();
    }

    public HashMap<String, String > getData(){
        return data;
    }
    public void refreshData(){
        data = Details.getDetails();
    }
}
