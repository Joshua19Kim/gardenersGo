package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
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

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordFormController {

    Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final GardenerFormService gardenerFormService;
    private final SecurityService securityService;

    String confirmationMessage = "An email was sent to the address if it was recognised";

    @Autowired
    public ForgotPasswordFormController(GardenerFormService gardenerFormService,
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

        InputValidationService inputValidator = new InputValidationService(gardenerFormService);
        Optional<String> validEmailError = inputValidator.checkValidEmail(email);
        Optional<String> emailInUseError = inputValidator.checkEmailInUse(email);
        model.addAttribute("returnMessage", validEmailError.orElse(confirmationMessage));

        if (emailInUseError.isEmpty() && validEmailError.isEmpty()){
            Optional<Gardener> gardener = gardenerFormService.findByEmail(email);
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

        return "forgotPasswordForm";
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
