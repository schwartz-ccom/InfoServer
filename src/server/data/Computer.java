package server.data;

import server.network.ConnectionHandler;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * On the UI, this is the component that shows the desktop of the computer
 * as well as the computer's name.
 * <p>
 * Also handles the actual data for the computer
 */
public class Computer extends JLabel {

    // Declare the two borders to use when selecting / un-selecting
    private Border unselectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(),
            BorderFactory.createEmptyBorder( 4,4,4,4 ) );

    private Border selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder( 2,2,2,2 ) );

    // Data variables for the computer
    private String name;
    private String ip;

    // Construct the JLabel for the Computer
    public Computer( String name, String IP ) {
        // Talk to the super class first
        super();

        // Give some basic data first
        this.name = name;
        this.ip = IP;

        // Define the sizes of the components;
        Dimension compIconSize = new Dimension( 128, 148 );

        // Get the imageIcon for the computer
        ImageIcon defIcon = new ImageIcon( getClass().getResource( "../../server/resources/images/defIcon.png" ) );

        // Give it a size
        this.setMinimumSize( compIconSize );

        // Set properties of this JLabel
        this.setIcon( defIcon );
        this.setText( name );
        this.setBorder( unselectedBorder );

        // Set text alignment properties
        this.setHorizontalTextPosition( JLabel.CENTER );
        this.setVerticalTextPosition( JLabel.BOTTOM );

        // Give it an ActionEvent when it's clicked.
        // Tell DataHandler that this is the current Computer to manage
        this.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                setDataComputer();
            }
        } );
    }

    public void updateInformation( Map< String, String > info ){

    }

    public String getIP(){
        return this.ip;
    }

    /**
     *
     * @param command The hot key action to run as string
     */
    public void setHotkeyAction( String command ){

    }

    public String getComputerName(){
        return this.name;
    }

    private void setDataComputer() {
        DataHandler.getInstance().setCurrentComputer( this );
    }

    // Purely display methods. Just puts a border around the currently selected computer.
    // Both called by DataHandler. These belong here since this class handles the UI look
    // as well as Data
    void select(){
        this.setBorder( selectedBorder );
    }
    void unselect(){
        this.setBorder( unselectedBorder );
    }

    @Override
    public String toString(){
        return getComputerName();
    }
}
