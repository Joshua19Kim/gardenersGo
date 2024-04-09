package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

public class LostPasswordFormController {
    @GetMapping("/forgotPassword")
    public String getLostPasswordForm() {
        return "lostPasswordForm";
    }

    @PostMapping("/resetPassword")
    public String sendResetPasswordLink() {
        return "lostPasswordForm";
    }
}
