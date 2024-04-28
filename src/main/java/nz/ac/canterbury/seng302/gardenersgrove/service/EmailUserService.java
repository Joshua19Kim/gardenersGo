package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailUserService {

    private final String api_key = System.getenv("SJMP");

    private final Email email;
    private final Mailer mailer;

    /**
     * Constructor initialises email object and mailer object using given parameters
     */
    public EmailUserService(String userEmail, String Message) {
        // Simple Java Mail -- https://www.simplejavamail.org/
        email = EmailBuilder.startingBlank()
                .from("Do Not Reply", "naturesfacebook@gmail.com")
                .to("Gardener", userEmail)
                .withSubject("Nature's Facebook Message")
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

