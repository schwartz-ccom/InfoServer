package server.data.macro;

import res.Out;

import java.io.*;

/**
 * Handles saving and loading of Macros from a .xml file
 */
class MacroFileHandler {

    static void saveMacrosToFile( File file, Macro[] m ){

        StringBuilder toExportAsXml = new StringBuilder();
        toExportAsXml.append( "<MACROS>" );
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
        }
        toExportAsXml.append( "</MACROS>" );
        try {
            BufferedWriter out = new BufferedWriter( new PrintWriter( file ) );
            out.write( toExportAsXml.toString() );
            out.close();
        } catch( IOException fnfe ){
            Out.printError( "MacroFileHandler", "File writing issue: " + fnfe.getLocalizedMessage() );
        }
    }
}
