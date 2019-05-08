package server.data;

import server.network.NetworkHandler;
import server.network.info.Message;
import server.resources.ComputerSubscriber;
import server.ui.components.ComputerList;

import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    // The current computer with information
    private Computer currentComputer = null;

    // The master list of computers
    private List< Computer > allComputers;

    // The list of classes that are dependent on the current Computer
    private List< ComputerSubscriber > subscribers;

    private static DataHandler instance;
    public static DataHandler getInstance(){
        if ( instance == null )
            instance = new DataHandler();
        return instance;
    }

    private DataHandler(){
        subscribers = new ArrayList<>();
        allComputers = new ArrayList<>();
    }

    void setCurrentComputer( Computer c ){
        // Unselect the current computer, and then select the new computer
        if ( currentComputer != null ) {
            currentComputer.unselect();
            if ( NetworkHandler.getInstance().isConnectedToComputer() )
                NetworkHandler.getInstance().sendCommand( new Message( "GOODBYE" ) );
        }
        this.currentComputer = c;
        currentComputer.select();

        // Alert any elements that are subscribed to this data
        alertSubscribers();
    }
    public Computer getCurrentComputer(){
        return this.currentComputer;
    }
    public void addComputer( Computer c ){
        allComputers.add( c );
        ComputerList.getInstance().addComputerToDisplay( c );
    }

    public Computer getComputer( String name ){
        for ( Computer c: allComputers ){
            if ( c.getComputerName().equalsIgnoreCase( name ) )
                return c;
        }
        return null;
    }
    public void subscribe( ComputerSubscriber sub ){
        subscribers.add( sub );
    }

    public void alertSubscribers(){

        // Go through the list of subscribers and tell them to update their data with
        // the current computer
        for ( ComputerSubscriber sub: subscribers )
            sub.updateComputer( currentComputer );
    }
}
