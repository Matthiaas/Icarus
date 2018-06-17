package main.java.bot;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


public class Email {

    // requires valid gmail id
    private static final String fromEmail = "otto.drone.protector@gmail.com";
    private static final String nameEmail = "Drone ALERT";
    private static final String replyTo = "otto.drone.protector@gmail.com";
    // correct password for gmail id
    private static final String password = "supergeheim";
    // can be any email id
    private static final String toEmail = "felix.ma.fischer@gmail.com";

    /**
     * Sends an Email.
     *
     * @param subject - subject line of the email
     * @param body    - text body of the email
     */
    public static void SendMailSSL(String subject, String body) {

        Properties props = new Properties();
        // SMTP Host
        props.put("mail.smtp.host", "smtp.gmail.com");
        // SSL Port
        props.put("mail.smtp.socketFactory.port", "465");
        // SSL Factory Class
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // Enabling SMTP Authentication
        props.put("mail.smtp.auth", "true");
        // SMTP Port
        props.put("mail.smtp.port", "465");

        Authenticator auth = new Authenticator() {
            // override the getPasswordAuthentication method
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);


        try {

            MimeMessage msg = new MimeMessage(session);
            // set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(fromEmail, nameEmail));

            msg.setReplyTo(InternetAddress.parse(replyTo, false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));


            Transport.send(msg);


        } catch (Exception e) {

        }

    }// void SendMailSSL


}// class notifications