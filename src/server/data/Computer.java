package server.data;

import res.Out;
import server.data.macro.Macro;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.HashMap;

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

    // comp[a-zA-Z] = Computer overall detail
    // compOs[a-zA-Z] = Computer OS details
    // compHard[a-zA-Z] = Computer Hardware details

    private String compName;
    private String compIp;
    private HashMap< String, String > details;

    // Construct the JLabel for the Computer
    public Computer( String name, String IP ) {
        // Talk to the super class first
        super();

        // Give some basic data first

        name = name.substring( 0,1 ).toUpperCase() + name.substring( 1 ).toLowerCase();

        this.compName = name;
        this.compIp = IP;

        this.details = new HashMap<>();
        details.put( "CNAME", this.compName );
        details.put( "CONNECTED?", "NO" );

        // Get the imageIcon for the computer
        Image defIcon = null;
        try {
            defIcon = ImageIO.read( getClass().getResource( "../../server/resources/images/defIcon.png" ) );
        } catch ( IOException e ){
            Out.printError( getClass().getSimpleName(), "Error getting resource: " + e.getMessage() );
        }
        Icon scaled = new ImageIcon(
                defIcon.getScaledInstance(
                        compIconSize.width,
                        compIconSize.height,
                        Image.SCALE_SMOOTH )
        );

        // Give it a size
        this.setMinimumSize( compIconSize );

        // Set properties of this JLabel
        this.setIcon( scaled );
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

    public String getIP() {
        return this.compIp;
    }

    /**
     * @param command The hot key action to run as macro
     */
    public void setHotkeyAction( Macro command ) {

    }

    public void setImage( RenderedImage i ){
        Image toScale = ( Image ) i;
        Image scaled = toScale.getScaledInstance( compIconSize.width, compIconSize.height, Image.SCALE_SMOOTH );
        setIcon( new ImageIcon( scaled ) );
    }

    public void setDetails( HashMap< String, String > s ){
        compName = s.get( "CNAME" );
        this.details = s;

        this.setText( compName );
    }

    public HashMap< String, String > getDetails(){
        return this.details;
    }

    public String getComputerName() {
        return this.compName;
    }

    public void setDataComputer() {
        // Test whether or not we already are the target
        if ( DataHandler.getInstance().getCurrentComputer() != this)
            DataHandler.getInstance().setCurrentComputer( this );
    }

    // Purely display methods. Just puts a border around the currently selected computer.
    // Both called by DataHandler. These belong here since this class handles the UI look
    // as well as Data
    void select() {
        this.setBorder( selectedBorder );
    }

    void unselect() {
        this.setBorder( unselectedBorder );
    }

    @Override
    public String toString() {
        return getComputerName();
    }
}
