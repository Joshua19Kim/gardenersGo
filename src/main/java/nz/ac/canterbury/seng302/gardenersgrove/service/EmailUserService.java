package nz.ac.canterbury.seng302.gardenersgrove.service;


import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class EmailUserService {
    Logger logger = LoggerFactory.getLogger(EmailUserService.class);

    private String api_key = System.getenv("SJMP");

    private Email email;
    private Mailer mailer;

    /**
     * Constructor initialises email object and mailer object using given parameters
     */
    public EmailUserService(String userEmail, String subject, String Message) {
        // Simple Java Mail -- https://www.simplejavamail.org/
        email = EmailBuilder.startingBlank()
                .from("Do Not Reply", "naturesfacebook@gmail.com")
                .to(userEmail)
                .withSubject(subject)
                .withPlainText(Message)
                .buildEmail();

        // Uses gmail SMTP
        mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "naturesfacebook@gmail.com", api_key)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer();
    }

    public void sendEmail() {

        mailer.sendMail(email);
    }
}
