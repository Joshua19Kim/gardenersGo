package nz.ac.canterbury.seng302.gardenersgrove;

import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class InputValidationServiceTest {

    private final GardenerFormService gardenerFormService;

    @Autowired
    public InputValidationServiceTest(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    @Test
    void testMatchingPassword() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password1 = "password";
        String password2 = "password";
        Optional<String> isValid = validate.checkPasswordsMatch(password1, password2);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testDifferentCapitalisation() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password1 = "password";
        String password2 = "PASSWORD";
        Optional<String> isValid = validate.checkPasswordsMatch(password1, password2);
        assertTrue(isValid.get().matches("Passwords do not match."));
    }

    @Test
    void testDifferentPassword() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password1 = "HelloWorld";
        String password2 = "ByeMoon";
        Optional<String> isValid = validate.checkPasswordsMatch(password1, password2);
        assertTrue(isValid.get().matches("Passwords do not match."));
    }

    @Test
    void testNullPassword() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password = "";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongPasswordNoSpecial() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password = "morethaneight";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongSpecialNoLetter() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password = "!@#$%^&**()";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongSpecialNoLowercase() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password = "ABCDEG!@#$";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongSpecialAlphaNoNumber() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password = "ABCDEG!@#$aa";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testValidPassword() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String password = "ABC123sd!";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testValidFirstName() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String name = "Brad";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.isEmpty());
    }

//    @Test
//    void testLongName() {
//        InputValidationService validate = new InputValidationService(gardenerFormService);
//        String name = "Thisnameisdefinatelylongerthansixtyfourcharactersanditisgoingtofailthetest";
//        String first  = "First";
//        boolean lastName = false;
//        Optional<String> isValid = validate.checkValidName(name, first, lastName);
//        assertTrue(isValid.get().matches(first +" name must be 64 characters long or less"));
//    }

    @Test
    void testNullFirstName() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String name = "";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces,\n" +
                "hyphens or apostrophes"));
    }

    @Test
    void testNullLastNameCheckboxUnselected() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String name = "";
        String first  = "Last";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces,\n" +
                "hyphens or apostrophes"));
    }

    @Test
    void testNullLastNameCheckboxSelected() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String name = "";
        String last  = "Last";
        boolean lastName = true;
        Optional<String> isValid = validate.checkValidName(name, last, lastName);
        assertTrue(isValid.isEmpty());
    }


    @Test
    void testValidSpecialCharactersName() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String name = "ÄÖÜäöüßĀĒĪŌŪāēīōū-' ";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testInvalidSpecialCharacterName() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String name = "%";
        String first = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces,\n" +
                "hyphens or apostrophes"));
    }

    @Test
    void testNumbersInEmail() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "sky123@yahoo.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testNormalEmail() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "sda110@uclive.ac.nz";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testInvalidSpecialCharacterEmail() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "$@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }


    @Test
    void testStartWithPeriod() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = ".failure@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void testEndWithPeriod() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "failure.@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void ConsecutivePeriods() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "this...fails@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void testValidHyphenInSuffix() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "username@hot-mail.co.nz";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testSuffixLongerThanSeven() {
        InputValidationService validate = new InputValidationService(gardenerFormService);
        String email = "longsuffix@uc.christchurch";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }



}
