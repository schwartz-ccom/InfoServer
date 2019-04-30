package client.data;

import res.Out;

import java.awt.*;
import java.awt.image.RenderedImage;

/**
 * Handles taking a screenshot and returning an image
 */
public class ScreenImager {

    public static RenderedImage getScreenshot(){
        try {
            // Declare a robot to get the screenshot
            Robot r = new Robot();

            // Get the screen dimensions
            Rectangle cap = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() );

            // Render it.
            RenderedImage ri = r.createScreenCapture( cap );

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
