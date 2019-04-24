package client.data;

import res.Out;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Gets the computer's details and sends it back to the server
 */
public class Details {

    private static String classId = "Details";
    private static OperatingSystemMXBean bean;

    public static HashMap< String, String > getDetails(){
        HashMap< String, String > details = new HashMap<>();
        bean = ( OperatingSystemMXBean ) ManagementFactory.getOperatingSystemMXBean();
        // Just a flag to avoid null pointer errors
        details.put( "CONNECTED?", "YES" );

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
        DecimalFormat d = new DecimalFormat( "##.##" );

        details.put( "CPU-USED", d.format( bean.getSystemLoadAverage() ) );
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
            Out.printError( classId, "Couldn't get host name: " + e.getMessage() );
            return "NONAME?";
        }
    }
}
