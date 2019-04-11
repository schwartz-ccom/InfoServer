package server.ui;

import client.data.ScreenImager;
import res.Out;
import server.data.Computer;
import server.data.DataHandler;
import server.resources.Subscriber;
import server.ui.components.ComputerList;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * App handles the user interface, as well as action events
 * <p>
 * This is set up as a singleton, because there should only
 * be on UI frame running / only one instance of the UI
 */
public class App implements Subscriber {

    private JFrame frm;
    private JLabel lblName;
    private static App instance;

    public static App getInstance() {
        if ( instance == null )
            instance = new App();
        return instance;
    }

    private App() {
        setLook();
        createGUI();
    }

    private void setLook(){
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception e ){
            Out.printError( "App", "Could not set LookAndFeel: " + e.getMessage() );
        }
    }
    private void createGUI() {
        frm = new JFrame( "Info Server" );
        frm.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frm.setLayout( new BorderLayout( 4, 4 ) );
        frm.setSize( new Dimension( 800, 600 ) );

        // Create the scrollable / draggable computer interface
        frm.add( new ComputerList(), BorderLayout.NORTH );

        DataHandler.getInstance().subscribe( this );
        lblName = new JLabel( "Name of computer selected: " );

        frm.add( lblName, BorderLayout.CENTER );
        frm.setVisible( true );
        frm.repaint();
        frm.revalidate();
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

    @Override
    public void update( Computer data ) {
        lblName.setText( "Name of computer selected: " + data );
    }
}
