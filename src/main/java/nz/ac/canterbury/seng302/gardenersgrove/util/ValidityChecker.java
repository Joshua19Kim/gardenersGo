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
        String result = "";
        if (name == null || name.trim().isEmpty()) {
            return "Garden name cannot be empty";
        }

        // https://stackoverflow.com/questions/24744375/regex-for-only-allowing-letters-numbers-space-commas-periods
        // https://stackoverflow.com/questions/20690499/concrete-javascript-regular-expression-for-accented-characters-diacritics
        String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!name.matches(regex)) {
            result += "Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes <br/>";
        }
        if(name.length() > 64) {
            result += "Garden name must be less than 64 characters";
        }
        if(result.isEmpty()) {
            return name;
        }
        return result;

    }

    /**
     * Checks that the garden description complies with the required format
     * @param description the garden description
     * @return The description of the garden if it is valid, else returns an error message
     */
    public static String validateGardenDescription(String description) {
        if (description == null || description.isEmpty()) {
            return description;
        }
        String regex = "^(?=.*[\\p{L}]).+";
        String result = "";
        if (!description.matches(regex)) {
            result = "Description must be 512 characters or less and contain some text <br/>";
        }
        if (description.length() > 512) {
            result += "Garden description must be less than 512 characters <br/>";
        }
        String[] descriptionWords = description.split("\\s+");

        for (String word : descriptionWords) {
            if (WordFilter.doesContainBadWords(word)) {
                result += "The description does not match the language standards of the app.";
                break;
            }
        }
        if(result.isEmpty()) {
            return  description;
        }
        return result;
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
        String result = "";
        if (name == null || name.trim().isEmpty() || !name.matches(regex)) {
            result += "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes <br/>";
        }

        if(name != null && name.length() > 64) {
            result += "Plant name must be less than or equal to 64 characters";
        }

        if (!result.isEmpty()) {
            return result;
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
        String result = "";
        if(suburb != null && suburb.matches(regex)) {
            result = "Please enter a suburb without only numerical characters <br/>";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (suburb != null && !suburb.trim().isEmpty() && !suburb.matches(regex)) {
            result += "Suburb must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (suburb != null && !suburb.trim().isEmpty() && !suburb.matches(regex)) {
            result += "Suburb must contain at least one alphanumeric character <br/>";
        }
        if(suburb != null && suburb.length() > 90) {
            result += "Please enter a suburb less than 90 characters";
        }
        if(result.isEmpty()) {
            return suburb;
        }
        return result;

    }

    public static String validateGardenCity(String city) {
        String regex = "^[0-9]+$";
        String result = "";
        if(city == null || city.trim().isEmpty()) {
            return "City is required";
        }
        if(city.matches(regex)) {
            result += "Please enter a city without only numerical characters <br/>";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!city.matches(regex)) {
            result += "City must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (!city.matches(regex)) {
            result += "City must contain at least one alphanumeric character <br/>";
        }
        if(city.length() > 180) {
            result += "Please enter a city less than 180 characters";
        }
        if(result.isEmpty()) {
            return city;
        }
        return result;
    }
    public static String validateGardenCountry(String country) {
        String regex = "^[0-9]+$";
        String result = "";
        if(country == null || country.trim().isEmpty()) {
            return "Country is required";
        }
        if(country.matches(regex)) {
            result += "Please enter a country without only numerical characters <br/>";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        if (!country.matches(regex)) {
            result += "Country must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (!country.matches(regex)) {
            result += "Country must contain at least one alphanumeric character <br/>";
        }
        if(country.length() > 60) {
            result += "Please enter a country less than 60 characters";
        }
        if(result.isEmpty()) {
            return country;
        }
        return result;
    }
    public static String validateGardenAddress(String address) {
        String regex = "^[0-9]+$";
        String result = "";
        if(address != null && address.matches(regex)) {
            result = "Please enter a street number and name without only numerical characters <br/>";
        }
        regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,./'-]+$";
        if (address != null && !address.trim().isEmpty() && !address.matches(regex)) {
            result += "Street number and name must only include letters, numbers, spaces, commas, dots, hyphens, slashes or apostrophes <br/>";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (address != null && !address.trim().isEmpty() && !address.matches(regex)) {
            result += "Street number and name must contain at least one alphanumeric character <br/>";
        }
        if(address != null && address.length() > 60) {
            result += "Please enter a street number and name less than 60 characters";
        }
        if(result.isEmpty()) {
            return address;
        }
        return result;

    }
    public static String validateGardenPostcode(String postcode) {
        String regex = "^[A-Za-zÀ-ÖØ-öø-ž0-9 ,.'-]+$";
        String result ="";
        if (postcode != null && !postcode.trim().isEmpty() && !postcode.matches(regex)) {
            result = "Postcode must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>";
        }
        regex = ".*[A-Za-zÀ-ÖØ-öø-ž0-9].*";
        if (postcode != null && !postcode.trim().isEmpty() && !postcode.matches(regex)) {
            result += "Postcode must contain at least one alphanumeric character <br/>";
        }
        if(postcode != null && postcode.length() > 10) {
            result += "Please enter a postcode less than 10 characters";
        }
        if(result.isEmpty()) {
            return postcode;
        }
        return result;

    }

    /**
     * Used to validate that the page number is not negative, an integer and does not go over the max limit
     * @param pageNumber the page number
     * @return the page number or default page number of 0
     */
    public static int validatePageNumber(String pageNumber) {
        int pageNo;
        // the max number of digits allowed for page numbers
        if(pageNumber.length()> 10)  {
            pageNo = 0;
        } else {
            try {
                pageNo = Integer.parseInt(pageNumber, 10);
                if(pageNo < 0) {
                    pageNo = 0;
                }
            } catch (NumberFormatException e) {
                pageNo = 0;
            }

        }
        return pageNo;
    }


}