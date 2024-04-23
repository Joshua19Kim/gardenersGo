package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This controller class is used to handle requests related to signup code forms
 */
@Controller
public class SignupCodeFormController {
    /**
     * Handles GET requests to "/signup" endpoint.
     * Displays the signup code form.
     *
     * @return signupCodeFormTemplate
     */
    @GetMapping("/signup")
    public String getSignupForm() {
        return "signupCodeForm";
    }

    @PostMapping("/signup")
    public String sendSignupForm() {
        return "signupCodeForm";
    }
}