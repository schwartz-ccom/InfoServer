package server.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * On the UI, this is the component that shows the desktop of the computer
 * as well as the computer's name.
 */
class Computer extends JLabel {

    // Construct the JLabel for the Computer
    Computer( String name ) {
        // Talk to the super class first
        super();

        // Define the sizes of the components;
        Dimension compIconSize = new Dimension( 128, 148 );

        // Get the imageIcon for the computer
        ImageIcon defIcon = new ImageIcon( getClass().getResource( "../../../server/resources/images/defIcon.png" ) );

        // Give it a size
        this.setSize( compIconSize );

        // Set properties of this JLabel
        this.setIcon( defIcon );
        this.setText( name );

        // Set text alignment properties
        this.setHorizontalTextPosition( JLabel.CENTER );
        this.setVerticalTextPosition( JLabel.BOTTOM );

    }
}
