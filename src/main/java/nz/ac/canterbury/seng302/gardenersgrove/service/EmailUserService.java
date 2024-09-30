package nz.ac.canterbury.seng302.gardenersgrove.service;


import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailUserService {
    Logger logger = LoggerFactory.getLogger(EmailUserService.class);
  private final String api_key;

    @Autowired
    public EmailUserService(@Value("${email.password}") String api_key) {
        this.api_key = api_key;
    }

    public void sendEmail(String userEmail, String subject, String message) {
        Email email = EmailBuilder.startingBlank()
                .from("Do Not Reply", "naturesfacebook@gmail.com")
                .to(userEmail)
                .withSubject(subject)
                .withPlainText(message)
                .buildEmail();
        // Uses Gmail SMTP
        Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "naturesfacebook@gmail.com", api_key)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer();

        logger.warn("Spring mail: " + api_key);
        mailer.sendMail(email);
    }
}
