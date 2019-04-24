package server.ui;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.data.MousePositionHandler;
import server.data.macro.MacroHandler;
import server.network.NetworkHandler;
import server.data.macro.Macro;
import server.resources.ComputerSubscriber;
import server.resources.MacroSubscriber;
import server.ui.components.ComputerList;
import server.ui.components.ConnectButton;
import server.ui.components.StatusBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * App handles the user interface, as well as action events
 * This is set up as a singleton, because there should only
 * be on UI frame running / only one instance of the UI
 */
public class App implements MacroSubscriber, ComputerSubscriber {

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
    private JMenuItem mniConnectionDetails;

    // Information pane elements
    private JPanel pnlInfo;
    private JPanel pnlMacro;
    private JLabel lblHasClientInstalled;
    private JLabel lblCompNameDisp;
    private JLabel lblOSInfoDisp;
    private JLabel lblUserNameDisp;
    private JLabel lblCPUUsageDisp;
    private JLabel lblMemUsageDisp;
    private JLabel lblDiskUsageDisp;
    private JLabel lblLastUpdateDisp;
    private DefaultListModel< String > modelAllMacros, modelLoadedMacros;

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
        DataHandler.getInstance().subscribe( this );

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
        frm = new JFrame( "Info Server - CONFIDENTIAL MARK 2 - WISCONSIN" );
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
        mniConnectionDetails = new JMenuItem( "Server Status" );

        mnConnection.add( mniConnectionDetails );

        // Add the menus to the menu bar
        mbMenu.add( mnGeneral );
        mbMenu.add( mnMacro );
        mbMenu.add( mnConnection );

        // Create the scrollable / draggable computer interface
        frm.add( ComputerList.getInstance(), BorderLayout.NORTH );

        // Create the information panel
        pnlInfo = makeInfoPanel();
        pnlInfo.setEnabled( false );
        frm.add( pnlInfo, BorderLayout.WEST );

        pnlMacro = makeMacroSelectPanel();
        pnlMacro.setEnabled( false );
        frm.add( pnlMacro, BorderLayout.CENTER );

        // Add event handlers
        mniAddComputer.addActionListener( actionEvent -> showAddComputerPane() );
        mniSettings.addActionListener( actionEvent -> showSettingsPane() );
        mniCreateMacro.addActionListener( actionEvent -> showMacroPane() );
        mniExport.addActionListener( actionEvent -> MacroHandler.getInstance().saveAllToFile() );
        mniImport.addActionListener( actionEvent -> MacroHandler.getInstance().getMacrosFromFile() );

        // Set the menu bar
        frm.setJMenuBar( mbMenu );

        // Set the status bar
        frm.add( new StatusBar(), BorderLayout.SOUTH );

