package res;

/**
 * Out
 * A nice and easy way to track where the prints are coming from
 * as well as format the output so it's not messy
 */
public class Out {

    public static void printInfo( String classId, String msg ){
        System.out.println( "[ " + classId + " ] " + msg );
    }
    public static void printError( String classId, String msg ){
        System.err.println( "[ " + classId + " ] " + msg );
    }
}
