package server.ui.components.macrocomps;

import server.data.DataHandler;
import server.ui.components.macrocomps.events.MouseEventHandler;

import javax.swing.*;
import java.awt.*;

public class MacroEditorPane extends JFrame {


    public MacroEditorPane() {
        // Call super with the title of the frame
        super( "Macro Editor" );

        Dimension editorSize;
        Dimension screenSizeForAdjustments;

        // Set up local formatting variables
        // This should only be null if we're running the macro pane on it's own for testing
        if ( DataHandler.getInstance().getCurrentComputer() == null ) {
            editorSize = new Dimension( 1280, 720 );
            screenSizeForAdjustments = new Dimension( 1920, 1080 );
        }
        else {
            editorSize = DataHandler.getInstance().getCurrentComputer().getReducedScreenSize();
            screenSizeForAdjustments = DataHandler.getInstance().getCurrentComputer().getScreenSize();
        }

        // Frame settings
        setSize( editorSize );
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        setLayout( new BorderLayout() );
        setResizable( false );

        // Make the JLabel to update the computer's image on.
        JLabel lblComputerScreen = new JLabel( "blargh" );
        add( lblComputerScreen, BorderLayout.CENTER );

        setVisible( true );

        // Initialize the mouse listener

        MouseEventHandler meh = new MouseEventHandler(
                lblComputerScreen,
                screenSizeForAdjustments
        );

        // Attach our custom mouse handler
        lblComputerScreen.addMouseListener( meh );
        lblComputerScreen.addMouseMotionListener( meh );
    }
    public static void main( String [] args ){
        new MacroEditorPane();
    }
}
