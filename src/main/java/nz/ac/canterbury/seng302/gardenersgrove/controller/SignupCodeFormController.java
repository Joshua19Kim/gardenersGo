package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupCodeFormController {
    @GetMapping("/signup")
    public String getSignupForm() {
        return "signupCodeForm";
    }

    @PostMapping("/signup")
    public String sendSignupForm() {
        return "signupCodeForm";
    }
}