package server.ui;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.data.MousePositionHandler;
import server.data.macro.MacroHandler;
import server.resources.ComputerSubscriber;
import server.data.macro.Macro;
import server.resources.MacroSubscriber;
import server.ui.components.ComputerList;

import javax.management.remote.JMXConnectionNotification;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * App handles the user interface, as well as action events
 * This is set up as a singleton, because there should only
 * be on UI frame running / only one instance of the UI
 */
public class App implements ComputerSubscriber, MacroSubscriber {

    private JFrame frm, macroInfoFrame;
    private JMenu mnMacro;
    private JMenuItem mniCreateMacro;
    private JMenuItem mniExport;
    private JMenuItem mniImport;

    private JComboBox< String > cbxMacros;
    private JLabel lblName;
    private JLabel lblMousePosition;
    private static App instance;

    public static App getInstance() {
        if ( instance == null )
            instance = new App();
        return instance;
    }

    private App() {
        // Subscribe to both of the data senders
        DataHandler.getInstance().subscribe( this );
        MacroHandler.getInstance().subscribe( this );

        // Set the look and fell, and then create the UI
        setLook();
        createGUI();
    }

    private void setLook() {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception e ) {
            Out.printError( "App", "Could not set LookAndFeel: " + e.getMessage() );
        }
    }

    private void createGUI() {
        frm = new JFrame( "Info Server" );
        frm.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frm.setLayout( new BorderLayout( 4, 4 ) );
        frm.setSize( new Dimension( 800, 600 ) );

        // Create the JMenu bar
        // Only gives access to Macros as of right now.
        // When a macro is created, they will appear in the Macro menu
        JMenuBar mbMenu = new JMenuBar();

        mnMacro = new JMenu( "Macros" );
        mniCreateMacro = new JMenuItem( "Create Macro" );
        mniImport = new JMenuItem( "Import Macros" );
        mniExport = new JMenuItem( "Export Macros" );

        mnMacro.add( mniCreateMacro );
        mnMacro.add( mniImport );
        mbMenu.add( mnMacro );

        // Create a mouse position follower so that it's easier to create macros
        lblMousePosition = new JLabel( "Current mouse x,y: 0, 0" );

        // Initiate a very important box
        cbxMacros = new JComboBox<>();
        cbxMacros.addItem( "Choose a Macro" );

        // Create the scrollable / draggable computer interface
        frm.add( new ComputerList(), BorderLayout.NORTH );

        // Just a test of the ComputerSubscriber system
        // This will be replaced with an informational JPanel
        lblName = new JLabel( "Name of computer selected: " );
        frm.add( lblName, BorderLayout.CENTER );

        // Add event handlers
        mniCreateMacro.addActionListener( actionEvent -> showMacroPane() );
        mniExport.addActionListener( actionEvent -> MacroHandler.getInstance().saveAllToFile() );
        mniImport.addActionListener( actionEvent -> MacroHandler.getInstance().getMacrosFromFile() );

        frm.setJMenuBar( mbMenu );

        // Finally, show the frame
        frm.setVisible( true );
        frm.repaint();
        frm.revalidate();
    }

    private void showMacroPane() {
        showMacroPane( 0, "", null );
    }

    /**
     * Shows the macro creation pane from the MenuItem with bits filled in
     */
    private void showMacroPane( int mode, String title, String[] body ) {

        // Formatting variable
        int maxWidth = 100;

        // Create the button labels for the JOptionPane
        Object[] optPaneBtnLabels = { "Create New Macro", "Cancel" };

        // Create the label for the name box
        JLabel lblName = new JLabel( "Macro Name: " );

        // Create the text area to put a name in
        JTextField txtName = new JTextField( title );
        txtName.setToolTipText( "Enter a name for the macro here." );
        txtName.setPreferredSize( new Dimension( maxWidth, 30 ) );

        // Create the JLabel to tell what the field is
        JLabel lblEditor = new JLabel( "Macro Commands: " );

        // Create the actual area to edit the macro
        JTextArea txtMacroEditor = new JTextArea();
        txtMacroEditor.setLineWrap( true );
        txtMacroEditor.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                if ( e.isControlDown() && e.isShiftDown()) {
                    Point toInsert = MousePositionHandler.getMouseLoc();
                    txtMacroEditor.append( "MOUSE MOVE " + toInsert.x + " " + toInsert.y + "\n" );
                }
            }
        } );
        txtMacroEditor.setWrapStyleWord( true );

        // Fill the edit macro area with whatever was passed
        if ( body != null ) {
            for ( String step : body ) {
                txtMacroEditor.append( step );
                txtMacroEditor.append( "\n" );
            }
        }

        // and then encapsulate it in a ScrollPane
        JScrollPane sp = new JScrollPane( txtMacroEditor );
        sp.setBorder( BorderFactory.createEtchedBorder() );
        sp.setPreferredSize( new Dimension( maxWidth, 200 ) );
        sp.createVerticalScrollBar();

        // Create the button that shows the style sheet
        JButton btnShowCommands = new JButton( "Show Macro Commands" );
        btnShowCommands.addActionListener( actionEvent -> showMacroHelp() );

        // Wrap them all up into an object
        Object[] uiElements = { lblName, txtName, lblEditor, sp, lblMousePosition, btnShowCommands };

        // Of course, gotta have the correct title on the object to not confuse anyone
        String frmTitle = "Create a macro";
        if ( mode == 1 ) {
            txtName.setToolTipText( "Writing a different name will make a copy of this macro!" );
            frmTitle = "Edit macro";
            optPaneBtnLabels[ 0 ] = "Save Edited Macro";
        }

        MousePositionHandler.startTracker();

        int status = JOptionPane.showOptionDialog(
                frm,
                uiElements,
                frmTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                optPaneBtnLabels,
                optPaneBtnLabels[ 0 ]
        );

        if ( macroInfoFrame != null )
            macroInfoFrame.dispose();

        MousePositionHandler.stopTracker();

        if ( status == 0 ) {
            if ( !txtName.getText().isEmpty() && !txtMacroEditor.getText().isEmpty() ) {
                // Create the Macro from the information given
                Macro newMacro = new Macro( txtName.getText() );

                // Add the commands to the macro. Each new line specifies a new command
                String[] cmds = txtMacroEditor.getText().split( "\n" );
                for ( String s : cmds )
                    newMacro.addAction( s );
                // Then add it.
                MacroHandler.getInstance().addMacroToCollection( newMacro );
            }
            else
                JOptionPane.showMessageDialog( frm, "Macro details ( Name / Events ) was empty. Please retry." );
        }
    }

    private void showMacroHelp() {
        String help = "MOUSE MOVE [ x_coordinate ] [ y_coordinate ]\n" +
                "MOUSE PRESS [ mouse_button ]\n" +
                "KEY PRESS [ key_code ]\n" +
                "TYPE [ string_to_type ]\n" +
                "RUN [ path_as_string ]\n" +
                "DELAY [ time_in_milliseconds ]\n" +
                "REPEAT [ times_to_repeat_macro ]\n\n" +
                "CTRL + SHIFT will insert a MOUSE MOVE x y\n\n" +
                "Note: These are in caps, but the commands are not\n" +
                "case sensitive. That being said, you should probably adopt\n" +
                "a style and either go full upper or lower case.";

        macroInfoFrame = new JFrame( "Help" );
        macroInfoFrame.setLocation( frm.getX() + frm.getWidth() + 30, frm.getY() );

        JTextArea e = new JTextArea( help );
        e.setPreferredSize( new Dimension( 400, 200 ) );

        macroInfoFrame.add( e );
        macroInfoFrame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        macroInfoFrame.pack();
        macroInfoFrame.setVisible( true );
    }

    /**
     * getUI
     * If another part of the app needs the UI, they can use this.
     *
     * @return the UI JFrame
     */
    public JFrame getUI() {
        return Objects.requireNonNullElseGet( frm, JFrame::new );
    }

    /**
     * Called when the selected computer is changed.
     *
     * @param data The Computer
     */
    @Override
    public void updateComputer( Computer data ) {
        lblName.setText( "Name of computer selected: " + data );
    }

    /**
     * Called when a new Macro is added
     *
     * @param macros The list of Macros
     */
    @Override
    public void updateMacros( Macro[] macros ) {
        mnMacro.removeAll();
        mnMacro.add( mniCreateMacro );
        mnMacro.add( mniImport );
        mnMacro.add( mniExport );
        mnMacro.addSeparator();

        cbxMacros.removeAllItems();
        cbxMacros.addItem( "Choose a Macro" );

        for ( Macro m : macros ) {
            JMenuItem temp = new JMenuItem( m.getMacroName() );
            temp.addActionListener( actionEvent -> {
                Object[] btnLabels = { "Run", "Edit", "Delete", "Close" };

                int choice = JOptionPane.showOptionDialog(
                        frm,
                        m.toString(),
                        "View Macro",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        btnLabels,
                        btnLabels[ 0 ]
                );
                if ( choice == 0 )
                    m.runMacro();
                else if ( choice == 1 )
                    showMacroPane( 1, m.getMacroName(), m.getMacroSteps() );
                else if ( choice == 2 )
                    MacroHandler.getInstance().remove( m );
            } );
            mnMacro.add( temp );
            cbxMacros.addItem( m.getMacroName() );
        }
    }

    public void updateMousePosition( int x, int y ) {
        lblMousePosition.setText( "Current mouse x,y: " + x + ", " + y );
        lblMousePosition.repaint();
    }
}
