package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for user registration
 * Handles the registration form submission and validation
 * Note the @link{Autowired} annotation giving us access to the @link{FormService} class automatically
 */
@Controller
public class RegisterController {
    private final GardenerFormService gardenerFormService;
    private final TokenService tokenService;
    private final WriteEmail writeEmail;
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * Constructor for the controller. Sets the gardener form service and authentication manager objects
     *
     * @param gardenerFormService - object that is used to interact with the database
     * @param writeEmail          - service for writing emails
     */
    @Autowired
    public RegisterController(GardenerFormService gardenerFormService, TokenService tokenService, WriteEmail writeEmail) {
        this.gardenerFormService = gardenerFormService;
        this.tokenService = tokenService;
        this.writeEmail = writeEmail;
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
     * Posts a form response with a new Gardener
     * @param firstName first name of user
     * @param lastName last name of user
     * @param DoB user's date of birth
     * @param email user's email
     * @param password user's password
     * @param passwordConfirm user's repeated password
     * @param model (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *              with values being set to relevant parameters provided
     * @return thymeleaf registration form template or redirect to signup confirmation page
     */
    @PostMapping("/register")
    public String submitForm(HttpServletRequest request,
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

        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);
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
                (lastNameError.isEmpty() || isLastNameOptional)  &&
                validEmailError.isEmpty() &&
                emailInUseError.isEmpty() &&
                passwordMatchError.isEmpty() &&
                DoBError.isEmpty() &&
                passwordStrengthError.isEmpty()) {

            Gardener newGardener = new Gardener(firstName, lastName, DoB, email, password);
            gardenerFormService.addGardener(newGardener);
            writeEmail.sendSignupEmail(newGardener, tokenService);
            return "redirect:/signup";


        }
        return "register";
    }
}
