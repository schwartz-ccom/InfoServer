package client.data;

import res.Out;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Gets the computer's details and sends it back to the server
 */
public class Details {

    private static String classId = "Details";

    public static HashMap< String, String > getDetails(){
        HashMap< String, String > details = new HashMap<>();

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

        return details;
    }
    private static String getComputerName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch( Exception e ){
            Out.printError( classId, "Couldn't get host name: " + e.getMessage() );
            return "NONAME?";
        }
    }
}
