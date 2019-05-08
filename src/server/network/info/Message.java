package server.network.info;

import res.Out;
import server.data.macro.Macro;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Data object sent over the socket, since I can't figure out an issue using multiple streams,
 * and I think this will just be more readable anyways.
 */
public class Message implements Serializable {

    // The data objects I'm more concerned about.
    private HashMap< String, String > info = null;
    private byte[] img = null;
    private Macro macro = null;
    private String primaryCommand, secondayCommand = "";

    /**
     * Default constructor, must supply a command.
     * @param cmd the command
     */
    public Message( String cmd ){
        this.primaryCommand = cmd;
    }

    // Setters for data vars
    public void setInfo( HashMap< String, String > details ){
        this.info = details;
    }
    public void setImg( BufferedImage im ){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write( im, "png", baos );
        } catch ( IOException ioe ) {
            Out.printError( getClass().getSimpleName(), "Error writing byte array: " + ioe.getMessage() );
        }
        this.img = baos.toByteArray();
    }
    public void setSecondayCommand( String newCommand ){
        this.secondayCommand = newCommand;
    }
    public void setMacro( Macro m ){
        this.macro = m;
    }

    // Getters for data vars
    public HashMap< String, String > getInfo(){
        return this.info;
    }
    public RenderedImage getImg(){
        ByteArrayInputStream bais = new ByteArrayInputStream( img );
        RenderedImage ri = null;
        try {
            ri = ImageIO.read( bais );
        } catch ( IOException ioe ){
            Out.printError( getClass().getSimpleName(), "Error reading byte array: " + ioe.getMessage() );
        }
        return ri;
    }
    public String getPrimaryCommand(){
        return this.primaryCommand;
    }
    public String getSecondaryCommand(){
        return this.secondayCommand;
    }
    public Macro getMacro(){
        return this.macro;
    }

    // Normal stuff
    public String toString(){
        return "MESSAGE [ " + getPrimaryCommand() + " ]";
    }
}
