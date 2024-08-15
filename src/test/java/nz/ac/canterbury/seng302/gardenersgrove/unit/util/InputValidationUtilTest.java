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

import java.util.Optional;

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
}
