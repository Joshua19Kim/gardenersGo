package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.util.Optional;
import java.time.LocalDate;
import java.time.Period;

public class InputValidationService {

    private final GardenerFormService gardenerFormService;

    public InputValidationService(GardenerFormService gardenerFormService) {
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

    /**
     * Verify password entered is strong as given by U1-AC12
     * @param password attempted by user
     * @return true if strong, false if weak
     */
    public Optional<String> checkStrongPassword (String password) {
        String validRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,}$";
        return (password.matches(validRegex) ? Optional.empty() : Optional.of("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));
    }

    /**
     * Verify name user has input passes conditions:
     * - 0 < Name < 64
     * - Allows special characters umlauts, macrons, apostrophes, spaces
     * @param name provided by user input
     * @return true if passes verification
     */
    public Optional<String> checkValidName (String name, String firstOrLast, boolean isLastNameOptional) {
        String nameRegex = "^[A-Za-zÄÖÜäöüßĀĒĪŌŪāēīōū]+[A-Za-zÄÖÜäöüßĀĒĪŌŪāēīōū' -]*$";
        if (isLastNameOptional) {
            return Optional.empty();
        } else if (name.length() > 64) {
            return Optional.of(firstOrLast +" name must\n" +
                    "be 64 characters long or less");
        } else if (!name.matches(nameRegex)) {
            return Optional.of(firstOrLast + " name cannot be empty and must only include letters, spaces,\n" +
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
        return (email.matches(emailRegex) ? Optional.empty() : Optional.of("Email address must be in the form ‘jane@doe.nz"));
    }

    /**
     * Checks if the email is already in use
     * @param email provided by user input
     * @return true if passes verification
     */
    public Optional<String> checkEmailInUse(String email) {
        return (gardenerFormService.findByEmail(email).isPresent() ? Optional.of("This email address is already in use") : Optional.empty());
    }

    /** Verifies that the user is old enough to register (13 years or more)
     * @param DoB
     * @return true if user is old enough
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
