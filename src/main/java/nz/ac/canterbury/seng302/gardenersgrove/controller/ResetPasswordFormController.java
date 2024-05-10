package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final TokenService tokenService;
    private final WriteEmail writeEmail;

    private Gardener gardener;

    @Autowired
    public ResetPasswordFormController(GardenerFormService gardenerFormService,
                                       TokenService tokenService, WriteEmail writeEmail) {
        this.gardenerFormService = gardenerFormService;
        this.tokenService = tokenService;
        this.writeEmail = writeEmail;
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
        String result = tokenService.validateLostPasswordToken(token);
        if (result == null) { // No issue
            Optional<Gardener> tempGardener = tokenService.findGardenerbyToken(token);
            if (tempGardener.isPresent()) {
                gardener = tempGardener.get();
                return  "resetPasswordForm";
            }
            return "redirect:/login"; // Gardener / Id not present
        } else if (result.equals("expired")) {
            Optional<LostPasswordToken> expiredToken = tokenService.getTokenFromString(token);
            expiredToken.ifPresent(e -> tokenService.removeToken(e));
            return "redirect:/login?expired"; // Token is expired
        }
        return "redirect:/login?expired"; // Token does not exist
    }

    /**
     * Validates and process the submitted reset password form
     * @param password The new password
     * @param retypePassword The re-entered new password
     * @param model (map-like) representation of error messages for use in thymeleaf
     * @return resetPasswordForm if input is invalid, otherwise redirects to login page
     */
    @PostMapping("/resetPassword")
    public String sendResetPasswordForm(@RequestParam(name = "password") String password,
                                        @RequestParam(name = "retypePassword") String retypePassword,
                                        Model model) {
        logger.info("POST /resetPassword");
        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);
        Optional<String> passwordMatchError = inputValidator.checkPasswordsMatch(password, retypePassword);
        model.addAttribute("passwordsMatch", passwordMatchError.orElse(""));
        Optional<String> passwordStrengthError = inputValidator.checkStrongPassword(password);
        model.addAttribute("passwordStrong", passwordStrengthError.orElse(""));

        if (passwordMatchError.isEmpty() && passwordStrengthError.isEmpty()) {
            gardener.updatePassword(password);
            gardenerFormService.addGardener(gardener);
            writeEmail.sendPasswordResetConfirmEmail(gardener);
            return "redirect:/login";
        }
        return "resetPasswordForm";
    }
}
