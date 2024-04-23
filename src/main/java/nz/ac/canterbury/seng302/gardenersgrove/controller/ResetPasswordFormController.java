package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ResetPasswordFormController {
    @GetMapping("/resetPassword")
    public String getResetPasswordForm() {
//        if () {
//            return "resetPasswordForm";
//        }
        return "redirect:/login?expire";
    }

    @PostMapping("/resetPassword")
    public String sendResetPasswordForm() {
        return "resetPasswordForm";
    }
}
