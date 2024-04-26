package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SecurityService;
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
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
public class LostPasswordFormController {

    Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final GardenerFormService gardenerFormService;
    private final SecurityService securityService;

    @Autowired
    public LostPasswordFormController(GardenerFormService gardenerFormService,
                                      SecurityService securityService) {
        this.gardenerFormService = gardenerFormService;
        this.securityService = securityService;
    }

    public String constructLostPasswordTokenEmail(String contextPath, Locale locale, String token, Gardener gardener) {
        // PATH MIGHT change
        String url = contextPath + "/changePassword?token=" + token;
        // Might need messages.getMessage???
        return ("Reset Password link: " + url);
    }

    public String getAppUrl(HttpServletRequest request) {
        // MIGHT need: "http://" + request.getServerName() + ":" + request.getServerPort() +
        return "localhost:8080" + request.getContextPath();
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
    public String sendLostPasswordLink( HttpServletRequest request,
                                        @RequestParam(name="email") String email,
                                        Model model) {
        logger.info("POST /forgotPassword");

        model.addAttribute("email", email);

        Optional<Gardener> gardener = gardenerFormService.findByEmail(email);

        if (gardener.isPresent()){
            // FROM https://www.baeldung.com/spring-security-registration-i-forgot-my-password
            String token = UUID.randomUUID().toString();
            gardenerFormService.createLostPasswordTokenForGardener(gardener.get(), token);
            String emailMessage = constructLostPasswordTokenEmail(getAppUrl(request), request.getLocale(), token, gardener.get());

            // FOR TESTING:
            email = "benmoore1.work@gmail.com";
            EmailUserService emailService = new EmailUserService(email, emailMessage);
            emailService.sendEmail();
            return "redirect:/login";
        }

        return "lostPasswordForm";
    }


    @GetMapping("/changePassword") // Authentication issues (I believe)
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        logger.info("POST /changePassword");
        String result = securityService.validateLostPasswordToken(token);
        if(result == null) {
            return "redirect:/resetPassword";
        }
        return "redirect:/login";
    }
}
