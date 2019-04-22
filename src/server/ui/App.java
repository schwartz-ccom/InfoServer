package server.ui;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.data.MousePositionHandler;
import server.data.macro.MacroHandler;
import server.network.NetworkHandler;
import server.data.macro.Macro;
import server.resources.MacroSubscriber;
import server.ui.components.ComputerList;
import server.ui.components.StatusBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private JMenuItem mniAddComputer;

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
        frm.setSize( new Dimension( 850, 600 ) );
        frm.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized( ComponentEvent e ) {
                // Tell the computer panel that we have resized to accommodate more info
                ComputerList.getInstance().updateFrameSize( frm.getWidth() );
            }
        } );
        // Create the JMenu bar
        // Only gives access to Macros as of right now.
        // When a macro is created, they will appear in the Macro menu
        JMenuBar mbMenu = new JMenuBar();

        // Create the general menu
        mnGeneral = new JMenu( "App" );
        mniAddComputer = new JMenuItem( "Add a Computer on the network" );
        mniSettings = new JMenuItem( "Settings" );

        mnGeneral.add( mniAddComputer );
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
        frm.add( ComputerList.getInstance(), BorderLayout.NORTH );

        // Create the information panel
        GridBagConstraints cs = new GridBagConstraints();
        JPanel pnlInfo = new JPanel( new GridBagLayout() );

        frm.add( pnlInfo, BorderLayout.CENTER );

        JTextField txtCommand = new JTextField( 50 );
        txtCommand.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ){
                    NetworkHandler.getInstance().alertCommSubscribers( txtCommand.getText() );
                }
            }
        } );
        pnlInfo.add( txtCommand );

        // Add event handlers
        mniAddComputer.addActionListener( actionEvent -> showAddComputerPane() );
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

    private void showAddComputerPane(){
        // Button labels
        Object[] btnLabels = { "Connect", "Cancel" };

        // Setting for server port
        JLabel lblName = new JLabel( "Computer Name: " );
        JTextField txtName = new JTextField();
        txtName.setText( "" );

        // Wrap up settings in Object[] container
        Object[] uiElements = { lblName, txtName };

        int status = JOptionPane.showOptionDialog(
                frm,
                uiElements,
                "Add a computer",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                btnLabels,
                btnLabels[ 0 ]
        );

        if ( status == 0 ) {
            String name = txtName.getText();
            if ( name.isEmpty() ){
                return;
            }
            try {
                String address = InetAddress.getByName( name ).getHostAddress();
                System.out.println( "Address of " + name + ": " + address );
                Computer c = new Computer( name, address );
                ComputerList.getInstance().addComputer( c );

            } catch ( UnknownHostException e ) {
                JOptionPane.showMessageDialog( frm, "Could not find hostname " + name );
            }
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

    /**
     * Kind of like a default constructor, just passes normal params.
     * This is used when initially creating a macro
     */
    private void showMacroPane() {
        showMacroPane( 0, "", null );
    }

    /**
     * Shows the macro creation pane from the MenuItem with bits filled in
     * Used when editing a macro
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

    /**
     * Shows what available commands there are when making / editing a macro
     */
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
