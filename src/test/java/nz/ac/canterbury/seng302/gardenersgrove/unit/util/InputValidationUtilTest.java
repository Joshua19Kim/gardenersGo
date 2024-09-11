package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputValidationUtilTest {

    @MockBean
    private GardenerFormService gardenerFormService;

    private InputValidationUtil inputValidationUtil;

    @BeforeEach
    public void setUp() {
        inputValidationUtil = new InputValidationUtil(gardenerFormService);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "😎@example.com: Email address must be in the form 'jane@doe.nz'",
            "💌@mail.com: Email address must be in the form 'jane@doe.nz'",
            "😊: Email address must be in the form 'jane@doe.nz'",
            "user😜@domain.com: Email address must be in the form 'jane@doe.nz'",
            "🤖@robotics.ai: Email address must be in the form 'jane@doe.nz'",
            "💌💌💌💌💌💌💌💌💌💌💌💌: Email address must be in the form 'jane@doe.nz'"
    }, delimiter = ':')
    public void emailEmojiValidation(String email, String expectedMessage) {
        Optional<String> actualMessage = inputValidationUtil.checkValidEmail(email);
        Assertions.assertEquals(expectedMessage, actualMessage.get());
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
        assertTrue(isValid.get().matches(first +" name must be 64 characters long or less <br/>"));
    }

    @Test
    void testNullFirstName() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "";
        String first  = "First";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces, " +
                "hyphens or apostrophes <br/>" + first + " name must include at least one letter"));
    }

    @Test
    void testNullLastNameCheckboxUnselected() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "";
        String first  = "Last";
        boolean lastName = false;
        Optional<String> isValid = validate.checkValidName(name, first, lastName);
        assertTrue(isValid.get().matches(first + " name cannot be empty and must only include letters, spaces, " +
                "hyphens or apostrophes <br/>" + first + " name must include at least one letter"));
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
                "hyphens or apostrophes <br/>" + first + " name must include at least one letter"));
    }

    @Test
    public void FirstNameEntered_MultipleErrors_MultipleErrorMessagesReturned() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String name = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
        String first = "First";
        boolean lastName = false;
        Optional<String> actualMessage = validate.checkValidName(name, first, lastName);
        Optional<String> expectedMessage = Optional.of("First name must be 64 characters long or less <br/>" +
                "First name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>" + first + " name must include at least one letter");
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void EmailEntered_MultipleErrors_MultipleErrorMessagesReturned() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
        Optional<String> actualMessage = validate.checkValidEmail(email);
        Optional<String> expectedMessage = Optional.of("Email address must be 320 characters or less <br/>" +
                "Email address must be in the form 'jane@doe.nz'");
        assertEquals(expectedMessage,actualMessage);
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
        assertFalse(isValid.isEmpty());
    }



    @Test
    void testInvalidSpecialCharacterEmail() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "$@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form 'jane@doe.nz'"));
    }


    @Test
    void testStartWithPeriod() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = ".failure@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form 'jane@doe.nz'"));
    }

    @Test
    void testEndWithPeriod() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "failure.@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form 'jane@doe.nz'"));
    }

    @Test
    void ConsecutivePeriods() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String email = "this...fails@gmail.com";
        Optional<String> isValid = validate.checkValidEmail(email);
        assertTrue(isValid.get().matches("Email address must be in the form 'jane@doe.nz'"));
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
        assertTrue(isValid.get().matches("Email address must be in the form 'jane@doe.nz'"));
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

    @Test
    void testValidEmptyDateString() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String validDateString = "";
        Optional<String> isValidDate = validate.checkDoB(validDateString);
        assertTrue(isValidDate.get().matches(""));
    }

    /* cannot test invalid date strings as HTML validates for us*/

    @Test
    void testValidCompleteDateString() {
        InputValidationUtil validate = new InputValidationUtil(gardenerFormService);
        String validDateString = "2000-12-30";
        Optional<String> isValidDate = validate.checkDoB(validDateString);
        assertTrue(isValidDate.isEmpty());
    }
}
