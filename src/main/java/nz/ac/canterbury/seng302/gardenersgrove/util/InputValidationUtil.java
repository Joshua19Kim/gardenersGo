package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;
import java.time.Period;

public class InputValidationUtil {

    private final GardenerFormService gardenerFormService;

    public InputValidationUtil(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    /**
     * Verify correctness of password entry by checking if match
     * @param passwordOne first password entered in form
     * @param passwordTwo second pass entered in form
     * @return boolean true for successful match or false if passwords do not match
     */
    public Optional<String> checkPasswordsMatch (String passwordOne, String passwordTwo) {
        return (passwordOne.equals(passwordTwo)) ? Optional.empty() : Optional.of("Passwords do not match.");
    }

    public Optional<String> checkOldPasswordDoesNotMatchNewPassword (String oldPassword, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return (encoder.matches(newPassword, oldPassword)) ? Optional.of("New password cannot be the same as old password.") : Optional.empty();
    }

    /**
     * Verify password entered is strong as given by U1-AC12
     * @param password attempted by user
     * @return empty optional if password is strong, otherwise error string
     */
    public Optional<String> checkStrongPassword (String password) {
        String validRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,255}$";
        return (password.matches(validRegex) ? Optional.empty() : Optional.of("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));
    }

    /**
     * Verify password entered is matching to the saved password in database
     * @param password password entered in form
     * @param hashedPasswordInServer password called from the database
     * @return empty optional if password is correct, otherwise error string
     */
    public Optional<String> checkSavedPassword (String password, String hashedPasswordInServer) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return (encoder.matches(password, hashedPasswordInServer)) ? Optional.empty() : Optional.of("Your old password is incorrect.");
    }

    /**
     * Verify name user has input passes conditions:
     * - 0 < Name < 64
     * - Allows special characters umlauts, macrons, apostrophes, spaces
     * @param name provided by user input
     * @return empty optional if input is valid, otherwise return error string
     */
    public Optional<String> checkValidName (String name, String firstOrLast, boolean isLastNameOptional) {
        String nameRegex = "[\\p{L}]+((?:[-' ]?\\p{L}+)?)*";
        if (isLastNameOptional && Objects.equals(firstOrLast, "Last")) {
            return Optional.empty();
        } else if (name.length() > 64) {
            return Optional.of(firstOrLast +" name must " +
                    "be 64 characters long or less");
        } else if (name == null || name.trim().isEmpty()) {
            return Optional.of(firstOrLast + " name cannot be empty and must only include letters, spaces, " +
                    "hyphens or apostrophes");
        } else if (!name.matches(nameRegex)) {
            return Optional.of(firstOrLast + " name cannot be empty and must only include letters, spaces, " +
                    "hyphens or apostrophes");
        } else {
            return Optional.empty();
        }
    }


    /**
     * Verifies that email matches IETF guidelines on acceptable addresses
     * @param email provided by user input
     * @return true if passes verification
     */
    public Optional<String> checkValidEmail (String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email.length() > 320) {
            return Optional.of("Email address must be 320 characters or less");
        } else {
            return (email.matches(emailRegex) ? Optional.empty() : Optional.of("Email address must be in the form ‘jane@doe.nz"));
        }
    }

    /**
     * Checks if the email is already in use
     * @param email provided by user input
     * @return empty optional if email is valid otherwise returns an Optional error string
     */
    public Optional<String> checkEmailInUse(String email) {
        return (gardenerFormService.findByEmail(email).isPresent() ? Optional.of("This email address is already in use") : Optional.empty());
    }

    /** Verifies that the user is old enough to register (13 years or more)
     * @param DoB LocalDate object that contains users age
     * @return empty optional if date is valid, otherwise returns Optional error string
     */
    public Optional<String> checkDoB (LocalDate DoB) {
        if (Period.between(DoB, LocalDate.now()).getYears() < 13) {
            return Optional.of("You must be 13 years or older to create an account");
        } else {
            return (Period.between(DoB, LocalDate.now()).getYears() > 120 ?
                    Optional.of("The maximum age allowed is 120 years") : Optional.empty());
        }
    }



}
