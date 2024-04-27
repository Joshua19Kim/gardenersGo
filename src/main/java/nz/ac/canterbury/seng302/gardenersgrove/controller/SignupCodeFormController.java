package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Objects;
import java.util.Optional;

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

    private final String token = "test";
    private long gardenerId;

    /**
     * Handles GET requests to "/signup" endpoint.
     * Displays the signup code form.
     *
     * @return signupCodeFormTemplate
     */
    @GetMapping("/signup")
    public String getSignupForm(HttpServletRequest request,
                                @RequestParam(name= "signupCode", required = false, defaultValue = "") String signupCode,
                                Model model) {
        gardenerId = (Long) request.getSession().getAttribute("newGardenerAttribute");
        Gardener newGardener = gardenerFormService.findById(gardenerId).get();
        logger.info("GET /signup");
        logger.info("New Gardener: " + newGardener);
        EmailUserService emailService = new EmailUserService("jxmine456@gmail.com", "Nature's Facebook Signup Code", String.format("""
                Your unique signup code for Nature's Facebook: %s
                
                If this was not you, you can ignore this message and the account will be deleted after 10 minutes""", token));
        emailService.sendEmail();
        return "signupCodeForm";
    }

    @PostMapping("/signup")
    public ModelAndView sendSignupForm(HttpServletRequest request,
                                       @RequestParam(name= "signupCode", required = false, defaultValue = "") String signupCode,
                                       Model model) {
        Gardener newGardener = gardenerFormService.findById(gardenerId).get();
        logger.info("POST /signup");
        logger.info("New Gardener: " + gardener);
        if (Objects.equals(signupCode, token)) {
            newGardener.grantAuthority("ROLE_USER");
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("signupCodeForm");
    }
}