package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for form example.
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class RegisterFormController {
    private final GardenerFormService gardenerFormService;
    private final AuthenticationManager authenticationManager;
    Logger logger = LoggerFactory.getLogger(RegisterFormController.class);


    @Autowired
    public RegisterFormController(GardenerFormService gardenerFormService, AuthenticationManager authenticationManager) {
        this.gardenerFormService = gardenerFormService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Gets form to be displayed, includes the ability to display results of previous form when linked to from POST form
     * @param firstName first name of user to be entered in the form
     * @param lastName last name of user
     * @param DoB user's date of birth
     * @param email user's email
     * @param password user's password
     * @param passwordConfirm user's confirmed password
     * @param isLastNameOptional is the last name checkbox selected
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf
     * @return thymeleaf demoFormTemplate
     */
    @GetMapping("/register")
    public String form(@RequestParam(name="firstName", required = false, defaultValue = "") String firstName,
                       @RequestParam(name="lastName", required = false, defaultValue = "") String lastName,
                       @RequestParam(name="DoB", required = false, defaultValue = "") LocalDate DoB,
                       @RequestParam(name="email", required = false, defaultValue = "") String email,
                       @RequestParam(name="password", required = false, defaultValue = "") String password,
                       @RequestParam(name="passwordConfirm", required = false, defaultValue = "") String passwordConfirm,
                       @RequestParam(name= "isLastNameOptional", required = false) boolean isLastNameOptional,
                       Model model) {
        logger.info("GET /register");

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("DoB", DoB);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("passwordConfirm", passwordConfirm);
        return "register";
    }

    /**
     * Posts a form response with name and favourite language
     * @param firstName first name of user
     * @param lastName last name of user
     * @param DoB user's date of birth
     * @param email user's email
     * @param password user's password
     * @param passwordConfirm user's repeated password
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf demoFormTemplate
     */
    @PostMapping("/register")
    public String submitForm( HttpServletRequest request,
                              @RequestParam(name="firstName") String firstName,
                              @RequestParam(name="lastName", required = false) String lastName,
                              @RequestParam(name="DoB", required = false) LocalDate DoB,
                              @RequestParam(name="email") String email,
                              @RequestParam(name="password") String password,
                              @RequestParam(name = "passwordConfirm") String passwordConfirm,
                              @RequestParam(name = "isLastNameOptional", required = false) boolean isLastNameOptional,
                              Model model) {
        logger.info("POST /register");

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("DoB", DoB);
        model.addAttribute("email", email);
        model.addAttribute("password", password);

        InputValidationService inputValidator = new InputValidationService(gardenerFormService);
        Optional<String> firstNameError = inputValidator.checkValidName(firstName, "First", false);
        model.addAttribute("firstNameValid", firstNameError.orElse(""));
        Optional<String> lastNameError = inputValidator.checkValidName(lastName, "Last", isLastNameOptional);
        model.addAttribute("lastNameValid", lastNameError.orElse(""));

        Optional<String> DoBError = Optional.empty();
        if (DoB != null) {
            DoBError = inputValidator.checkDoB(DoB);
        }
        model.addAttribute("DoBValid", DoBError.orElse(""));

        Optional<String> validEmailError = inputValidator.checkValidEmail(email);
        Optional<String> emailInUseError = inputValidator.checkEmailInUse(email);
        // emailValid is either the String stored in validEmailError OR ELSE it is equal to the String stored in emailInUseError otherwise its empty
        model.addAttribute("emailValid", validEmailError.orElse(emailInUseError.orElse("")));

        Optional<String> passwordMatchError = inputValidator.checkPasswordsMatch(password, passwordConfirm);
        model.addAttribute("passwordsMatch", passwordMatchError.orElse(""));
        Optional<String> passwordStrengthError = inputValidator.checkStrongPassword(password);
        model.addAttribute("passwordStrong", passwordStrengthError.orElse(""));

        if (firstNameError.isEmpty() &&
                lastNameError.isEmpty() &&
                validEmailError.isEmpty() &&
                emailInUseError.isEmpty() &&
                passwordMatchError.isEmpty() &&
                DoBError.isEmpty() &&
                passwordStrengthError.isEmpty()) {
            Gardener newGardener = new Gardener(firstName, lastName, DoB, email, password, "defaultProfilePic.png");
            newGardener.grantAuthority("ROLE_USER");
            gardenerFormService.addGardener(newGardener);

            // Auto-login when registering
            // Create a new Authentication with Username and Password (authorities here are optional as the following function fetches these anyway)
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password, newGardener.getAuthorities());
            // Authenticate the token properly with the CustomAuthenticationProvider
            Authentication authentication = authenticationManager.authenticate(token);

            // Check if the authentication is actually authenticated (in this example any username/password is accepted so this should never be false)
            if (authentication.isAuthenticated()) {
                logger.info("user is authenticated");
                // Add the authentication to the current security context (Stateful)
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // Add the token to the request session (needed so the authentication can be properly used)
                request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
                return "redirect:user";
            }
        }
        return "register";
    }
}
