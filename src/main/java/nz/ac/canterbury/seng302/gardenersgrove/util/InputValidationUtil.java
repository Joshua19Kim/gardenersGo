package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.controller.BrowseGardensController;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;


import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;
import java.time.Period;

public class InputValidationUtil {

    private final GardenerFormService gardenerFormService;
    Logger logger = LoggerFactory.getLogger(InputValidationUtil.class);


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
     * - 0 < Name <= 64
     * - Allows special characters umlauts, macrons, apostrophes, spaces
     * @param name provided by user input
     * @return empty optional if input is valid, otherwise return error string
     */
    public Optional<String> checkValidName (String name, String firstOrLast, boolean isLastNameOptional) {
        String nameRegex = "[\\p{L}]+((?:[-' ]?\\p{L}+)?)*";
        String result = "";
        if (isLastNameOptional && Objects.equals(firstOrLast, "Last")) {
            return Optional.empty();
        } if (name.length() > 64) {
            result = firstOrLast +" name must " +
                    "be 64 characters long or less <br/>";
        } if (name == null || name.trim().isEmpty()) {
            result += firstOrLast + " name cannot be empty and must only include letters, spaces, " +
                    "hyphens or apostrophes <br/>";
        } else if (!name.matches(nameRegex)) {
            result += firstOrLast + " name cannot be empty and must only include letters, spaces, " +
                    "hyphens or apostrophes <br/>";
        } if (!name.matches("\\p{L}.*")) {
            result += firstOrLast + " name must include at least one letter";
        }
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);
    }


    /**
     * Verifies that email matches IETF guidelines on acceptable addresses
     * @param email provided by user input
     * @return optional empty if passes verification, else optional with error messages
     */
    public Optional<String> checkValidEmail (String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        String result = "";
        if (email.length() > 320) {
            result = "Email address must be 320 characters or less <br/>";
        }
        if(!email.matches(emailRegex)) {
            result += "Email address must be in the form 'jane@doe.nz'";
        }
        if(result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);
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
     * @param DoBString LocalDate object that contains users age
     * @return empty optional if date is valid, otherwise returns Optional error string
     */
    public Optional<String> checkDoB (String DoBString) {
        LocalDate DoB;
        String result = "";
        logger.info("*/*/*/*/*" + DoBString);

        if (!DoBString.isEmpty()) {
            try {
                DoB = LocalDate.parse(DoBString);
                logger.info(DoB + "<-DoB");
            } catch (Exception e) {
                result += "Date is not in valid format, DD/MM/YYYY";
                return Optional.of(result);
            }

            if (Period.between(DoB, LocalDate.now()).getYears() < 13) {
                result = "You must be 13 years or older to create an account <br/>";
            }
            if (Period.between(DoB, LocalDate.now()).getYears() > 120) {
                result += "The maximum age allowed is 120 years";
            }
        }

        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result);

    }



}
