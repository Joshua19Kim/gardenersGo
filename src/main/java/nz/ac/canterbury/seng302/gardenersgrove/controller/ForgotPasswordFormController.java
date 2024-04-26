package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
public class ForgotPasswordFormController {
    Logger logger = LoggerFactory.getLogger(ForgotPasswordFormController.class);
    private final GardenerFormService gardenerFormService;
    String confirmationMessage = "An email was sent to the address if it was recognised";
    @Autowired
    public ForgotPasswordFormController(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    /**
     * Displays the form for resetting the password if forgotten
     * @return The lost password form template
     */
    @GetMapping("/forgotPassword")
    public String getLostPasswordForm() {
        logger.info("GET /forgotPassword");
        return "forgotPasswordForm";
    }

    /**
     * Handles the submission of the lost password form
     * If the user inputs an email that exists in the database then a unique token is emailed to the email address
     * @param email The email of the user with the forgotten password
     * @param model The model for passing data to the view.
     * @return The template for the lost password form
     */
    @PostMapping("/forgotPassword")
    public String sendResetPasswordLink(@RequestParam(name="email") String email, Model model) {
        logger.info("POST /forgotPassword");
        model.addAttribute("email", email);
        InputValidationService inputValidator = new InputValidationService(gardenerFormService);
        Optional<String> validEmailError = inputValidator.checkValidEmail(email);
        Optional<String> emailInUseError = inputValidator.checkEmailInUse(email);

        model.addAttribute("returnMessage", validEmailError.orElse(confirmationMessage));

        if (emailInUseError.isEmpty() && validEmailError.isEmpty()) {
            //send link to email
        }

        return "forgotPasswordForm";
    }
}
