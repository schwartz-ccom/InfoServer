package client.data;

import res.Out;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles taking a screenshot and returning an image
 */
public class ScreenImager {

    private Robot r;
    private String classId = this.getClass().getSimpleName();
    private Dimension compIconSize = new Dimension( 256, 144 );

    public ScreenImager() {
        try {
            r = new Robot();
        } catch ( AWTException ae ) {
            Out.printError( classId, "Could not create robot: " + ae.getMessage() );
        }
    }

    public Icon getScreenshot() {
        // Get the screen dimensions
        Rectangle cap = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() );

        // Store it in a bufferedImage.
        BufferedImage ri = r.createScreenCapture( cap );
        if ( ri == null )
            Out.printError( "ScreenImager", "However, it was NULL" );

        Icon scaled = null;
        if ( ri != null ) {
            scaled = new ImageIcon(
                    ri.getScaledInstance(
                            compIconSize.width,
                            compIconSize.height,
                            Image.SCALE_SMOOTH )
            );
        }

        Out.printInfo( classId, "Succesfully scaled screenshot. Sending..." );
        return scaled;
    }
}
