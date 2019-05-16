package client.data;

import res.Out;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Gets the computer's details and sends it back to the server
 */
public class Details {

    // Declare the bean to retrieve info from
    private OperatingSystemMXBean bean;

    /**
     * Constructor to create the details and start the MXBean
     */
    public Details(){
         bean = ( OperatingSystemMXBean ) ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * Gets the details of the computer it is running on
     * @return the Details as String[] array.
     */
    public String[] getDetails(){

        // Create an array with 13 slots
        String[] toFill = new String[ 13 ];

        // Have we connected successfully?
        toFill[ 0 ] = "YES";

        // Set the last update time
        Date d = Calendar.getInstance().getTime();
        String format = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        toFill[ 1 ] = sdf.format( d );

        // Computer name
        toFill[ 2 ] = getComputerName();

        // Computer architecture ( x86 / x86_64 )
        toFill[ 3 ] = System.getProperty( "os.arch" );

        // Computer OS name
        toFill[ 4 ] = System.getProperty( "os.name" );

        // Computer OS version
        toFill[ 5 ] = System.getProperty( "os.version" );

        // Currently logged in user name
        toFill[ 6 ] = System.getProperty( "user.name" );

        // Next, get all OS details ( RAM / Disk Space / CPU )
        toFill[ 7 ] = String.valueOf( bean.getAvailableProcessors() );

        // Run some things through a formatter since they can get ugly
        DecimalFormat decFormat = new DecimalFormat( "##.##" );
        toFill[ 8 ] = decFormat.format( bean.getSystemCpuLoad() );

        // Get memory information
        toFill[ 9 ] = String.valueOf( bean.getFreePhysicalMemorySize() );
        toFill[ 10 ] = String.valueOf( bean.getTotalPhysicalMemorySize() );

        // Get disk details for main C:\ drive. I don't care about other drives.
        File cDrive = new File( "C:\\" );

        toFill[ 11 ] = String.valueOf( cDrive.getTotalSpace() );
        toFill[ 12 ] = String.valueOf( cDrive.getFreeSpace() );

        return toFill;
    }

    /**
     * Helper method to get the computer name based off InetAddress
     * @return the computer name as String
     */
    private String getComputerName(){
        try {
            String host = InetAddress.getLocalHost().getHostName();
            return ( host.substring( 0, 1 ).toUpperCase() + host.substring( 1 ).toLowerCase() );
        } catch( Exception e ){
            Out.printError( "Details", "Couldn't get host name: " + e.getMessage() );
            return "NONAME?";
        }
    }
}
