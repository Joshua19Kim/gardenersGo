package nz.ac.canterbury.seng302.gardenersgrove.util;

/**
 *  A class used to validate the inputs of forms. Contains static methods that will either return the input if
 *  it is valid or an error message if it is invalid.
 */
public class ValidityChecker {

    /**
     * Checks that the garden name complies with the required format
     * @param name the garden name
     * @return The name of the garden if it is valid, else returns an error message
     */
    public static String validateGardenName(String name) {
        // https://stackoverflow.com/questions/14721397/checking-if-a-string-is-empty-or-null-in-java
        if (name == null || name.trim().isEmpty()) {
            return "Garden name cannot be empty";
        }

        // https://stackoverflow.com/questions/24744375/regex-for-only-allowing-letters-numbers-space-commas-periods
        // https://stackoverflow.com/questions/20690499/concrete-javascript-regular-expression-for-accented-characters-diacritics
        String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!name.matches(regex)) {
            return "Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes";
        }
        if(name.length() > 64) {
            return "Garden name must be less than 64 characters";
        }

        return name;
    }

    /**
     * Checks that the garden location complies with the required format
     * @param location the garden location
     * @return The location of the garden if it is valid, else returns an error message
     */
    public static String validateGardenLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return "Location cannot be empty";
        }

        String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!location.matches(regex)) {
            return "Location name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes";
        }
        if(location.length() > 128) {
            return "Location must be less than 128 characters";
        }

        return location;
    }

    /**
     * Checks that the garden size is a positive number
     * @param size the garden size
     * @return The size of the garden if it is valid, else returns an error message
     */
    public static String validateGardenSize(String size) {
        if (size == null || size.trim().isEmpty()) {
            return size;
        }

        String sizeReplaced = size.replace(',', '.');

        // https://stackoverflow.com/questions/39182829/how-to-check-if-a-string-is-parsable-to-float
        try {
            float newSize = Float.parseFloat(sizeReplaced);
            if (newSize > 1e7) {
                return "Garden size must be less than 10 Million";
            }
        } catch (NumberFormatException e) {
            return "Garden size must be a valid number with only a decimal place allowed";
        }

        if (sizeReplaced.contains("-")) {
            return "Garden size must be a positive number";
        }
        return sizeReplaced;
    }

    /**
     * Validates that the plant name is of valid format
     * @param name the plant name
     * @return Either an error message or the plant name
     */
    public static String validatePlantName(String name) {

        String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (name == null || name.trim().isEmpty() || !name.matches(regex)) {
            return "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes";
        }
        if(name.length() > 64) {
            return "Plant name must be less than 64 characters";
        }
        return name;
    }

    /**
     * Validates the plant count is of valid format
     * @param count the plant count
     * @return Either an error message or the correctly formatted count
     */
    public static String validatePlantCount(String count) {
        if (count == null || count.trim().isEmpty()) {
            return count;
        }

        String countReplaced = count.replace(',', '.');

        try {
            float plantSize = Float.parseFloat(countReplaced);
            if (plantSize > 1e7) {
                return "Plant count must be less than 10 Million";
            }
        } catch (NumberFormatException e) {
            return "Plant count must be a positive number";
        }
        if (countReplaced.contains("-")) {
            return "Plant count must be a positive number";
        }
        return countReplaced;
    }

    /**
     * Checks that the description is less than 512 characters
     * @param description plant description
     * @return Either an error message or the description
     */
    public static String validatePlantDescription(String description) {
        if(description == null || description.trim().isEmpty()) {
            return description;
        }
        if(description.length() > 512) {
            return "Plant description must be less than 512 characters";
        }
        return description;
    }



}