package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordFormController {

    Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final GardenerFormService gardenerFormService;
    private final TokenService tokenService;
    private final WriteEmail writeEmail;
    private final EmailUserService emailService;

    private final String confirmationMessage = "An email was sent to the address if it was recognised";

    @Autowired
    public ForgotPasswordFormController(GardenerFormService gardenerFormService,
                                        TokenService tokenService,
                                        EmailUserService emailService,
                                        WriteEmail writeEmail) {
        this.gardenerFormService = gardenerFormService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.writeEmail = writeEmail;
    }

    /**
     * Displays the form for sending a reset link if the password is forgotten
     * @return The lost password form template
     */
    @GetMapping("/forgotPassword")
    public String getForgotPasswordForm() {
        logger.info("GET /forgotPassword");
        return "forgotPasswordForm";
    }

    /**
     * Posts a form response with the email to send lost password link to the user
     * @param email User's email
     * @param model (map-like) representation of email and message for use in thymeleaf
     * @return thymeleaf forgotPasswordForm (if error) or redirects to LostPasswordTokenForm if valid
     */
    @PostMapping("/forgotPassword")
    public String sendResetPasswordLink( HttpServletRequest request,
                                        @RequestParam(name="email") String email,
                                        Model model) {
        logger.info("POST /forgotPassword");

        model.addAttribute("email", email);

        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);
        Optional<String> validEmailError = inputValidator.checkValidEmail(email);
        model.addAttribute("returnMessage", validEmailError.orElse(confirmationMessage));

        if (validEmailError.isEmpty()){
            Optional<Gardener> gardener = gardenerFormService.findByEmail(email);
            if (gardener.isPresent()) {

                writeEmail.sendPasswordForgotEmail(gardener.get(), request); // Blocks ***
                return "forgotPasswordForm"; // Email sent
            }
            return "forgotPasswordForm"; // Email not in DB
        }
        return "forgotPasswordForm"; // Email not valid
    }
}
