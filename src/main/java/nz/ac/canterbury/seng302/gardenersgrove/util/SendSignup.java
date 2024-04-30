package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;

import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SendSignup {
    private TokenService tokenService;

    public void sendSignupEmail(Gardener gardener, TokenService tokenService) {
        this.tokenService = tokenService;

        String token = UUID.randomUUID().toString();
        tokenService.createLostPasswordTokenForGardener(gardener, token);
        String email = gardener.getEmail();
        String subject = "Nature's Facebook Signup Code";
        String message = String.format("""
            Your unique signup code for Nature's Facebook: %s
            
            If this was not you, you can ignore this message and the account will be deleted after 10 minutes""", token);

        EmailUserService emailService = new EmailUserService(email, subject, message);
        emailService.sendEmail();
    }

    /**
     * Send confirmation email to gardener's email when gardener(user) updates password successfully.
     * @param gardener Gardener to get the email address
     */
    public void sendPasswordUpdateConfirmEmail(Gardener gardener) {
        String email = gardener.getEmail();
        String message = "Your Password has been updated";
        String subject = "Updated Password";
        EmailUserService emailService = new EmailUserService(email, subject, message);
        emailService.sendEmail();

    }
}
