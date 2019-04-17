package server.data.macro;

import res.Out;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading of Macros from a .xml file
 */
class MacroFileHandler {

    /**
     * Handles saving of the macros to a file as made up .xml
     * @param file The location to save to
     * @param m The Macro list
     */
    static void saveMacrosToFile( File file, Macro[] m ){

        // Use a string builder since it's more efficient than appending
        // string variables
        // DELTAORION is just an error checking mechanism so you can't load
        // random .xml files
        StringBuilder toExportAsXml = new StringBuilder();
        toExportAsXml.append( "DELTAORION <MACROS SIZE=" );
        toExportAsXml.append( m.length );
        toExportAsXml.append( ">" );
        toExportAsXml.append( '\n' );

        for ( Macro mac: m ){
            // Append the name
            toExportAsXml.append( '\t' );
            toExportAsXml.append( "<NAME>" );
            toExportAsXml.append( mac.getMacroName() );
            toExportAsXml.append( "</NAME>" );
            toExportAsXml.append( '\n' );

            // Append each step tabbed out
            for ( String s: mac.getMacroSteps() ){
                toExportAsXml.append( '\t' );
                toExportAsXml.append( '\t' );
                toExportAsXml.append( "<STEP>" );
                toExportAsXml.append( s );
                toExportAsXml.append( "</STEP>" );
                toExportAsXml.append( '\n' );
            }

            // Write an end bit so when we load, we know when to stop
            toExportAsXml.append( '\t' );
            toExportAsXml.append( "<END>" );
            toExportAsXml.append( "\n" );
        }
        toExportAsXml.append( "</MACROS>" );

        // Then write the entire string to the file
        try {
            BufferedWriter out = new BufferedWriter( new PrintWriter( file ) );
            out.write( toExportAsXml.toString() );
            out.close();
        } catch( IOException fnfe ){
            Out.printError( "MacroFileHandler", "File writing issue: " + fnfe.getMessage() );
        }
    }

    /**
     * Handles loading the macros in from a file.
     * It get's the error checking part, and then it finds
     * how many macros are saved / to be loaded, and goes from there
     * @param file The file to read from
     * @return A list of macros
     */
    static List< Macro > loadMacrosFromFile( File file ){
        BufferedReader in = null;
        try {
            // Try to open the file.
            // This should work since we are using a JFileChooser, but that's
            // why we have error checking
            in = new BufferedReader( new FileReader( file ) );
        } catch ( IOException ioe ){
            Out.printError( "MacroFileHandler", "Could not open file: " + ioe.getMessage() );
        }

        // Error checking
        if ( in == null )
            return null;

        // Declare the list to use / return
        List< Macro > toReturn = new ArrayList<>();

        try {
            String line;

            // Read the first line and check the preamble
            line = in.readLine();
            if ( !line.startsWith( "DELTAORION" ) ) {
                JOptionPane.showMessageDialog( null, "Not a valid macro file!" );
                return null;
            }
            // Then find how many macros there are
            int macroCount = Integer.valueOf( line.substring( line.indexOf( "SIZE=" ) + 5, line.indexOf( ">" ) ).trim() );

            // And for each one, run through the steps and get the info
            for ( int toCreate = 0; toCreate < macroCount; toCreate++ ){
                String name = in.readLine();
                // Get the name
                name = name.substring( name.indexOf( ">" ) + 1, name.lastIndexOf( "<" ) );
                Macro temp = new Macro( name );

                // Get the commands
                String command;
                while ( !( command = in.readLine() ).contains( "<END>" ) ) {
                    temp.addAction( command.substring( command.indexOf( "<STEP>" ) + 6, command.indexOf( "</STEP>" ) ) );
                }
                toReturn.add( temp );
            }
        } catch ( IOException ioe ){
            Out.printError( "MacroFileHandler", "There was an issue: " + ioe.getMessage() );
        }
        // Return the list
        return toReturn;
    }
}
