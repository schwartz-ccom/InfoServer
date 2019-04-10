package server;

import server.ui.App;

/**
 * Launch the server, and show the GUI.
 *
 * If this is the first time opening, refresh all computer stats
 * Else, let the JFrame in ui handle refreshing computers
 */
public class ServerLauncher {

    /**
     * Default constructor that does the launch
     */
    private ServerLauncher(){
        // Start the GUI
        App.getInstance();
    }

    public static void main( String [] args ){
        new ServerLauncher();
    }
}
