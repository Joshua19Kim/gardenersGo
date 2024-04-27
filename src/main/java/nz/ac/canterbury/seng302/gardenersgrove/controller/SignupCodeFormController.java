package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * This controller class is used to handle requests related to signup code forms
 */
@Controller
public class SignupCodeFormController {
    private final Logger logger = LoggerFactory.getLogger(SignupCodeFormController.class);
    private final GardenerFormService gardenerFormService;
    private Gardener gardener;
    @Autowired
    public SignupCodeFormController(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    private final String message = "fasdjlkfhak";

    /**
     * Handles GET requests to "/signup" endpoint.
     * Displays the signup code form.
     *
     * @return signupCodeFormTemplate
     */
    @GetMapping("/signup")
    public String getSignupForm(@RequestParam(name= "signupCode", required = false, defaultValue = "") String signupCode, Model model) {
        logger.info("GET /signup");
        EmailUserService emailService = new EmailUserService("jxmine456@gmail.com", "Nature's Facebook Signup Code", String.format("""
                Your unique signup code for Nature's Facebook: %s
                
                If this was not you, you can ignore this message and the account will be deleted after 10 minutes""", message));
        emailService.sendEmail();
        return "signupCodeForm";
    }

    @PostMapping("/signup")
    public String sendSignupForm(@RequestParam(name= "signupCode", required = false, defaultValue = "") String signupCode, Model model) {
        logger.info("POST /signup");
        if (Objects.equals(signupCode, message)) {
            return "redirect:user";
        }
        return "signupCodeForm";
    }
}