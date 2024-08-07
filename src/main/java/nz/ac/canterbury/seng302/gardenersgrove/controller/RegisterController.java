package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.MainPageLayout;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.MainPageLayoutService;
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
    private final MainPageLayoutService mainPageLayoutService;
    Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * Constructor for the controller. Sets the gardener form service and authentication manager objects
     *
     * @param gardenerFormService - object that is used to interact with the database
     * @param writeEmail          - service for writing emails
     */
    @Autowired
    public RegisterController(GardenerFormService gardenerFormService, TokenService tokenService, WriteEmail writeEmail, MainPageLayoutService mainPageLayoutService) {
        this.gardenerFormService = gardenerFormService;
        this.tokenService = tokenService;
        this.writeEmail = writeEmail;
        this.mainPageLayoutService = mainPageLayoutService;
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
        return "registerTemplate";
    }

    /**
     * Posts a form response with a new Gardener
     *
     * @param firstName       first name of user
     * @param lastName        last name of user
     * @param DoB             user's date of birth
     * @param email           user's email
     * @param password        user's password
     * @param passwordConfirm user's repeated password
     * @param isLastNameOptional Indication of existence of user's last name
     * @param isDoBInvalid Indication of existence of a partially inputted date e.g. "10/mm/yyyy"
     * @param model           (map-like) representation of name, language and isJava boolean for use in thymeleaf,
     *                        with values being set to relevant parameters provided
     * @return thymeleaf registration form template or redirect to signup confirmation page
     */
    @PostMapping("/register")
    public String submitForm(@RequestParam(name="firstName") String firstName,
                             @RequestParam(name="lastName", required = false) String lastName,
                             @RequestParam(name="DoB", required = false) LocalDate DoB,
                             @RequestParam(name="email") String email,
                             @RequestParam(name="password") String password,
                             @RequestParam(name = "passwordConfirm") String passwordConfirm,
                             @RequestParam(name = "isLastNameOptional", required = false) boolean isLastNameOptional,
                             @RequestParam(name = "isDoBInvalid", required = false) boolean isDoBInvalid,
                             Model model) {
        logger.info("POST /register");

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("DoB", DoB);
        model.addAttribute("email", email);
        model.addAttribute("password", password);

        if (isLastNameOptional) {
            model.addAttribute("isLastNameOptional", isLastNameOptional);
        }

        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);
        Optional<String> firstNameError = inputValidator.checkValidName(firstName, "First", false);
        model.addAttribute("firstNameValid", firstNameError.orElse(""));
        Optional<String> lastNameError = Optional.empty();
        if (!isLastNameOptional) {
            lastNameError = inputValidator.checkValidName(lastName, "Last", false);
            model.addAttribute("lastNameValid", lastNameError.orElse(""));
        }
        else if (lastName != null && !lastName.isEmpty()) {
            lastNameError = Optional.of("You cannot enter a last name");
            model.addAttribute("lastNameValid", "You cannot enter a last name");
        }

        Optional<String> DoBError = Optional.empty();

        if (isDoBInvalid) {
            DoBError = Optional.of("Date is not in valid format, DD/MM/YYYY");
        } else if (DoB != null) {
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
                (lastNameError.isEmpty())  &&
                validEmailError.isEmpty() &&
                emailInUseError.isEmpty() &&
                passwordMatchError.isEmpty() &&
                DoBError.isEmpty() &&
                passwordStrengthError.isEmpty()) {

            Gardener newGardener = new Gardener(firstName, lastName, DoB, email, password);
            gardenerFormService.addGardener(newGardener);
            Optional<Gardener> optionalGardner = gardenerFormService.getUserByEmailAndPassword(email, password);
            Gardener savedGardener = null;
            if (optionalGardner.isPresent()) {
                savedGardener = optionalGardner.get();
            }
            MainPageLayout mainPageLayout = new MainPageLayout(savedGardener);
            mainPageLayoutService.addMainPageLayout(mainPageLayout);
            writeEmail.sendSignupEmail(newGardener, tokenService);
            return "redirect:/signup";


        }
        return "registerTemplate";
    }
}