        // Finally, show the frame
        frm.setVisible( true );
        frm.repaint();
        frm.revalidate();
    }

    private JPanel makeInfoPanel(){
        GridBagConstraints cs = new GridBagConstraints();
        cs.insets = new Insets( 4, 4, 4, 4 );
        JPanel pnlInfo = new JPanel( new GridBagLayout() );
        pnlInfo.setBorder( BorderFactory.createTitledBorder( "Computer Info" ) );

        // Create all the labels
        JLabel lblCompName = new JLabel( "Computer Name: " );
        cs.gridx = 0;
        cs.gridy = 0;
        pnlInfo.add( lblCompName, cs );

        JLabel lblHasClient = new JLabel( "Is reachable: " );
        cs.gridy = 1;
        pnlInfo.add( lblHasClient, cs );

        // Displays all OS info on one line
        JLabel lblOSInfo = new JLabel( "OS Info: " );
        cs.gridy = 2;
        pnlInfo.add( lblOSInfo, cs );

        JLabel lblUserName = new JLabel( "Current User: " );
        cs.gridy = 3;
        pnlInfo.add( lblUserName, cs );

        // Shows all CPU stats on one line
        JLabel lblCPUUsage = new JLabel( "CPU Usage: " );
        cs.gridy = 4;
        pnlInfo.add( lblCPUUsage, cs );

        // Shows all Mem stats on one line
        JLabel lblMemUsage = new JLabel( "Memory Usage: " );
        cs.gridy = 5;
        pnlInfo.add( lblMemUsage, cs );

        JLabel lblDiskUsage = new JLabel( "C:\\ Disk Usage: " );
        cs.gridy = 6;
        pnlInfo.add( lblDiskUsage, cs );

        JLabel lblLastUpdate = new JLabel( "Updated at: " );
        cs.gridy = 7;
        pnlInfo.add( lblLastUpdate, cs );

        // Initialize the elements that display the info. If no info is known, "UNKNOWN" will be displayed

        lblCompNameDisp = new JLabel( "No Computer Selected");
        cs.gridx = 1;
        cs.gridy = 0;
        pnlInfo.add( lblCompNameDisp, cs );

        lblHasClientInstalled = new JLabel( "Unknown" );
        cs.gridy = 1;
        pnlInfo.add( lblHasClientInstalled, cs );

        // Displays all OS info on one line
        lblOSInfoDisp = new JLabel( "Unknown" );
        cs.gridy = 2;
        pnlInfo.add( lblOSInfoDisp, cs );

        lblUserNameDisp = new JLabel( "Unknown" );
        cs.gridy = 3;
        pnlInfo.add( lblUserNameDisp, cs );

        // Shows all CPU stats on one line
        lblCPUUsageDisp = new JLabel( "Unknown" );
        cs.gridy = 4;
        pnlInfo.add( lblCPUUsageDisp, cs );

        // Shows all Mem stats on one line
        lblMemUsageDisp = new JLabel( "Unknown" );
        cs.gridy = 5;
        pnlInfo.add( lblMemUsageDisp, cs );

        lblDiskUsageDisp = new JLabel( "Unknown" );
        cs.gridy = 6;
        pnlInfo.add( lblDiskUsageDisp, cs );

        lblLastUpdateDisp = new JLabel( "Never updated" );
        cs.gridy = 7;
        pnlInfo.add( lblLastUpdateDisp, cs );

        // Now add all the buttons to send commands with
        // Refresh - Updates info / icon
        // Play Macro - Runs loaded macro
        // Run Command - Runs a typed command on command line
        // Restart - Confirms, and then restarts.

        cs.gridy = 8;
        cs.gridx = 0;

        Dimension dimBtnPreferred = new Dimension( 180, 30 );

        ConnectButton btnRefresh = new ConnectButton( "Connect" );
        btnRefresh.setPreferredSize( dimBtnPreferred );
        pnlInfo.add( btnRefresh, cs );

        JButton btnMacro = new JButton( "Macros" );
        btnMacro.setPreferredSize( dimBtnPreferred );
        cs.gridx = 1;
        pnlInfo.add( btnMacro, cs );

        JButton btnRunCmd = new JButton( "Run Command" );
        btnRunCmd.setPreferredSize( dimBtnPreferred );
        btnRunCmd.addActionListener( actionEvent -> {
            String cmd = JOptionPane.showInputDialog( "Command to run on remote machine " );
            if ( cmd != null )
                NetworkHandler.getInstance().sendCommand( "RUN " + cmd );
        } );
        cs.gridy = 9;
        cs.gridx = 0;
        pnlInfo.add( btnRunCmd, cs );

        JButton btnMore = new JButton( "More Commands" );
        btnMore.setPreferredSize( dimBtnPreferred );
        cs.gridx = 1;
        pnlInfo.add( btnMore, cs );

        pnlInfo.setMinimumSize( new Dimension( frm.getWidth() / 2, 300 ) );

        return pnlInfo;
    }

    private JPanel makeMacroSelectPanel(){

        JPanel pnlMacro = new JPanel( new GridBagLayout() );
        pnlMacro.setBorder( BorderFactory.createTitledBorder( "Macro Select" ) );

        GridBagConstraints cs = new GridBagConstraints();
        cs.insets = new Insets( 4, 4, 4, 4 );

        modelAllMacros = new DefaultListModel<>();
        modelLoadedMacros = new DefaultListModel<>();

        Dimension dimListSize = new Dimension( 100, 120 );

        // All macros
        JList< String > listAllMacros = new JList<>( modelAllMacros );
        JScrollPane spAll = new JScrollPane( listAllMacros );
        spAll.setSize( dimListSize );
        pnlMacro.add( spAll, cs );

        // Button to transfer them
        JButton btnSendMacro = new JButton( ">>" );
        cs.gridx = 3;
        cs.gridy = 0;
        pnlMacro.add( btnSendMacro, cs );

        JButton btnTakeMacro = new JButton( "<<" );
        cs.gridy = 1;
        pnlMacro.add( btnTakeMacro, cs );

        // Client's loaded macros
        JList< String > listLoadedMacros = new JList<>( modelLoadedMacros );
        cs.gridx = 4;
        cs.gridy = 0;
        JScrollPane spLoaded = new JScrollPane( listLoadedMacros );
        spLoaded.setSize( dimListSize );
        pnlMacro.add( spLoaded, cs );

        return pnlMacro;
    }

    private void showAddComputerPane() {
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
            if ( name.isEmpty() ) {
                return;
            }
            try {
                String address = InetAddress.getByName( name ).getHostAddress();
                System.out.println( "Address of " + name + ": " + address );
                Computer c = new Computer( name, address );
                DataHandler.getInstance().addComputer( c );

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
                if ( e.isControlDown() && e.isShiftDown() ) {
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

            // Update Macro menu
            mnMacro.add( temp );

            // Update ComboBox menu in edit
            cbxMacros.addItem( m.getMacroName() );

            // Update all Macros list
            modelAllMacros.addElement( m.getMacroName() );
        }
    }

    public void updateMousePosition( int x, int y ) {
        lblMousePosition.setText( "Current mouse x,y: " + x + ", " + y );
        lblMousePosition.repaint();
    }

    @Override
    public void updateComputer( Computer data ) {

        // Enable the info panel if it wasn't already
        if ( !pnlInfo.isEnabled() ) {
            pnlInfo.setEnabled( true );
            pnlMacro.setEnabled( true );
        }

        // Get a local copy of the details
        HashMap< String, String > dets = data.getDetails();

        // Set the computer name info label
        lblCompNameDisp.setText( data.getComputerName() );

        // If we haven't connected yet, stop.
        if ( dets.get( "CONNECTED?" ).equalsIgnoreCase( "NO" ) )
            return;

        lblHasClientInstalled.setText( "Yup" );

        // Set last access time
        Date d = Calendar.getInstance().getTime();
        String format = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        lblLastUpdateDisp.setText( sdf.format( d ) );

        // Set OS label
        String infoOS = dets.get( "CONAM" ) + " " + dets.get( "CARCH" ) + dets.get( "CVERS" );
        lblOSInfoDisp.setText( infoOS );

        // Set user label
        lblUserNameDisp.setText( dets.get( "UNAME" ) );

        // Set CPU usage label
        String infoCPU = ( Double.valueOf( dets.get( "CPU-USED" ) ) * 100 ) + "% load over " + dets.get( "CPU-AMT" ) + " cores.";
        lblCPUUsageDisp.setText( infoCPU );

        // Set Memory usage label
        String infoMem = dets.get( "MEM-FREE" ) + " out of " + dets.get( "MEM-TOTAL" ) + " free";
        lblMemUsageDisp.setText( infoMem );

        // Set Disk usage label
        String infoDisk = dets.get( "DISK-FREE" ) + " out of " + dets.get( "DISK-TOTAL" ) + " free";
        lblDiskUsageDisp.setText( infoDisk );

        frm.repaint();
    }
}
