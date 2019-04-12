package server.ui;

import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.data.MacroHandler;
import server.resources.ComputerSubscriber;
import server.data.Macro;
import server.resources.MacroSubscriber;
import server.ui.components.ComputerList;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * App handles the user interface, as well as action events
 * This is set up as a singleton, because there should only
 * be on UI frame running / only one instance of the UI
 */
public class App implements ComputerSubscriber, MacroSubscriber {

    private JFrame frm;
    private JMenu mnMacro;
    private JMenuItem mniCreateMacro;
    private JMenuItem mniExport;
    private JMenuItem mniEditMacro;

    private JComboBox< String > cbxMacros;
    private JLabel lblName;
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
        mniEditMacro = new JMenuItem( "Edit Macro" );
        mniExport = new JMenuItem( "Export Macros" );

        mnMacro.add( mniCreateMacro );
        mbMenu.add( mnMacro );

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
        mniCreateMacro.addActionListener( actionEvent -> showMacroCreationPane() );
        mniEditMacro.addActionListener( actionEvent -> showMacroEditingPane() );
        mniExport.addActionListener( actionEvent -> {
            // Implement later
            JOptionPane.showMessageDialog( frm, "Not implemented yet. Sorry." );
        } );

        frm.setJMenuBar( mbMenu );

        // Finally, show the frame
        frm.setVisible( true );
        frm.repaint();
        frm.revalidate();
    }

    /**
     * Shows the macro creation pane from the MenuItem
     */
    private void showMacroCreationPane() {
        // Create the button labels for the JOptionPane
        Object[] optPaneBtnLabels = { "Create New Macro", "Cancel" };

        // Create the label for the name box
        JLabel lblName = new JLabel( "Macro Name: " );

        // Create the text area to put a name in
        JTextField txtName = new JTextField();
        txtName.setPreferredSize( new Dimension( 60, 30 ) );

        // Create the JLabel to tell what the field is
        JLabel lblEditor = new JLabel( "Macro Commands: " );

        // Create the actual area to edit the macro
        JTextArea txtMacroEditor = new JTextArea();
        txtMacroEditor.setLineWrap( true );
        txtMacroEditor.setWrapStyleWord( true );

        // and then encapsulate it in a ScrollPane
        JScrollPane sp = new JScrollPane( txtMacroEditor );
        sp.setBorder( BorderFactory.createEtchedBorder() );
        sp.setPreferredSize( new Dimension( 60, 200 ) );
        sp.createVerticalScrollBar();

        // Create the button that shows the style sheet
        JButton btnShowCommands = new JButton( "Show Available Macro Commands..." );
        btnShowCommands.addActionListener( actionEvent1 -> showMacroInformationPane() );

        // Wrap them all up into an object
        Object[] uiElements = { lblName, txtName, lblEditor, sp, btnShowCommands };
        int status = JOptionPane.showOptionDialog(
                frm,
                uiElements,
                "Create a macro",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                optPaneBtnLabels,
                optPaneBtnLabels[ 0 ]
        );

        if ( status == 0 ) {
            // Create the Macro from the information given
            Macro newMacro = new Macro( txtName.getText() );
            // Add the commands to the macro. Each new line specifies a new command
            String[] cmds = txtMacroEditor.getText().split( "\n" );
            for ( String s : cmds )
                newMacro.addAction( s );
            // Then add it.
            MacroHandler.getInstance().addMacroToCollection( newMacro );
        }
    }

    /**
     * Shows the macro edit pane from the MenuItem
     */
    private void showMacroEditingPane() {
        // Create the button labels for the JOptionPane
        Object[] optPaneBtnLabels = { "Save Edited Macro", "Cancel" };

        // Create the label for the name box
        JLabel lblName = new JLabel( "Macro Name: " );

        // Create the JLabel to tell what the field is
        JLabel lblEditor = new JLabel( "Macro Commands: " );

        // Create text area for all the commands
        // This is out of order for a reason.
        JTextArea txtCommands = new JTextArea();
        txtCommands.setWrapStyleWord( true );
        txtCommands.setLineWrap( true );

        // For later, we need a combo box, so here we go
        // Create the combo box to get all of the macros
        Out.printInfo( getClass().getSimpleName(), "ilis = " + cbxMacros.getItemListeners().length );
        if ( cbxMacros.getItemListeners().length == 1 ) {
            cbxMacros.addItemListener( itemEvent -> {
                if ( cbxMacros.getSelectedIndex() == 0 || cbxMacros.getSelectedItem() == null )
                    return;
                Macro current = MacroHandler.getInstance().getMacro( cbxMacros.getSelectedItem().toString() );
                StringBuilder sb = new StringBuilder();
                for ( String s : current.getMacroSteps() ) {
                    sb.append( s );
                    sb.append( "\n" );
                }
                txtCommands.setText( sb.toString() );
            } );
        }

        // Wrap it in a scroll pane
        JScrollPane sp = new JScrollPane( txtCommands );
        sp.setPreferredSize( new Dimension( 60, 200 ) );
        sp.setBorder( BorderFactory.createEtchedBorder() );
        sp.setAutoscrolls( true );

        // Create the button that shows the style sheet
        JButton btnShowCommands = new JButton( "Show Available Macro Commands..." );
        btnShowCommands.addActionListener( actionEvent1 -> showMacroInformationPane() );

        // Combine them into an Object array
        Object[] uiElements = { lblName, cbxMacros, lblEditor, sp, btnShowCommands };

        // Then display it in an OptionPane
        int status = JOptionPane.showOptionDialog(
                frm,
                uiElements,
                "Edit Macro",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                optPaneBtnLabels,
                optPaneBtnLabels[ 0 ]
        );
        if ( status == 0 ) {
            if ( cbxMacros.getSelectedItem() != null ) {
                // Get the edited lines ( Get all lines, and then use those. )
                String[] cmdsEdit = txtCommands.getText().split( "\n" );
                // Tell the MacroHandler to get the macro by name and change the lines to whatever we have
                MacroHandler.getInstance().changeMacroInCollection( cbxMacros.getSelectedItem().toString(), cmdsEdit );
            }
        }
    }

    /**
     * Shows the available Macro commands
     */
    private void showMacroInformationPane(){
        String message = "Available commands: \n" +
                "MOUSE MOVE [ x_coordinate ] [ y_coordinate ]\n" +
                "MOUSE PRESS [ mouse_button ]\n" +
                "KEY PRESS [ key_code ]\n" +
                "TYPE [ string_to_type ]\n" +
                "RUN [ path_as_string ]\n" +
                "DELAY [ time_in_milliseconds ]\n\n" +
                "Note: These are in caps, but the commands are not case sensitive.\n" +
                "That being said, you should probably adopt\n" +
                "a style and either go full upper or lower case.";
        JOptionPane.showMessageDialog( null, message );
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

        Out.printInfo( getClass().getSimpleName(), "Macro size: " + macros.length );

        mnMacro.removeAll();
        mnMacro.add( mniCreateMacro );
        mnMacro.add( mniEditMacro );
        mnMacro.add( mniExport );
        mnMacro.addSeparator();

        cbxMacros.removeAllItems();
        cbxMacros.addItem( "Choose a Macro" );

        for ( Macro m : macros ) {
            JMenuItem temp = new JMenuItem( m.getMacroName() );
            temp.addActionListener( actionEvent -> {
                // Implement running later
                JOptionPane.showMessageDialog( frm, m.toString() );
            } );
            mnMacro.add( temp );
            cbxMacros.addItem( m.getMacroName() );
        }
    }
}
