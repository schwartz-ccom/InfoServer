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

    static void saveMacrosToFile( File file, Macro[] m ){

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
            toExportAsXml.append( '\t' );
            toExportAsXml.append( "<END>" );
            toExportAsXml.append( "\n" );
        }
        toExportAsXml.append( "</MACROS>" );
        try {
            BufferedWriter out = new BufferedWriter( new PrintWriter( file ) );
            out.write( toExportAsXml.toString() );
            out.close();
        } catch( IOException fnfe ){
            Out.printError( "MacroFileHandler", "File writing issue: " + fnfe.getMessage() );
        }
    }

    static List< Macro > loadMacrosFromFile( File file ){
        BufferedReader in = null;
        try {
            in = new BufferedReader( new FileReader( file ) );
        } catch ( IOException ioe ){
            Out.printError( "MacroFileHandler", "Could not open file: " + ioe.getMessage() );
        }
        if ( in == null )
            return null;
        List< Macro > toReturn = new ArrayList<>();

        try {
            String line;
            line = in.readLine();
            if ( !line.startsWith( "DELTAORION" ) ) {
                JOptionPane.showMessageDialog( null, "Not a valid macro file!" );
                return null;
            }
            int macroCount = Integer.valueOf( line.substring( line.indexOf( "SIZE=" ) + 5, line.indexOf( ">" ) ).trim() );
            for ( int toCreate = 0; toCreate < macroCount; toCreate++ ){
                String name = in.readLine();
                name = name.substring( name.indexOf( ">" ) + 1, name.lastIndexOf( "<" ) );
                Macro temp = new Macro( name );

                String command;
                while ( !( command = in.readLine() ).contains( "<END>" ) ) {
                    temp.addAction( command.substring( command.indexOf( "<STEP>" ) + 6, command.indexOf( "</STEP>" ) ) );
                }
                toReturn.add( temp );
            }
        } catch ( IOException ioe ){
            Out.printError( "MacroFileHandler", "There was an issue: " + ioe.getMessage() );
        }
        // Temporary
        return toReturn;
    }
}
