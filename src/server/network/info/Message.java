package server.network.info;

import server.data.macro.Macro;

import javax.swing.*;
import java.io.Serializable;

/**
 * Data object sent over the socket, since I can't figure out an issue using multiple streams,
 * and I think this will just be more readable anyways.
 */
public class Message implements Serializable {

    // The data objects I'm more concerned about.
    private String[] info = null;
    //private byte[] img = null;
    private Icon img = null;
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
    public void setInfo( String[] details ){
        this.info = details;
    }
    public void setImg( Icon im ){
        this.img = im;
    }
    public void setSecondayCommand( String newCommand ){
        this.secondayCommand = newCommand;
    }
    public void setMacro( Macro m ){
        this.macro = m;
    }

    // Getters for data vars
    public String[] getInfo(){
        return this.info;
    }
    public Icon getImg(){
        return this.img;
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
