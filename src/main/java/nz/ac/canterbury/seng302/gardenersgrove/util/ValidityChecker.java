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
     * Checks that the garden description complies with the required format
     * @param description the garden description
     * @return The description of the garden if it is valid, else returns an error message
     */
    public static String validateGardenDescription(String description) {
        if (description.length() == 0) {
            return description;
        }
        String regex = "^(?=.*[\\p{L}]).+";
        if (!description.matches(regex)) {
            return "Description must be 512 characters or less and contain some text";
        }
        if (description.length() > 512) {
            return "Garden description must be less than 512 characters";
        }
        String[] descriptionWords = description.split("\\s+");

        for (String word : descriptionWords) {
            if (WordFilter.doesContainBadWords(word)) {
                return "The description does not match the language standards of the app.";
            }
        }

        return description;
    }

    /**
     * Checks that the garden location complies with the required format
     * @param location the garden location
     * @return The location of the garden if it is valid, else returns an error message
     */
    public static String validateGardenLocation(String location) {
    location = location.trim();
    if (location == null || location.isEmpty()) {
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
    size = size.trim();
    if (size == null || size.isEmpty()) {
            return size;
        }

        String sizeReplaced = size.replace(',', '.');

        if (sizeReplaced.contains("-")) {
            return "Garden size must be a positive number";
        }
        // https://stackoverflow.com/questions/39182829/how-to-check-if-a-string-is-parsable-to-float
        try {
            float newSize = Float.parseFloat(sizeReplaced);
            if (newSize > 1e7) {
                return "Garden size must be less than 10 Million";
            }
        } catch (NumberFormatException e) {
            return "Garden size must be a positive number";
        }

        if(size.length() > 512) {
            return "Garden size must be less than 10 Million";
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

        if (countReplaced.contains("-")) {
            return "Plant count must be a positive number";
        }

        try {
            float plantSize = Float.parseFloat(countReplaced);
            if (plantSize > 1e7) {
                return "Plant count must be less than 10 Million";
            }
        } catch (NumberFormatException e) {
            return "Plant count must be a positive number";
        }
        if(count.length() > 12) {
            return "Plant count must be less than 10 Million";
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

    public static String validateGardenSuburb(String suburb) {
        String regex = "^[0-9]+$";
        if(suburb.matches(regex)) {
            return "Please enter a suburb without only numerical characters";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (suburb != null && !suburb.trim().isEmpty() && !suburb.matches(regex)) {
            return "Suburb must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (suburb != null && !suburb.trim().isEmpty() && !suburb.matches(regex)) {
            return "Suburb must must contain at least one alphanumeric character";
        }
        if(suburb.length() > 90) {
            return "Please enter a suburb less than 90 characters";
        }
        return suburb;
    }

    public static String validateGardenCity(String city) {
        String regex = "^[0-9]+$";
        if(city == null || city.trim().isEmpty()) {
            return "City is required";
        }
        if(city.matches(regex)) {
            return "Please enter a city without only numerical characters";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!city.matches(regex)) {
            return "City must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (!city.matches(regex)) {
            return "City must must contain at least one alphanumeric character";
        }
        if(city.length() > 180) {
            return "Please enter a city less than 180 characters";
        }
        return city;
    }
    public static String validateGardenCountry(String country) {
        String regex = "^[0-9]+$";
        if(country == null || country.trim().isEmpty()) {
            return "Country is required";
        }
        if(country.matches(regex)) {
            return "Please enter a country without only numerical characters";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!country.matches(regex)) {
            return "Country must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (!country.matches(regex)) {
            return "Country must must contain at least one alphanumeric character";
        }
        if(country.length() > 60) {
            return "Please enter a country less than 60 characters";
        }
        return country;
    }
    public static String validateGardenAddress(String address) {
    String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (address != null && !address.trim().isEmpty() && !address.matches(regex)) {
            return "Street number and name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes";
        }
    regex = ".*[A-Za-zÀ-Ö].*";
        if (address != null && !address.trim().isEmpty() && !address.matches(regex)) {
      return "Street number and name must contain at least one letter";
        }
        if(address.length() > 60) {
            return "Please enter a street number and name less than 60 characters";
        }
        return address;
    }
    public static String validateGardenPostcode(String postcode) {
        String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (postcode != null && !postcode.trim().isEmpty() && !postcode.matches(regex)) {
            return "Postcode must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (postcode != null && !postcode.trim().isEmpty() && !postcode.matches(regex)) {
            return "Postcode must must contain at least one alphanumeric character";
        }
        if(postcode.length() > 10) {
            return "Please enter a postcode less than 10 characters";
        }
        return postcode;
    }


}