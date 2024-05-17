package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ForgotPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class InputValidationUtilTest {

    private final GardenerFormService gardenerFormService;
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @MockBean
    private ForgotPasswordFormController forgotPasswordFormController;

    @MockBean
    private EmailUserService emailUserService;


    @Autowired
    public InputValidationUtilTest(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    @Test
    void testMatchingPassword() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password1 = "password";
        String password2 = "password";
        Optional<String> isValid = validate.checkPasswordsMatch(password1, password2);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testDifferentCapitalisation() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password1 = "password";
        String password2 = "PASSWORD";
        Optional<String> isValid = validate.checkPasswordsMatch(password1, password2);
        assertTrue(isValid.get().matches("Passwords do not match."));
    }

    @Test
    void testDifferentPassword() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password1 = "HelloWorld";
        String password2 = "ByeMoon";
        Optional<String> isValid = validate.checkPasswordsMatch(password1, password2);
        assertTrue(isValid.get().matches("Passwords do not match."));
    }

    @Test
    void testNullPassword() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongPasswordNoSpecial() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "morethaneight";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongSpecialNoLetter() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "!@#$%^&**()";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongSpecialNoLowercase() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "ABCDEG!@#$";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));

    }

    @Test
    void testLongSpecialAlphaNoNumber() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "ABCDEG!@#$aa";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.get().matches("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));
    }
    @Test
    void testInvalidPassword7Characters() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "AB23sd!";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertEquals(isValid.get(), "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
    }

    @Test
    void testValidPassword8Characters() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "ABC12sd!";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testValidPassword9Characters() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "ABC123sd!";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testInvalidPassword256CharactersTooLong() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "Password1!Password1!Password1!Password1!Password1!Password1!" +
                "Password1!Password1!Password1!Password1!Password1!Password1!Password1!" +
                "Password1!Password1!Password1!Password1!Password1!Password1!Password1!" +
                "Password1!Password1!Password1!Password1!Password1!Passwo";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertFalse(isValid.isEmpty());
    }

    @Test
    void testValidPassword255Characters() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String password = "Password1!Password1!Password1!Password1!Password1!Password1!" +
                "Password1!Password1!Password1!Password1!Password1!Password1!Password1!" +
                "Password1!Password1!Password1!Password1!Password1!Password1!Password1!" +
                "Password1!Password1!Password1!Password1!Password1!Passw";
        Optional<String> isValid = validate.checkStrongPassword(password);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testValidFirstName() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "Brad";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testValidFirstNameHyphens() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "A-B-C";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testInvalidFirstNameHyphens() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "---";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertFalse(isValid.isEmpty());
    }

    @Test
    void testInvalidFirstNameEndWithHyphen() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "Sam-";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertFalse(isValid.isEmpty());
    }

    @Test
    void testInvalidFirstNameMultipleSpaces() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "Sam   bad name";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertFalse(isValid.isEmpty());
    }

    @Test
    void testInvalidFirstNameMultipleApostrophe() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "Sa''''test";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertFalse(isValid.isEmpty());
    }

    @Test
    void testInvalidFirstNameMultipleTypesConsecutive() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "Sa!-'test";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertFalse(isValid.isEmpty());
    }

    @Test
    void testValidFirstNameSingleApostrophe() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String nameToTest = "Sa'test'asd";
        String first = "First";
        boolean lastNameNeeded = false;
        Optional<String> isValid = validate.checkValidName(nameToTest, first, lastNameNeeded);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testLongName() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "Thisnameisdefinatelylongerthansixtyfourcharactersanditisgoingtofailthetesthahahahahahahahahahah";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first +" name must be 64 characters long or less"));
    }

    @Test
    void testNullFirstName() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces, " +
                "hyphens or apostrophes"));
    }

    @Test
    void testNullLastNameCheckboxUnselected() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "";
        String first  = "Last";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces, " +
                "hyphens or apostrophes"));
    }

    @Test
    void testNullLastNameCheckboxSelected() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "";
        String last  = "Last";
        boolean lastName = true;
        Optional<String> isValid = validate.checkValidName(name, last, lastName);
        assertTrue(isValid.isEmpty());
    }


    @Test
    void testValidSpecialCharactersName() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "ÄÖÜäöüßĀĒĪŌŪāēīōū";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testInvalidSpecialCharacterName() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "%";
        String first = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces, " +
                "hyphens or apostrophes"));
    }

    @Test
    void testNumbersInEmail() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "sky123@yahoo.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testNormalEmail() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "sda110@uclive.ac.nz";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testValidEmail320CharactersTotal() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdab" +
                "cdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd" +
                "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdab" +
                "cdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd" +
                "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdab" +
                "cdabcdabcdabcdabcdabcdabcdab@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testInvalidEmail321CharactersTotal() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdab" +
                "cdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd" +
                "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdab" +
                "cdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd" +
                "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdab" +
                "cdabcdabcdabcdabcdabcdabcdabc@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        logger.info(isValid.toString());
        assertFalse(isValid.isEmpty());
    }



    @Test
    void testInvalidSpecialCharacterEmail() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "$@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }


    @Test
    void testStartWithPeriod() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = ".failure@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void testEndWithPeriod() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "failure.@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void ConsecutivePeriods() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "this...fails@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void testValidHyphenInSuffix() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "username@hot-mail.co.nz";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testSuffixLongerThanSeven() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "longsuffix@uc.christchurch";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form ‘jane@doe.nz"));
    }

    @Test
    void testSamePasswordWithSavedPassword() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordEntered = "PassworD1@";
        String hashedPasswordInServer = encoder.encode("PassworD1@");
        Optional<String> isValid = validate.checkSavedPassword(passwordEntered, hashedPasswordInServer);
        assertTrue(isValid.isEmpty());
    }

    @Test
    void testDifferentPasswordWithSavedPassword() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordEntered = "PassworD1@";
        String hashedPasswordInServer = encoder.encode("PassworD2@");
        Optional<String> isValid = validate.checkSavedPassword(passwordEntered, hashedPasswordInServer);
        assertTrue(isValid.get().matches("Your old password is incorrect."));
    }
}
