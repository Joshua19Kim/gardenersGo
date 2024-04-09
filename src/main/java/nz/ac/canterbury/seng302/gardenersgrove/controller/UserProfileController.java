package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for User.
 * Note the @link{Autowired} annotation giving us access to the @link{GardenerFormService} class automatically
 */

@Controller
public class UserProfileController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;

    private Gardener gardener;

    @Autowired
    private ImageService imageService;

    @Autowired
    public UserProfileController(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    /**
     * Retrieve user's details (first name, last name, date of birth, email and also check the existence of last name.) based on the current authentication
     * If the edit button is clicked in user.html, all the details will be editable.
     * When the save button is clicked, all the existed/edited details will be checked for validation and saved if all pass the tests.
     * And it will go back to user profile page.
     * @param firstName First name of user to be entered on User profile form
     * @param lastName Last name of user to be entered on User profile form
     * @param DoB Date of Birth of user to be entered on User profile form
     * @param email Email of Birth of user to be entered on User profile form
     * @param isLastNameOptional Indication of existence of user's last name
     * @param model (map-like) representation of firstName, lastName, date of birth, email and profile picture for use in thymeleaf
     * @return thymeleaf user profile page or redirect user (to reload page)
     */
    @GetMapping("/user")
    public String getUserProfile(@RequestParam(name = "firstName", required = false) String firstName,
                                 @RequestParam(name = "lastName", required = false) String lastName,
                                 @RequestParam(name = "DoB", required = false) LocalDate DoB,
                                 @RequestParam(name = "email", required = false) String email,
                                 @RequestParam(name = "isLastNameOptional", required = false) boolean isLastNameOptional,
                                 Model model) {

        logger.info("GET /user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
        if (gardenerOptional.isPresent()) {
            gardener = gardenerOptional.get();
            model.addAttribute("firstName", gardener.getFirstName());
            model.addAttribute("lastName", gardener.getLastName());
            model.addAttribute("DoB", gardener.getDoB());
            model.addAttribute("email", gardener.getEmail());
            model.addAttribute("profilePic", gardener.getProfilePicture());
        } else {
            model.addAttribute("firstName", "Not Registered");
        }

        InputValidationService inputValidator = new InputValidationService(gardenerFormService);

        Optional<String> firstNameError = Optional.empty();
        if (firstName != null) {
            firstNameError = inputValidator.checkValidName(firstName, "First", isLastNameOptional);
        }
        model.addAttribute("firstNameValid", firstNameError.orElse(""));

        Optional<String> lastNameError = Optional.empty();
        if (lastName != null) {
            lastNameError = inputValidator.checkValidName(lastName, "Last", isLastNameOptional);
        }
        model.addAttribute("lastNameValid", lastNameError.orElse(""));

        Optional<String> DoBError = Optional.empty();
        if (DoB != null) {
            DoBError = inputValidator.checkDoB(DoB);
        }
        model.addAttribute("DoBValid", DoBError.orElse(""));

        Optional<String> validEmailError = Optional.empty();
        if (email != null) {
            validEmailError = inputValidator.checkValidEmail(email);
        }

        Optional<String> emailInUseError = Optional.empty();
        if (email != null && !email.equals(currentUserEmail)) {
            emailInUseError = inputValidator.checkEmailInUse(email);
        }
        model.addAttribute("emailValid", validEmailError.orElse(emailInUseError.orElse("")));

        if (firstNameError.isEmpty() &&
                lastNameError.isEmpty() &&
                DoBError.isEmpty() &&
                validEmailError.isEmpty() &&
                emailInUseError.isEmpty()) {
            if (firstName != null || lastName != null || DoB != null || email != null) {
                gardener.setFirstName(firstName);
                gardener.setLastName(lastName);
                gardener.setEmail(email);
                gardener.setDoB(DoB);
                gardenerFormService.addGardener(gardener);
                // Re-authenticates user to catch case when they change their email
                Authentication newAuth = new UsernamePasswordAuthenticationToken(gardener.getEmail(), gardener.getPassword(), gardener.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication((newAuth));
                return "redirect:/user";
            }
        }
        return "user";
    }

    /**
     * Check whether there is the authentication of current user to change the profile photo.
     * If yes,read the uploaded file from user.html and Save the file.
     * If the file is empty, redirect user to 'user' page with existing image(or default photo).
     * If there is an image file, go back to 'user' page with new image
     * @param file the file of profile picture
     * @param model (map-like) representation of profile picture for use in thymeleaf
     * @return thymeleaf 'user' page after updating successfully to reload user's details, otherwise thymeleaf login page
     */
    @PostMapping("/user")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("POST /upload");

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Optional<String> uploadMessage =  imageService.saveImage(file);
            if (uploadMessage.isEmpty()) {
                return "redirect:/user";
            } else {
                model.addAttribute("uploadMessage", uploadMessage.get());
                model.addAttribute("profilePic", gardenerFormService.findByEmail(authentication.getName()).get().getProfilePicture());
                return "/user";
            }
        }
        return "/login";
    }

    /**Check whether there is the authentication of current user to change the profile photo.
     * If yes, redirect user to 'user' page with photo uploading function
     * If no, go to 'login' page
     * @return thymeleaf 'user' page or 'login' page
     */
    @GetMapping("/redirectToUserPage")
    public RedirectView profileButton() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: " + authentication);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new RedirectView("/user");
        }
        return new RedirectView("/login");
    }

    /**
     * direct a user to 'Update password' page
     * @return 'Update password' page
     */
    @GetMapping("/password")
    public String passwordForm() {
        logger.info("GET /password");
        return "password";
    }

    /**
     * Posts a password form with old password and new password.
     * Authentication will be checked first to compare with the password in the server, then
     * new password and retyped password will be checked for validation and whether they are matching or not.
     * If no error message comes up, new password will be saved.
     * @param oldPassword old password to be entered in the form
     * @param newPassword new password to be entered in the form
     * @param retypePassword retype password to be entered in the form
     * @param model (map-like) representation of passwords in thymeleaf
     * @return thymeleaf 'User' page if all the inputs are valid, otherwise stay on 'Update password' page
     */
    @PostMapping("/password")
    public String updatePassword(@RequestParam(name = "oldPassword", required = false) String oldPassword,
                              @RequestParam(name = "newPassword", required = false) String newPassword,
                              @RequestParam(name = "retypePassword", required = false) String retypePassword,
                              Model model) {
        logger.info("POST /password");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
        InputValidationService inputValidator = new InputValidationService(gardenerFormService);

        if (gardenerOptional.isEmpty()) {return "/login";}

        gardener = gardenerOptional.get();
        Optional<String> passwordCorrectError = inputValidator.checkSavedPassword(oldPassword, gardener.getPassword());
        model.addAttribute("passwordCorrect", passwordCorrectError.orElse(""));
        Optional<String> passwordMatchError = inputValidator.checkPasswordsMatch(newPassword, retypePassword);
        model.addAttribute("passwordsMatch", passwordMatchError.orElse(""));
        Optional<String> passwordStrengthError = inputValidator.checkStrongPassword(newPassword);
        model.addAttribute("passwordStrong", passwordStrengthError.orElse(""));

        if (passwordCorrectError.isEmpty() && passwordMatchError.isEmpty() && passwordStrengthError.isEmpty()) {
            gardener.updatePassword(newPassword);
            gardenerFormService.addGardener(gardener);
            // Re-authenticates user to catch case when they change their email
            Authentication newAuth = new UsernamePasswordAuthenticationToken(gardener.getEmail(), gardener.getPassword(), gardener.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication((newAuth));
            return "redirect:/user";
        }

        return "password";
    }
}