package server.ui.components.macrocomps;

import server.data.DataHandler;
import server.ui.components.macrocomps.events.MouseEventHandler;

import javax.swing.*;
import java.awt.*;

public class MacroEditorPane extends JFrame {


    public MacroEditorPane() {
        // Call super with the title of the frame
        super( "Macro Editor" );

        // Set up local formatting variables
        Dimension editorSize = new Dimension( 1240, 720 );

        // Frame settings
        setSize( editorSize );
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        setLayout( new BorderLayout( 4, 4 ) );
        setResizable( false );

        setVisible( true );

        // Initialize the mouse listener
        MouseEventHandler meh = new MouseEventHandler(
                this,
                DataHandler.getInstance().getCurrentComputer().getScreenSize()
        );

        // Attach our custom mouse handler
        addMouseListener( meh );
        addMouseMotionListener( meh );
    }
    public static void main( String [] args ){
        new MacroEditorPane();
    }
}
