package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.AuthorityFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
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

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * This controller class is used to handle requests related to signup code forms
 */
@Controller
public class SignupCodeFormController {
    private final Logger logger = LoggerFactory.getLogger(SignupCodeFormController.class);
    private final GardenerFormService gardenerFormService;
    private final AuthorityFormService authorityFormService;
    private final TokenService tokenService;
    private Gardener gardener;
    @Autowired
    public SignupCodeFormController(GardenerFormService gardenerFormService, AuthorityFormService authorityFormService, TokenService tokenService) {
        this.gardenerFormService = gardenerFormService;
        this.authorityFormService = authorityFormService;
        this.tokenService = tokenService;
    }
    private long gardenerId;

    /**
     * Handles GET requests to "/signup" endpoint.
     * Displays the signup code form.
     *
     * @return signupCodeFormTemplate
     */
    @GetMapping("/signup")
    public String getSignupForm() {
        logger.info("GET /signup");
            return "signupCodeForm";
    }

    /**
     * Validates the signup token and assigns the user role (verifies them) to the user
     * @param signupToken The unique Token with which the user can be identified
     * @param model For displaying the error messages in thymeleaf
     * @return Redirect to login page if successful, otherwise back to the signup code form
     */
    @PostMapping("/signup")
    public String sendSignupForm(
                                 @RequestParam(name= "signupToken", required = false, defaultValue = "") String signupToken,
                                 Model model) {
        logger.info("POST /signup");
        if (tokenService.validateLostPasswordToken(signupToken) != "invalidToken") {
            Optional<Gardener> tempGardener = tokenService.findGardenerbyToken(signupToken);
            if (tempGardener.isPresent() && tokenService.validateLostPasswordToken(signupToken) == "expired") {
                gardener = tempGardener.get();
                Optional<LostPasswordToken> tempToken = tokenService.getTokenFromString(signupToken);
                if(tempToken.isPresent()){
                    tokenService.removeToken(tempToken.get());
                    gardenerFormService.removeGardener(gardener);
                    logger.info("need to remove user");}
                return "redirect:/register";
            }
            else if (tempGardener.isPresent() && tokenService.validateLostPasswordToken(signupToken)== null) {
                gardener = tempGardener.get();
                logger.info("Granting authority.....");
                gardener.grantAuthority("ROLE_USER");
                gardenerFormService.addGardener(gardener);
                return "redirect:/login";
        }
        return "signupCodeForm";
    } return "redirect:/register";
}}