package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ResetPasswordFormController {
    Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final GardenerFormService gardenerFormService;
    private final SecurityService securityService;

    private Gardener gardener;

    @Autowired
    public ResetPasswordFormController(GardenerFormService gardenerFormService,
                                       SecurityService securityService) {
        this.gardenerFormService = gardenerFormService;
        this.securityService = securityService;
    }

    /**
     * Displays the form for resetting the password
     *
     * @param token Unique token from reset link, used to get the gardener
     * @return The reset password form template
     */
    @GetMapping("/resetPassword")
    public String getResetPasswordForm(@RequestParam(name = "token") String token) {
        logger.info("GET /resetPassword");
        // Verifies token has associated user and is not expired
        String result = securityService.validateLostPasswordToken(token);
        if (result == null) {
            Optional<Gardener> tempGardener = securityService.findGardenerbyToken(token);
            tempGardener.ifPresent(g -> gardener = g); // Referenced from ChatGPT
            return "resetPasswordForm";
        }
        return "redirect:/login"; // Token does not exist or is expired
    }

    /**
     * Validates and process the submitted reset password form
     * @param password The new password
     * @param retypePassword The re-entered new password
     * @param model (map-like) representation of error messages for use in thymeleaf
     * @return
     */
    @PostMapping("/resetPassword")
    public String sendResetPasswordForm(@RequestParam(name = "password") String password,
                                        @RequestParam(name = "retypePassword") String retypePassword,
                                        Model model) {
        logger.info("POST /resetPassword");
        InputValidationService inputValidator = new InputValidationService(gardenerFormService);
        Optional<String> passwordMatchError = inputValidator.checkPasswordsMatch(password, retypePassword);
        model.addAttribute("passwordsMatch", passwordMatchError.orElse(""));
        Optional<String> passwordStrengthError = inputValidator.checkStrongPassword(password);
        model.addAttribute("passwordStrong", passwordStrengthError.orElse(""));

        if (passwordMatchError.isEmpty() && passwordStrengthError.isEmpty()) {
            gardener.updatePassword(password);
            gardenerFormService.addGardener(gardener);
            // Re-authenticates user to catch case when they change their email
            Authentication newAuth = new UsernamePasswordAuthenticationToken(gardener.getEmail(), gardener.getPassword(), gardener.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication((newAuth)); // do i need this part for resetting?
            return "redirect:/login";
        }
        return "resetPasswordForm";
    }
}
