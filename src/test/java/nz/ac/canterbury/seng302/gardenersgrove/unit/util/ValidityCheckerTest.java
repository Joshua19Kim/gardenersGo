package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals("Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes", returnedInput);
    }


    @Test
    public void LocationEntered_AlphaNumericInput_NameReturned() {
        String input = "JackErsk1ne";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("JackErsk1ne", returnedInput);
    }
    @Test
    public void LocationEntered_SpaceInput_NameReturned() {
        String input = "a a";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("a a", returnedInput);
    }
    @Test
    public void LocationEntered_CommaInput_NameReturned() {
        String input = ",";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals(",", returnedInput);
    }
    @Test
    public void LocationEntered_DotInput_NameReturned() {
        String input = ".";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals(".", returnedInput);
    }
    @Test
    public void LocationEntered_HyphenInput_NameReturned() {
        String input = "-";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("-", returnedInput);
    }
    @Test
    public void LocationEntered_ApostropheInput_NameReturned() {
        String input = "'";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("'", returnedInput);
    }
    @Test
    public void LocationEntered_EmptyInput_ErrorMessageReturned() {
        String input = "";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("Location cannot be empty", returnedInput);
    }
    @Test
    public void LocationEntered_EmptySpaceInput_ErrorMessageReturned() {
        String input = " ";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("Location cannot be empty", returnedInput);
    }
    @Test
    public void LocationEntered_InvalidInput_ErrorMessageReturned() {
        String input = "J@(K_Er$K!^e";
        String returnedInput = ValidityChecker.validateGardenLocation(input);
        Assertions.assertEquals("Location name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes", returnedInput);
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
        Assertions.assertEquals("Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes", returnedInput);
    }
    @Test
    public void PlantNameEntered_EmptySpaceInput_ErrorMessageReturned() {
        String input = " ";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes", returnedInput);
    }
    @Test
    public void PlantNameEntered_InvalidInput_ErrorMessageReturned() {
        String input = "J@(K_Er$K!^e";
        String returnedInput = ValidityChecker.validatePlantName(input);
        Assertions.assertEquals("Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes", returnedInput);
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
    @Test // should fail
    public void PlantCountEntered_MultipleCommaInput_ErrorMessageReturned() {
        String input = "1,1,";
        String returnedInput = ValidityChecker.validatePlantCount(input);
        Assertions.assertEquals("Plant count must be a positive number", returnedInput);
    }

    @Test
    public void DatePlantedEntered_ValidFormat_DatePlantedReturned() {
        String date = "21/07/2003";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals(date, returnedInput);
    }

    @Test
    public void DatePlantedEntered_TooManyNumbers_ErrorMessageReturned() {
        String date = "211/0711/20035";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals("Date is not in valid format, DD/MM/YYYY", returnedInput);
    }

    @Test
    public void DatePlantedEntered_TooLittleNumbers_ErrorMessageReturned() {
        String date = "2/07/203";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals("Date is not in valid format, DD/MM/YYYY", returnedInput);
    }

    @Test
    public void DatePlantedEntered_InvalidDashesFormat_ErrorMessageReturned() {
        String date = "21-07-2003";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals("Date is not in valid format, DD/MM/YYYY", returnedInput);
    }

    @Test
    public void DatePlantedEntered_BigEndianFormat_ErrorMessageReturned() {
        String date = "2003/21/07";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals("Date is not in valid format, DD/MM/YYYY", returnedInput);
    }

    @Test
    public void DatePlantedEntered_EmptyString_EmptyStringReturned() {
        String date = "";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals("", returnedInput);
    }

    @Test
    public void DatePlantedEntered_SpaceString_SpaceStringReturned() {
        String date = " ";
        String returnedInput = ValidityChecker.validatePlantDate(date);
        Assertions.assertEquals(" ", returnedInput);
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

}