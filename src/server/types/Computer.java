package server.types;

import res.Out;
import server.data.DataHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * On the UI, this is the component that shows the desktop of the computer
 * as well as the computer's name.
 * Also handles the actual data for the computer
 */
public class Computer extends JLabel {

    // Declare the two borders to use when selecting / un-selecting
    private Border unselectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(),
            BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );

    private Border selectedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );

    // Formatting variables
    private Dimension compIconSize = new Dimension( 256, 144 );

    // Data variables for the computer

    private String compName;
    private String compIp;
    private String[] details;

    // Screen sizing
    private Dimension scrSize;
    private Dimension sixteennine = new Dimension( 1280, 720 );
    private Dimension sixteenten = new Dimension( 1280, 800 );

    // Construct the JLabel for the Computer
    public Computer( String name, String IP ) {
        // Talk to the super class first
        super();

        // Give some basic data first
        name = name.substring( 0, 1 ).toUpperCase() + name.substring( 1 ).toLowerCase();

        this.compName = name;
        this.compIp = IP;

        this.details = new String[ 13 ];
        details[ 0 ] = "NO";
        details[ 2 ] = this.compName;

        // Get the imageIcon for the computer
        Image defIcon = null;
        try {
            defIcon = ImageIO.read( getClass().getResource( "../../server/resources/images/defIcon.png" ) );
        } catch ( IOException e ) {
            Out.printError( getClass().getSimpleName(), "Error getting resource: " + e.getMessage() );
        }
        Icon scaled = null;
        if ( defIcon != null ) {
            scaled = new ImageIcon(
                    defIcon.getScaledInstance(
                            compIconSize.width,
                            compIconSize.height,
                            Image.SCALE_SMOOTH )
            );
        }

        // Give it a size
        this.setMinimumSize( compIconSize );

        // Set properties of this JLabel
        this.setIcon( scaled );
        this.setText( compName );
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

    public String getIP() {
        return this.compIp;
    }

    public void setImage( Icon i ) {
        setIcon( i );
    }

    public void setDetails( String[] s ) {
        this.details = s;
        this.compName = details[ 2 ];

        this.scrSize = new Dimension( Integer.valueOf( details[ 13 ] ), Integer.valueOf( details[ 14 ] ) );

        this.setText( compName );
    }

    public String[] getDetails() {
        return this.details;
    }

    public Dimension getScreenSize(){
        return this.scrSize;
    }
    public Dimension getReducedScreenSize(){

        // If the screen is 16:9
        if ( scrSize.width / scrSize.height == ( 16 / 9 ) ) {
            Out.printInfo( getClass().getSimpleName(), "Aspect Ratio: 16:9" );
            return sixteennine;
        }
        else {
            Out.printInfo( getClass().getSimpleName(), "Aspect Ratio: 16:10" );
            return sixteenten;
        }
    }

    public String getComputerName() {
        return this.compName;
    }

    public void setDataComputer() {
        // Test whether or not we already are the target
        if ( DataHandler.getInstance().getCurrentComputer() != this )
            DataHandler.getInstance().setCurrentComputer( this );
    }

    // Purely display methods. Just puts a border around the currently selected computer.
    // Both called by DataHandler. These belong here since this class handles the UI look
    // as well as Data
    public void select() {
        this.setBorder( selectedBorder );
    }

    public void unselect() {
        this.setBorder( unselectedBorder );
    }

    @Override
    public String toString() {
        return getComputerName();
    }
}
