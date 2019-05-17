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

    // Image items
    private Dimension compIconSize;
    private Rectangle cap;

    /**
     * Constructor to set up the Robot
     */
    public ScreenImager() {
        try {
            r = new Robot();
        } catch ( AWTException ae ) {
            Out.printError( classId, "Could not create robot: " + ae.getMessage() );
        }

        compIconSize = new Dimension( 256, 144 );
        cap = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() );
    }

    /**
     * Uses the robot class to get a screen shot, and then scales it
     * @return the screenshot as a smooth scaled Icon
     */
    public Icon getScreenshot() {

        // Store it in a bufferedImage.
        BufferedImage ri = r.createScreenCapture( cap );
        if ( ri == null )
            Out.printError( classId, "Screen capture was null?" );

        Icon scaled = null;
        if ( ri != null ) {
            scaled = new ImageIcon(
                    ri.getScaledInstance(
                            compIconSize.width,
                            compIconSize.height,
                            Image.SCALE_SMOOTH )
            );
        }
        return scaled;
    }
}
