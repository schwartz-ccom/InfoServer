package client.data;

import res.Out;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles taking a screenshot and returning an image
 */
public class ScreenImager {

    public static BufferedImage getScreenshot(){
        try {
            // Declare a robot to get the screenshot
            Robot r = new Robot();

            // Get the screen dimensions
            Rectangle cap = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() );

            // Store it in a bufferedImage.
            BufferedImage ri = r.createScreenCapture( cap );

            Out.printInfo( "ScreenImager", "Successfully captured screenshot" );
            if ( ri == null )
                Out.printError( "ScreenImager", "However, it was NULL" );
            return ri;

        } catch ( Exception e ) {
            Out.printError("ScreenImager", "Something happened" );
            e.printStackTrace();
        }
        return null;
    }
}
