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
public class LostPasswordFormController {
    Logger logger = LoggerFactory.getLogger(LostPasswordFormController.class);
    private final GardenerFormService gardenerFormService;
    String confirmationMessage = "An email was sent to the address if it was recognised";
    @Autowired
    public LostPasswordFormController(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }
    @GetMapping("/forgotPassword")
    public String getLostPasswordForm(@RequestParam(name="email") String email, Model model) {
        logger.info("GET /forgotPassword");
        model.addAttribute("email", email);
        return "lostPasswordForm";
    }

    @PostMapping("/forgotPassword")
    public String sendResetPasswordLink(@RequestParam(name="email") String email, Model model) {
        logger.info("POST /forgotPassword");
        model.addAttribute("email", email);
        InputValidationService inputValidator = new InputValidationService(gardenerFormService);
        Optional<String> validEmailError = inputValidator.checkValidEmail(email);
        Optional<String> emailInUseError = inputValidator.checkEmailInUse(email);
        // i want if validEmailError is empty but emailInUseError is not for ac4
        // i want if validEmailError is empty and emailInUseError is too for ac3

        // emailValid is either the String stored in validEmailError OR ELSE it is equal to the String stored in emailInUseError otherwise its empty
        model.addAttribute("emailValid", validEmailError.orElse(emailInUseError.orElse("")));

        if (validEmailError.isPresent()) {
            model.addAttribute("confirmationMessage", "");
        } else {
            model.addAttribute("confirmationMessage", confirmationMessage);
            if (emailInUseError.isEmpty()) {
                //send link to email
                return "redirect:/login";
            }
        }
        return "lostPasswordForm";
    }
}
