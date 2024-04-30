package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;

import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.springframework.stereotype.Component;
import static nz.ac.canterbury.seng302.gardenersgrove.util.TokenGenerator.generateToken;

import java.util.Locale;
import java.util.UUID;
/**
 * Util class for writing and sending emails.
 */


@Component
public class WriteEmail {
    private TokenService tokenService;

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

        EmailUserService emailService = new EmailUserService(email, subject, message);
        emailService.sendEmail();
    }
}
