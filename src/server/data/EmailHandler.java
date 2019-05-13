package server.data;

import res.Out;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * Handles emailing the dev with a feature request or something similar
 */
public class EmailHandler {

    private Properties props;
    private static EmailHandler instance;

    // Singleton, so use getInstance method.
    public static EmailHandler getInstance() {
        if ( instance == null )
            instance = new EmailHandler();
        return instance;
    }

    // Default constructor to set up some properties
    // We don't want to do this every time, so this save like... a millisecond or two idk
    private EmailHandler() {
        props = new Properties();
        props.put( "mail.smtp.host", "mail.ccom.unh.edu" );
        props.put( "mail.smtp.localhost", "mail.ccom.unh.edu" );
        props.put( "mail.debug", "true" );
        props.put( "mail.smtp.auth", "false" );
        Out.printInfo( "EmailHandler", "Done setting up props" );
    }

    /**
     * Sends an email to the developer
     * @param title The title of the message as String. Specifies what ype of request.
     * @param body The body of the message as String. Specifies request details.
     */
    public void sendEmail( String title, String body ) {
        String classId = "EmailHandler";

        Out.printInfo( classId, "Sending email to Chris..." );

        Session session = Session.getDefaultInstance( props );

        MimeMessage message = new MimeMessage( session );
        try {
            message.setFrom( new InternetAddress( "no-reply@infoserver.com", "InfoServer Requester Service" ) );
            message.setRecipient( MimeMessage.RecipientType.TO, new InternetAddress( "cschwartz@ccom.unh.edu" ) );
            message.setSubject( title );
            message.setText( body, "utf-8" );
            message.setSentDate( new Date() );
            Transport.send( message );
        } catch ( MessagingException me ) {
            Out.printError( classId, "Error sending message: " + me.getMessage() );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
    }
}
