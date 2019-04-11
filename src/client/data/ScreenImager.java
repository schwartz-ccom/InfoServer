package client.data;

import res.Out;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScreenImager {

    public static void sendScreenshotToServer(){
        try {
            Robot r = new Robot();

            // Temporarily save it to a file
            // LINUX. ONLY.
            File toSave = new File ( System.getProperty( "user.home" ) + "/img.png" );

            Rectangle cap = new Rectangle( Toolkit.getDefaultToolkit().getScreenSize() );

            BufferedImage bi = r.createScreenCapture( cap );
            ImageIO.write( bi, "png", toSave );

            Out.printInfo( "ScreenImager", "Saved image!" );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
