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
import java.time.LocalDate;

@Controller
public class LostPasswordFormController {

    Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final GardenerFormService gardenerFormService;

    @Autowired
    public LostPasswordFormController(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    @GetMapping("/forgotPassword")
    public String getLostPasswordForm() {
        return "lostPasswordForm";
    }

    /**
     * Posts a form response with the email to send lost password link to
     * @param email User's email
     * @param model (map-like) representation of email for use in thymeleaf
     * @return thymeleaf lostPasswordForm (if error) or redirects to LostPasswordTokenForm if valid
     */
    @PostMapping("/forgotPassword")
    public String sendLostPasswordLink( @RequestParam(name="email") String email,
                                        Model model) {
        logger.info("POST /forgotPassword");

        model.addAttribute("email", email);

//        Gardener gardener = GardenerFormService;
        //Code to send email
		EmailUserService emailService = new EmailUserService("benmoore1.work@gmail.com", "gmail2");
		emailService.sendEmail();
        return "lostPasswordForm";
    }

}
