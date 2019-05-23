package res;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Out
 * A nice and easy way to track where the prints are coming from
 * as well as format the output so it's not messy
 */
public class Out {

    private static boolean time = false;

    public static void printInfo( String classId, String msg ){
        if ( !time )
            System.out.println( "[ " + classId + " ] " + msg );
        else
            System.out.println( "[ " + classId + " @ " + timeNow() + " ] " + msg );
    }
    public static void printError( String classId, String msg ){
        if ( !time )
            System.err.println( "[ " + classId + " ] " + msg );
        else
            System.err.println( "[ " + classId + " @ " + timeNow() + " ] " + msg );
    }
    private static String timeNow(){
        Date d = Calendar.getInstance().getTime();
        String format = "HH:mm:ss.SSS";
        SimpleDateFormat sdf = new SimpleDateFormat( format );
        return sdf.format( d );
    }
    public static void enableTime(){
        time = true;
    }
    public static void disableTime(){
        time = false;
    }
}
