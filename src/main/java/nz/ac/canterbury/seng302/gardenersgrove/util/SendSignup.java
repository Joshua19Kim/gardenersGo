package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class SendSignup {
    private TokenService tokenService;

    public void sendSignupEmail(Gardener gardener, TokenService tokenService) {
        this.tokenService = tokenService;
        String token = UUID.randomUUID().toString();
        tokenService.createLostPasswordTokenForGardener(gardener, token);
        String email = gardener.getEmail();
        String message = String.format("""
            Your unique signup code for Nature's Facebook: %s
            
            If this was not you, you can ignore this message and the account will be deleted after 10 minutes""", token);

        EmailUserService emailService = new EmailUserService(email, message);
        emailService.sendEmail();
    }
}
