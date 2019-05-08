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
import java.util.HashMap;

/**
 * Gets the computer's details and sends it back to the server
 */
public class Details {

    private static HashMap< String, String > details;

    public static HashMap< String, String > getDetails(){
        // Create the hashmap to store stuff
        details = null;
        details = new HashMap<>();

        // Set up the OS bean to get info
        OperatingSystemMXBean bean =
                ( OperatingSystemMXBean ) ManagementFactory.getOperatingSystemMXBean();

        // Just a flag to avoid null pointer errors
        details.put( "CONNECTED?", "YES" );

        // Set the last update time
        Date d = Calendar.getInstance().getTime();
        String format = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        details.put( "TIME", sdf.format( d ) );

        // Computer name
        details.put( "CNAME", getComputerName() );

        // Computer architecture ( x86 / x86_64 )
        details.put( "CARCH", System.getProperty( "os.arch" ) );

        // Computer OS name
        details.put( "CONAM", System.getProperty( "os.name" ) );

        // Computer OS version
        details.put( "CVERS", System.getProperty( "os.version" ) );

        // Currently logged in user name
        details.put( "UNAME", System.getProperty( "user.name" ) );

        // Next, get all OS details ( RAM / Disk Space / CPU )
        details.put( "CPU-AMT", String.valueOf( bean.getAvailableProcessors() ) );

        // Run some things through a formatter since they can get ugly
        DecimalFormat decFormat = new DecimalFormat( "##.##" );

        details.put( "CPU-USED", decFormat.format( bean.getSystemCpuLoad() ) );
        details.put( "MEM-FREE", String.valueOf( bean.getFreePhysicalMemorySize() ) );
        details.put( "MEM-TOTAL", String.valueOf( bean.getTotalPhysicalMemorySize() ) );

        // Get disk details for main C:\ drive. I don't care about other drives.
        File cDrive = new File( "C:\\" );
        details.put( "DISK-TOTAL", String.valueOf( cDrive.getTotalSpace() ) );
        details.put( "DISK-FREE", String.valueOf( cDrive.getFreeSpace() ) );

        return details;
    }
    private static String getComputerName(){
        try {
            String host = InetAddress.getLocalHost().getHostName();
            return ( host.substring( 0, 1 ).toUpperCase() + host.substring( 1 ).toLowerCase() );
        } catch( Exception e ){
            Out.printError( "Details", "Couldn't get host name: " + e.getMessage() );
            return "NONAME?";
        }
    }
}
