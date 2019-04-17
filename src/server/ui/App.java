package server.ui;

import res.Out;
import server.data.MousePositionHandler;
import server.data.macro.MacroHandler;
import server.network.NetworkHandler;
import server.data.macro.Macro;
import server.resources.MacroSubscriber;
import server.ui.components.ComputerList;
import server.ui.components.StatusBar;

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
public class App implements MacroSubscriber {

    private JFrame frm, macroInfoFrame;

    // General Menu
    private JMenu mnGeneral;
    private JMenuItem mniSettings;

    // Macro Menu
    private JMenu mnMacro;
    private JMenuItem mniCreateMacro;
    private JMenuItem mniExport;
    private JMenuItem mniImport;

    // Connection Menu
    private JMenu mnConnection;
    private JMenuItem mniEnableServer;
    private JMenuItem mniConnectionDetails;

    private JComboBox< String > cbxMacros;
    private JLabel lblMousePosition;
    private static App instance;

    public static App getInstance() {
        if ( instance == null )
            instance = new App();
        return instance;
    }

    private App() {
        // Subscribe to both of the data senders
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

        // Create the general menu
        mnGeneral = new JMenu( "App" );
        mniSettings = new JMenuItem( "Settings" );

        mnGeneral.add( mniSettings );

        // Create the macro menu
        mnMacro = new JMenu( "Macros" );
        mniCreateMacro = new JMenuItem( "Create Macro" );
        mniImport = new JMenuItem( "Import Macros" );
        mniExport = new JMenuItem( "Export Macros" );

        // Create a mouse position follower so that it's easier to create macros
        lblMousePosition = new JLabel( "Current mouse x,y: 0, 0" );

        // Initiate a very important box
        cbxMacros = new JComboBox<>();
        cbxMacros.addItem( "Choose a Macro" );

        // Add all relevant items to the macro menu
        mnMacro.add( mniCreateMacro );
        mnMacro.add( mniImport );

        // Create the connection menu
        mnConnection = new JMenu( "Network" );
        mniEnableServer = new JMenuItem( "Enable Server" );
        mniConnectionDetails = new JMenuItem( "Server Status" );

        mnConnection.add( mniEnableServer );
        mnConnection.add( mniConnectionDetails );

        // Add the menus to the menu bar
        mbMenu.add( mnGeneral );
        mbMenu.add( mnMacro );
        mbMenu.add( mnConnection );

        // Create the scrollable / draggable computer interface
        frm.add( new ComputerList(), BorderLayout.NORTH );

        // Add event handlers
        mniSettings.addActionListener( actionEvent -> showSettingsPane() );
        mniCreateMacro.addActionListener( actionEvent -> showMacroPane() );
        mniExport.addActionListener( actionEvent -> MacroHandler.getInstance().saveAllToFile() );
        mniImport.addActionListener( actionEvent -> MacroHandler.getInstance().getMacrosFromFile() );
        mniEnableServer.addActionListener( actionEvent -> enableServer() );

        // Set the menu bar
        frm.setJMenuBar( mbMenu );

        // Set the status bar
        frm.add( new StatusBar(), BorderLayout.SOUTH );

        // Finally, show the frame
        frm.setVisible( true );
        frm.repaint();
        frm.revalidate();
    }

    private void enableServer() {
        if ( mniEnableServer.getText().equalsIgnoreCase( "Enable Server" ) ) {
            NetworkHandler.getInstance().activate();
            mniEnableServer.setText( "Disable Server" );
        }
        else {
            NetworkHandler.getInstance().deactivate();
            mniEnableServer.setText( "Enable Server" );
        }
    }

    private void showSettingsPane() {

        // Button labels
        Object[] btnLabels = { "Apply Settings", "Cancel" };

        // Setting for server port
        JLabel lblPort = new JLabel( "Server Port Number: " );
        JTextField txtPort = new JTextField();
        txtPort.setText( String.valueOf( NetworkHandler.getInstance().getPort() ) );

        // Wrap up settings in Object[] container
        Object[] uiElements = { lblPort, txtPort };

        int status = JOptionPane.showOptionDialog(
                frm,
                uiElements,
                "Settings",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                btnLabels,
                btnLabels[ 0 ]
        );

        if ( status == 0 ) {
            NetworkHandler.getInstance().setPort( Integer.valueOf( txtPort.getText() ) );
        }
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
