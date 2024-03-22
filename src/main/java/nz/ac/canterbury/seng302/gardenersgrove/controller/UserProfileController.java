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
 * Controller for User Profile.
 * Note the @link{Autowired} annotation giving us access to the @link{GardenerFormService} class automatically
 */

@Controller
public class UserProfileController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;

    private Authentication authentication;
    private Gardener gardener;

    @Autowired
    private ImageService imageService;

    @Autowired
    public UserProfileController(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    @GetMapping("/user")
    public String getUserProfile(@RequestParam(name = "firstName", required = false) String firstName,
                                 @RequestParam(name = "lastName", required = false) String lastName,
                                 @RequestParam(name = "DoB", required = false) LocalDate DoB,
                                 @RequestParam(name = "email", required = false) String email,
                                 @RequestParam(name = "isLastNameOptional", required = false) boolean isLastNameOptional,
                                 Model model) {

        logger.info("GET /user");

        authentication = SecurityContextHolder.getContext().getAuthentication();
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
            model.addAttribute("lastName", "");
            model.addAttribute("DoB", "");
            model.addAttribute("email", "");
        }


        // PASTED HERE
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
        // emailValid is either the String stored in validEmailError OR ELSE it is equal to the String stored in emailInUseError otherwise its empty
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

    @GetMapping("/redirectToUserPage")
    public RedirectView profileButton() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: " + authentication);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new RedirectView("/user");
        }
        return new RedirectView("/login");
    }

}