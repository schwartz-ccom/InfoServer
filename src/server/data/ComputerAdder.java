package server.data;

import res.Out;
import server.ui.App;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Adds a quick way to add a computer, instead of going through menu items
 */
public class ComputerAdder extends JLabel {

    private Border defaultBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(),
            BorderFactory.createEmptyBorder( 4, 4, 4, 4 ) );

    /**
     * Constructor creates a JLabel that is similar to a Computer JLabel,
     * but has a plus on it and can't be selected
     */
    public ComputerAdder(){
        // Call the super JLabel class for setup.
        super();

        // Define the icon size
        Dimension compIconSize = new Dimension( 256, 144 );

        // Get the imageIcon for the computer
        Image defIcon = null;
        try {
            defIcon = ImageIO.read( getClass().getResource( "../../server/resources/images/defAdd.png" ) );
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
        this.setText( "Add a computer" );
        this.setBorder( defaultBorder );

        // Set text alignment properties
        this.setHorizontalTextPosition( JLabel.CENTER );
        this.setVerticalTextPosition( JLabel.BOTTOM );

        // Give it an ActionEvent when it's clicked.
        // Tell DataHandler that this is the current Computer to manage
        this.addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                // If left click
                if ( e.getButton() == 1 )
                    App.getInstance().showAddComputerPane();
            }
        } );
    }
}
