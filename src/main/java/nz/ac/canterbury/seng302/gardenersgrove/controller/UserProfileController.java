package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.MainPageLayout;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for User.
 * Note the @link{Autowired} annotation giving us access to the @link{GardenerFormService} class automatically
 */

@Controller
public class UserProfileController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;
    private final RequestService requestService;
    private final WriteEmail writeEmail;
    private final MainPageLayoutService mainPageLayoutService;
    private Gardener gardener;

    @Autowired
    private ImageService imageService;

    @Autowired
    private RelationshipService relationshipService;

    private boolean isFileNotAdded;

    @Autowired
    public UserProfileController(GardenerFormService gardenerFormService, WriteEmail writeEmail, RequestService requestService, MainPageLayoutService mainPageLayoutService) {
        this.gardenerFormService = gardenerFormService;
        this.writeEmail = writeEmail;
        this.requestService = requestService;
        this.mainPageLayoutService = mainPageLayoutService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication
     * We will always have to check whether the gardener was retrieved in the calling method, so the return type was left as an optional
     * @return An optional of the requested gardener
     */
    public Optional<Gardener> getGardenerFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return gardenerFormService.findByEmail(currentUserEmail);
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
     * @param isDoBInvalid Indication of existence of a partially inputted date e.g. "10/mm/yyyy"
     * @param model (map-like) representation of firstName, lastName, date of birth, email and profile picture for use in thymeleaf
     * @return thymeleaf user profile page or redirect user (to reload page)
     */
    @GetMapping("/user")
    public String getUserProfile(@RequestParam(name = "firstName", required = false) String firstName,
                                 @RequestParam(name = "lastName", required = false) String lastName,
                                 @RequestParam(name = "DoB", required = false) LocalDate DoB,
                                 @RequestParam(name = "email", required = false) String email,
                                 @RequestParam(name = "isLastNameOptional", required = false) boolean isLastNameOptional,
                                 @RequestParam(name = "isDoBInvalid", required = false) boolean isDoBInvalid,
                                 @RequestParam(name = "user", required = false) String user,
                                 HttpServletRequest request,
                                 Model model) {

        logger.info("GET /user");

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();

        model.addAttribute("requestURI", requestService.getRequestURI(request));

        if (gardenerOptional.isPresent()) {
            gardener = gardenerOptional.get();

            if(user != null) {
                Optional<Gardener> friend = gardenerFormService.findById(Long.parseLong(user, 10));
                if(friend.isPresent()) {
                    model.addAttribute("gardener", friend.get()); // add friend details to the model
                    return "unauthorizedUser";
                } else {
                    return "redirect:/user";
                }
            } else {
                model.addAttribute("firstName", gardener.getFirstName());
                model.addAttribute("lastName", gardener.getLastName());
                model.addAttribute("DoB", gardener.getDoB());
                model.addAttribute("email", gardener.getEmail());
                model.addAttribute("profilePic", gardener.getProfilePicture());
            }
        } else {
            model.addAttribute("firstName", "Not Registered");
        }
        if(isLastNameOptional) {
            lastName = null;
        }

        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);

        Optional<String> firstNameError = Optional.empty();
        if (firstName != null) {
            firstNameError = inputValidator.checkValidName(firstName, "First", isLastNameOptional);
            model.addAttribute("firstName", firstName);
        }
        model.addAttribute("firstNameValid", firstNameError.orElse(""));

        Optional<String> lastNameError = Optional.empty();
        if (lastName != null) {
            lastNameError = inputValidator.checkValidName(lastName, "Last", isLastNameOptional);
            model.addAttribute("lastName", lastName);
        }
        model.addAttribute("isLastNameOptional", isLastNameOptional);
        model.addAttribute("lastNameValid", lastNameError.orElse(""));

        Optional<String> DoBError = Optional.empty();
        if (isDoBInvalid) {
            DoBError = Optional.of("Date is not in valid format, DD/MM/YYYY");
        } else if (DoB != null) {
            DoBError = inputValidator.checkDoB(DoB);
        }
        model.addAttribute("DoBValid", DoBError.orElse(""));

        Optional<String> validEmailError = Optional.empty();
        if (email != null) {
            validEmailError = inputValidator.checkValidEmail(email);
            model.addAttribute("email", email);
        }

        Optional<String> emailInUseError = Optional.empty();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        if (email != null && !email.equals(currentUserEmail)) {
            emailInUseError = inputValidator.checkEmailInUse(email);
        }

        if (isFileNotAdded) {
            model.addAttribute("uploadMessage", "No image uploaded.");
        } else {
            model.addAttribute("uploadMessage", "");

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

        MainPageLayout mainPageLayout = mainPageLayoutService.getLayoutByGardenerId(gardener.getId());
        String widgetsEnabled = mainPageLayout.getWidgetsEnabled();

        String[] values = widgetsEnabled.split(" ");
        List<Boolean> selectionList = new ArrayList<>();

        for (String value : values) {
            selectionList.add(value.equals("1"));
        }

        Boolean recentlyAccessedGardens = selectionList.get(0);
        Boolean newestPlants = selectionList.get(1);
        Boolean myGardensList = selectionList.get(2);
        Boolean friendsList = selectionList.get(3);

        model.addAttribute("recentlyAccessedGardens", recentlyAccessedGardens);
        model.addAttribute("newestPlants", newestPlants);
        model.addAttribute("myGardensList", myGardensList);
        model.addAttribute("friendsList", friendsList);

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
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   HttpServletRequest request,
                                   Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("POST /upload");

        if (file.isEmpty()) {
            isFileNotAdded = true;
            return "redirect:/user";
        } else {
            isFileNotAdded = false;
        }

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Optional<String> uploadMessage =  imageService.saveImage(file);
            if (uploadMessage.isEmpty()) {
                return "redirect:/user";
            } else {
                Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
                gardenerOptional.ifPresent(value -> gardener = value);

                model.addAttribute("requestURI", requestService.getRequestURI(request));
                model.addAttribute("uploadMessage", uploadMessage.get());
                model.addAttribute("profilePic", gardenerFormService.findByEmail(authentication.getName()).get().getProfilePicture());
                model.addAttribute("firstName", gardenerFormService.findByEmail(authentication.getName()).get().getFirstName());
                model.addAttribute("lastName", gardenerFormService.findByEmail(authentication.getName()).get().getLastName());
                model.addAttribute("DoB", gardenerFormService.findByEmail(authentication.getName()).get().getDoB());
                model.addAttribute("email", gardenerFormService.findByEmail(authentication.getName()).get().getEmail());
                model.addAttribute("profilePic", gardenerFormService.findByEmail(authentication.getName()).get().getProfilePicture());
                model.addAttribute("firstNameValid", "");
                model.addAttribute("lastNameValid", "");
                model.addAttribute("DoBValid", "");
                model.addAttribute("emailValid", "");

                return "user";
            }
        }
        return "loginForm";
    }

    /**Check whether there is the authentication of current user to change the profile photo.
     * If yes, redirect user to 'user' page with photo uploading function
     * If no, go to 'login' page
     * @return thymeleaf 'user' page or 'login' page
     */
    @GetMapping("/redirectToUserPage")
    public String profileButton() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return "user";
        }
        return "loginForm";
    }

    /**
     * direct a user to 'Update password' page
     * @return 'Update password' page
     */
    @GetMapping("/password")
    public String passwordForm(Model model, HttpServletRequest request) {
        logger.info("GET /password");
        model.addAttribute("requestURI", requestService.getRequestURI(request));
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
                                 HttpServletRequest request,
                                 Model model) {
        logger.info("POST /password");

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);

        model.addAttribute("requestURI", requestService.getRequestURI(request));

        if (gardenerOptional.isEmpty()) {return "loginForm";}

        Optional<String> passwordCorrectError = inputValidator.checkSavedPassword(oldPassword, gardener.getPassword());
        model.addAttribute("passwordCorrect", passwordCorrectError.orElse(""));
        Optional<String> passwordMatchError = inputValidator.checkPasswordsMatch(newPassword, retypePassword);
        model.addAttribute("passwordsMatch", passwordMatchError.orElse(""));
        Optional<String> passwordStrengthError = inputValidator.checkStrongPassword(newPassword);
        model.addAttribute("passwordStrong", passwordStrengthError.orElse(""));
        Optional<String> newPasswordDifferentFromOldPassword = inputValidator.checkOldPasswordDoesNotMatchNewPassword(gardener.getPassword(), newPassword);
        model.addAttribute("newDifferentFromOld", newPasswordDifferentFromOldPassword.orElse(""));


        if (passwordCorrectError.isEmpty() && passwordMatchError.isEmpty() && passwordStrengthError.isEmpty() &&
                newPasswordDifferentFromOldPassword.isEmpty()) {
            gardener.updatePassword(newPassword);
            gardenerFormService.addGardener(gardener);
            writeEmail.sendPasswordUpdateConfirmEmail(gardener);
            return "redirect:/user";
        }

        return "password";
    }
}