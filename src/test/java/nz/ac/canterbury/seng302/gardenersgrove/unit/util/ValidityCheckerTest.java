package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ValidityCheckerTest {
    @Test
    public void NameEntered_AlphaNumericInput_NameReturned() {
        String input = "JackErsk1ne";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("JackErsk1ne", returnedInput);
    }
    @Test
    public void NameEntered_SpaceInput_NameReturned() {
        String input = "a a";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("a a", returnedInput);
    }
    @Test
    public void NameEntered_CommaInput_NameReturned() {
        String input = ",";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals(",", returnedInput);
    }
    @Test
    public void NameEntered_DotInput_NameReturned() {
        String input = ".";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals(".", returnedInput);
    }
    @Test
    public void NameEntered_HyphenInput_NameReturned() {
        String input = "-";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("-", returnedInput);
    }
    @Test
    public void NameEntered_ApostropheInput_NameReturned() {
        String input = "'";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("'", returnedInput);
    }
    @Test
    public void NameEntered_EmptyInput_ErrorMessageReturned() {
        String input = "";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("Garden name cannot be empty", returnedInput);
    }
    @Test
    public void NameEntered_EmptySpaceInput_ErrorMessageReturned() {
        String input = " ";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("Garden name cannot be empty", returnedInput);
    }
    @Test
    public void NameEntered_InvalidInput_ErrorMessageReturned() {
        String input = "J@(K_Er$K!^e";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes <br/>", returnedInput);
    }

    @Test
    public void GardenNameEntered_MultipleErrors_MultipleErrorMessagesReturned() {
        String input = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
        String returnedInput = ValidityChecker.validateGardenName(input);
        Assertions.assertEquals("Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes <br/>" +
                "Garden name must be less than 64 characters", returnedInput);
    }

    @Test
    public void SizeEntered_EmptyInput_EmptyInputReturned() {
        String input = "";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("", returnedInput);
    }
    @Test
    public void SizeEntered_IntegerInput_EmptyInputReturned() {
        String input = "1";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("1", returnedInput);
    }
    @Test
    public void SizeEntered_DecimalInput_EmptyInputReturned() {
        String input = "0.1";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("0.1", returnedInput);
    }
    @Test //should work
    public void SizeEntered_CommaInput_EmptyInputReturned() {
        String input = "0,1";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("0.1", returnedInput);
    }
    @Test // should work
    public void SizeEntered_NegativeInput_ErrorMessageReturned() {
        String input = "-1";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("Garden size must be a positive number", returnedInput);
    }
    @Test
    public void SizeEntered_AlphaInput_ErrorMessageReturned() {
        String input = "One";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("Garden size must be a positive number", returnedInput);
    }
    @Test //should fail
    public void SizeEntered_MultipleDotInput_ErrorMessageReturned() {
        String input = "1.1.";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("Garden size must be a positive number", returnedInput);
    }
    @Test // should fail
    public void SizeEntered_MultipleCommaInput_ErrorMessageReturned() {
        String input = "1,1,";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("Garden size must be a positive number", returnedInput);
    }
    @Test //should fail
    public void SizeEntered_LargeInput_ErrorMessageReturned() {
        String input = "10000001";
        String returnedInput = ValidityChecker.validateGardenSize(input);
        Assertions.assertEquals("Garden size must be less than 10 Million", returnedInput);
    }

    @Test
    public void PlantNameEntered_AlphaNumericInput_PlantNameReturned() {
        String input = "JackErsk1ne";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("JackErsk1ne", returnedInput);
    }
    @Test
    public void PlantNameEntered_SpaceInput_PlantNameReturned() {
        String input = "a a";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("a a", returnedInput);
    }
    @Test
    public void PlantNameEntered_CommaInput_PlantNameReturned() {
        String input = ",";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals(",", returnedInput);
    }
    @Test
    public void PlantNameEntered_DotInput_PlantNameReturned() {
        String input = ".";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals(".", returnedInput);
    }
    @Test
    public void PlantNameEntered_HyphenInput_PlantNameReturned() {
        String input = "-";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("-", returnedInput);
    }
    @Test
    public void PlantNameEntered_ApostropheInput_PlantNameReturned() {
        String input = "'";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("'", returnedInput);
    }
    @Test
    public void PlantNameEntered_EmptyInput_ErrorMessageReturned() {
        String input = "";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>", returnedInput);
    }
    @Test
    public void PlantNameEntered_EmptySpaceInput_ErrorMessageReturned() {
        String input = " ";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>", returnedInput);
    }
    @Test
    public void PlantNameEntered_InvalidInput_ErrorMessageReturned() {
        String input = "J@(K_Er$K!^e";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>", returnedInput);
    }

    @Test
    public void PlantNameEntered_NameTooLongAndInvalid_MultipleErrorMessagesReturned() {
        String input = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean@";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("Plant name cannot be empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>Plant name must be less than or equal to 64 characters", returnedInput);
    }

    @Test
    public void PlantCountEntered_EmptyInput_EmptyInputReturned() {
        String input = "";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("", returnedInput);
    }
    @Test
    public void PlantCountEntered_IntegerInput_PlantCountReturned() {
        String input = "1";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("1", returnedInput);
    }
    @Test
    public void PlantCountEntered_DecimalInput_PlantCountReturned() {
        String input = "0.1";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("0.1", returnedInput);
    }
    @Test //should work
    public void PlantCountEntered_CommaInput_PlantCountReturned() {
        String input = "0,1";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("0.1", returnedInput);
    }
    @Test // should work
    public void PlantCountEntered_NegativeInput_ErrorMessageReturned() {
        String input = "-1";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("Plant count must be a positive number", returnedInput);
    }
    @Test
    public void PlantCountEntered_AlphaInput_ErrorMessageReturned() {
        String input = "One";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("Plant count must be a positive number", returnedInput);
    }
    @Test //should fail
    public void PlantCountEntered_MultipleDotInput_ErrorMessageReturned() {
        String input = "1.1.";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("Plant count must be a positive number", returnedInput);
    }
    @Test //should fail
    public void PlantCountEntered_LargeInput_ErrorMessageReturned() {
        String input = "10000001";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("Plant count must be less than 10 Million", returnedInput);
    }
    @Test // should fail
    public void PlantCountEntered_MultipleCommaInput_ErrorMessageReturned() {
        String input = "1,1,";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("Plant count must be a positive number", returnedInput);
    }

    @Test
    public void PlantDescriptionEntered_LessThan512Characters_PlantDescriptionReturned() {
        String description = "The rose is a symbol of love";
        String returnedInput = ValidityChecker.validatePlantDescription(description);
        Assertions.assertEquals(description, returnedInput);
    }

    @Test
    public void PlantDescriptionEntered_MoreThan512Characters_ErrorMessageReturned() {
        String description = "The majestic Oak tree, known scientifically as Quercus, is a symbol of strength, endurance, " +
                "and wisdom. This towering deciduous tree is a hallmark of many landscapes, with its spreading branches " +
                "and dense foliage providing shade and shelter to countless creatures. With a lifespan spanning centuries, " +
                "the Oak tree has witnessed the ebb and flow of history, its sturdy trunk bearing the scars of time. " +
                "From ancient mythologies to modern literature, the Oak tree has captivated the human imagination, " +
                "inspiring awe and reverence. Its acorns, a source of sustenance for wildlife, are also a symbol of " +
                "potential and renewal. In folklore and legend, the Oak tree is often associated with gods and spirits," +
                " embodying resilience and resilience. From the whispering leaves to the gnarled bark," +
                " every aspect of the Oak tree tells a story of resilience, adaptability, and the enduring power of nature.";
        String returnedInput = ValidityChecker.validatePlantDescription(description);
        Assertions.assertEquals("Plant description must be less than 512 characters", returnedInput);
    }

    @Test
    public void PlantDescriptionEntered_EmptyString_EmptyStringReturned() {
        String description = "";
        String returnedInput = ValidityChecker.validatePlantDescription(description);
        Assertions.assertEquals("", returnedInput);
    }

    @Test
    public void PlantDescriptionEntered_SpaceString_SpaceStringReturned() {
        String description = " ";
        String returnedInput = ValidityChecker.validatePlantDescription(description);
        Assertions.assertEquals(" ", returnedInput);
    }


    @ParameterizedTest
    @CsvSource(value = {
            "Drury: Drury",
            "12345: Please enter a suburb without only numerical characters <br/>",
            "@@#$%^&&**(*: Suburb must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes " +
                    "<br/>Suburb must contain at least one alphanumeric character <br/>",
            "Achieving balance between work and life is essential for long-term happiness and overall well-being.: " +
                    "Please enter a suburb less than 90 characters",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@: " +
                    "Suburb must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes " +
                    "<br/>Suburb must contain at least one alphanumeric character <br/>" +
                    "Please enter a suburb less than 90 characters"
    }, delimiter = ':')
    public void ValidateGardenSuburbTest(String suburb, String expectedMessage) {
        String actualMessage = ValidityChecker.validateGardenSuburb(suburb);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Drury: Drury",
            "     : City is required",
            "12345: Please enter a city without only numerical characters <br/>",
            "@@#$%^&&**(*: City must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes " +
                    "<br/>City must contain at least one alphanumeric character <br/>",
            "Achieving balance between work and life is essential for long-term happiness and overall well-being." +
                    "Achieving balance between work and life is essential for long-term happiness and overall well-being.: " +
                    "Please enter a city less than 180 characters",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" +
                    "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@: " +
                    "City must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes " +
                    "<br/>City must contain at least one alphanumeric character <br/>" +
                    "Please enter a city less than 180 characters"
    }, delimiter = ':')
    public void ValidateGardenCityTest(String city, String expectedMessage) {
        String actualMessage = ValidityChecker.validateGardenCity(city);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Drury: Drury",
            "     : Country is required",
            "12345: Please enter a country without only numerical characters <br/>",
            "@@#$%^&&**(*: Country must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes" +
                    " <br/>Country must contain at least one alphanumeric character <br/>",
            "Achieving balance between work and life is essential to an awesome life: Please enter a country less than 60 characters",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@: " +
                    "Country must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes " +
                    "<br/>Country must contain at least one alphanumeric character <br/>" +
                    "Please enter a country less than 60 characters"
    }, delimiter = ':')
    public void ValidateGardenCountryTest(String country, String expectedMessage) {
        String actualMessage = ValidityChecker.validateGardenCountry(country);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Drury: Drury",
            "5/10 Ilam Road: 5/10 Ilam Road",
            "12345: Please enter a street number and name without only numerical characters <br/>",
            "@@#$%^&&**(*: Street number and name must only include letters, numbers, spaces, commas, dots, hyphens, slashes or apostrophes" +
                    " <br/>Street number and name must contain at least one alphanumeric character <br/>",
            "Achieving balance between work and life is essential to an awesome life: " +
                    "Please enter a street number and name less than 60 characters",
            "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@: " +
                    "Street number and name must only include letters, numbers, spaces, commas, dots, hyphens, slashes or apostrophes " +
                    "<br/>Street number and name must contain at least one alphanumeric character <br/>" +
                    "Please enter a street number and name less than 60 characters"
    }, delimiter = ':')
    public void ValidateGardenAddressTest(String address, String expectedMessage) {
        String actualMessage = ValidityChecker.validateGardenAddress(address);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "12323242A: 12323242A",
            "@@#$%^&&**(*: Postcode must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes " +
                    "<br/>Postcode must contain at least one alphanumeric character <br/>Please enter a postcode less than 10 characters",
            "Achieving balance: Please enter a postcode less than 10 characters",
            ",,,....: Postcode must contain at least one alphanumeric character <br/>",
            "@A: Postcode must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>"
    }, delimiter = ':')
    public void ValidateGardenPostcodeTest(String postcode, String expectedMessage) {
        String actualMessage = ValidityChecker.validateGardenPostcode(postcode);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "The majestic Oak tree, known scientifically as Quercus, is a symbol of fucken strength, endurance, " +
                    "and wisdom. This towering deciduous tree is a hallmark of many landscapes, with its spreading branches " +
                    "and dense foliage providing shade and shelter to countless creatures. With a lifespan spanning centuries, " +
                    "the Oak tree has witnessed the ebb and flow of history, its sturdy trunk bearing the scars of time. " +
                    "From ancient mythologies to modern literature, the Oak tree has captivated the human imagination, " +
                    "inspiring awe and reverence. Its acorns, a source of sustenance for wildlife, are also a symbol of " +
                    "potential and renewal. In folklore and legend, the Oak tree is often associated with gods and spirits," +
                    " embodying resilience and resilience. From the whispering leaves to the gnarled bark," +
                    " every aspect of the Oak tree tells a story of resilience, adaptability, and the enduring power of nature." +
                    ": Garden description must be less than 512 characters <br/>The description does not match the language standards of the app.",
            "123: Description must be 512 characters or less and contain some text <br/>",
            "shit: The description does not match the language standards of the app.",
            "hi : hi"
    }, delimiter = ':')
    public void ValidateGardenDescriptionTest(String description, String expectedMessage) {
        String actualMessage = ValidityChecker.validateGardenDescription(description);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-1: 0",
            "10000000000000: 0",
            "dsfgdhdrevs: 0",
            "5: 5",
            "-1000000000000: 0"
    }, delimiter = ':')
    public void ValidatePageNumberTest(String pageNumber, int expectedNumber) {
        int actualNumber = ValidityChecker.validatePageNumber(pageNumber);
        Assertions.assertEquals(expectedNumber, actualNumber);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "@@@@@@@: Scientific name must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>",
            "Science: Science",
            "     :     ",
            "@@@$$%^S^&&S&FBBNSJLKJNSLFNNSSKNKNY#^^#T*HFBSFBSKNOIH#&@Y(@*Y*(@HOFSIHF(*Y#)#H)HFONW*(*3*@B***@HG(H@: " +
                    "Scientific name must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>" +
                    "Scientific name must be less than 64 characters",
            "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh: " +
                    "Scientific name must be less than 64 characters"
    }, delimiter = ':')
    void ValidateScientificNameTest(String scientificName, String expectedMessage) {
        String actualMessage = ValidityChecker.validateScientificPlantName(scientificName);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }


    @ParameterizedTest
    @CsvSource(value = {
            "0 : 0 : true",
            "-90 : -180 : true",
            "90 : 180 : true",
            "89 : 179 : true",
            "-91 : -181 : false",
            "-91 : 0 : false",
            "0, : -181 : false",
            "0 : 181 : false",
            "91 : 0 : false",
            "-90 : 0 : true",
            "0 : -180 : true",
            "0 : 180 : true",
            "90 : 0 : true",
            "'' : '' : true",
            "'' : 0 : false",
            "0 : '' : false"

    }, delimiter = ':')
    void ValidateCoordinates(String plantLatitude, String plantLongitude, boolean expectedResult) {

        boolean result = ValidityChecker.validatePlantCoordinates(plantLatitude, plantLongitude);

        Assertions.assertEquals(result, expectedResult);
    }




}