package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static nz.ac.canterbury.seng302.gardenersgrove.util.TokenGenerator.generateToken;
/**
 * Util class for writing and sending emails.
 */


@Component
public class WriteEmail {
    private TokenService tokenService;
    private final EmailUserService emailService;

    public WriteEmail(EmailUserService emailUserService, TokenService tokenService)
    {
        this.emailService = emailUserService;
        this.tokenService = tokenService;
    }

    /**
     * Sends a signup email to the given Gardener's email
     * @param gardener The Gardener to send the email to
     * @param tokenService  The token service to create the unique LostPasswordToken for the gardener
     */
    public void sendSignupEmail(Gardener gardener, TokenService tokenService) {
        this.tokenService = tokenService;
        String token = generateToken();
        tokenService.createLostPasswordTokenForGardener(gardener, token);
        String email = gardener.getEmail();
        String subject = "Nature's Facebook Signup Code";
        String message = String.format("""
            Your unique signup code for Nature's Facebook: %s
            
            If this was not you, you can ignore this message and the account will be deleted after 10 minutes""", token);

        emailService.sendEmail(email, subject, message);
    }

    /**
     * Send confirmation email to gardener's email when gardener(user) updates password successfully.
     * @param gardener Gardener to get the email address
     */
    public void sendPasswordUpdateConfirmEmail(Gardener gardener) {
        String email = gardener.getEmail();
        String message = "Your Password has been updated";
        String subject = "Updated Password";
        emailService.sendEmail(email, subject, message);

    }
    /**
     * Send reset password confirmation email to gardener's email when gardener(user) resets password successfully.
     * @param gardener Gardener to get the email address
     */
    public void sendPasswordResetConfirmEmail(Gardener gardener) {
        String email = gardener.getEmail();
        String message = "Your Password has been updated";
        String subject = "Password Updated";
        emailService.sendEmail(email, subject, message); // *** Blocking


    }

        /**
         *
         * @param baseURL the path of the server
         * @param token the token to be sent to the user
         * @return the content of the email which is the link to reset the users password
         */
    public String constructLostPasswordTokenEmail(String baseURL, String token) {
        String url = baseURL + "/resetPassword?token=" + token;
        return ("Reset Password link:\n" + url +"\nThis link will expire after 10 mins.");
    }

    /**
     * Send forgot password email to gardener's email when gardener(user) forgets password.
     * Sends an email with a link that takes user to a form where they can reset their password
     * @param gardener Gardener to get the email address
     */
    public void sendPasswordForgotEmail(Gardener gardener, String url) {
        // FROM https://www.baeldung.com/spring-security-registration-i-forgot-my-password
        String token = UUID.randomUUID().toString();
        tokenService.createLostPasswordTokenForGardener(gardener, token);
        String email = gardener.getEmail();
        String emailMessage = constructLostPasswordTokenEmail(url, token);
        String subject = "Forgot password?";
        emailService.sendEmail(email, subject, emailMessage); // *** Blocking
    }

    public void sendTagWarningEmail (Gardener gardener) {
        String subject = "!! Warning for bad words !!";
        String emailMessage = "You have reached the maximum number of bad words on our web site. If you add another inappropriate tag, Your account will be blocked for one week.";
        emailService.sendEmail(gardener.getEmail(), subject, emailMessage);
    }



}
