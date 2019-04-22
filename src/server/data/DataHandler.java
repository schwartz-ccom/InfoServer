package server.data;

import server.resources.ComputerSubscriber;

import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    // The current computer with information
    private Computer currentComputer;

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
    }

    void setCurrentComputer( Computer c ){

        // Unselect the current computer, and then select the new computer
        if ( currentComputer != null )
            currentComputer.unselect();
        this.currentComputer = c;
        currentComputer.select();

        // Alert any elements that are subscribed to this data
        alertSubscribers();
    }
    public void subscribe( ComputerSubscriber sub ){
        subscribers.add( sub );
    }

    private void alertSubscribers(){

        // Go through the list of subscribers and tell them to update their data with
        // the current computer
        for ( ComputerSubscriber sub: subscribers )
            sub.updateComputer( currentComputer );
    }

    public Computer getCurrentComputer(){
        return this.currentComputer;
    }
}
