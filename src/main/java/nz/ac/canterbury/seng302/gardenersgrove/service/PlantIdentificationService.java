package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantResponse;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for handling plant identification through an external API.
 * It processes plant images, interacts with the API, and saves the identification results in the database.
 */
@Service
public class PlantIdentificationService {
    Logger logger = LoggerFactory.getLogger(PlantIdentificationService.class);
    private static final String PROJECT = "all";
    private static final String API_URL = "https://my-api.plantnet.org/v2/identify/";
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/uploads/";

    private final String apiKey;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final IdentifiedPlantRepository identifiedPlantRepository;

    /**
     * Constructs a new PlantIdentificationService with the specified API key and repository.
     *
     * @param apiKey  the API key for authenticating with the external plant identification service
     */
    @Autowired
    public PlantIdentificationService(@Value("${plantNet.password}") String apiKey, IdentifiedPlantRepository identifiedPlantRepository) {
        this.apiKey = apiKey;
        this.identifiedPlantRepository = identifiedPlantRepository;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }

    private static final boolean INCLUDE_RELATED_IMAGES = true;
    private static final boolean NO_REJECT = false;
    private static final int NB_RESULTS = 1;
    private static final String LANGUAGE = "en";
    private static final String MODEL_TYPE = "kt";

    /**
     * Identifies a plant from the provided image using an external API and saves the result in the database.
     * The result is first stored in an intermediate object containing the JSON data.
     * Then, a new entity is created to map the response data into.
     *
     * @param image    the image file of the plant to be identified
     * @param gardener the gardener who uploaded the image
     * @return the identified plant entity containing the identification details
     * @throws IOException if an error occurs during the identification process or saving the image
     */
    public IdentifiedPlant identifyPlant(MultipartFile image, Gardener gardener) throws IOException {
        String url = API_URL + PROJECT + buildQueryParameters();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("images", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String imagePath = saveImageFile(image);
            IdentifiedPlantResponse identifiedPlantResponse = objectMapper.readValue(response.getBody(), IdentifiedPlantResponse.class);

            return getIdentifiedPlantDetails(identifiedPlantResponse, gardener, imagePath);
        } else {
            throw new IOException("Failed to identify plant. API returned status code: " + response.getStatusCode());
        }
    }

    /**
     * Saves the uploaded image file to the filesystem with a unique filename.
     *
     * @param image the image file to be saved
     * @return the path to the saved image
     * @throws IOException if an error occurs while saving the image
     */
    private String saveImageFile(MultipartFile image) throws IOException {
        Path directory = Paths.get(IMAGE_DIRECTORY);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String originalFileName = image.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String newFileName = UUID.randomUUID() + "." + extension;

        String filePath = IMAGE_DIRECTORY + newFileName;
        Path file = Paths.get(filePath);

        // The following code checks to make sure the user has not messed with the file path
        File checkFile = new File(filePath);
        String canonicalDestinationPath = checkFile.getCanonicalPath();
        if (!canonicalDestinationPath.startsWith(IMAGE_DIRECTORY)) {
            throw new IOException("Entry is outside of the target directory");
        }

        // If everything is all good, then create the file
        Files.write(file, image.getBytes());
        return "/uploads/" + newFileName;
    }

    /**
     * Builds the query parameters for the API request.
     *
     * @return a string containing the query parameters
     */
    private String buildQueryParameters() {
        return "?api-key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                "&include-related-images=" + INCLUDE_RELATED_IMAGES +
                "&no-reject=" + NO_REJECT +
                "&nb-results=" + NB_RESULTS +
                "&lang=" + URLEncoder.encode(LANGUAGE, StandardCharsets.UTF_8) +
                "&type=" + URLEncoder.encode(MODEL_TYPE, StandardCharsets.UTF_8);
    }

    /**
     * Get the plant details out of the response from the plant identification API.
     *
     * @param identifiedPlantResponse the response from the API containing identification details, stored in a Java object
     * @param gardener                the gardener who uploaded the image
     * @param imagePath               the path to the uploaded image
     * @return the identified plant entity
     */
    public IdentifiedPlant getIdentifiedPlantDetails(IdentifiedPlantResponse identifiedPlantResponse, Gardener gardener, String imagePath) {
        String speciesAttribute = "species";
        JsonNode firstResult = identifiedPlantResponse.getResults().get(0);
        String bestMatch = identifiedPlantResponse.getBestMatch();
        Double score = firstResult.get("score").asDouble();
        List<String> commonNames = objectMapper.convertValue(
                firstResult.get(speciesAttribute).get("commonNames"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        String gbifId = firstResult.get("gbif").get("id").asText();
        String imageUrl = firstResult.get("images").get(0).get("url").get("o").asText();
        String speciesScientificNameWithoutAuthor = firstResult.get(speciesAttribute).get("scientificNameWithoutAuthor").asText();
        String familyScientificNameWithoutAuthor = firstResult.get(speciesAttribute).get("family").get("scientificNameWithoutAuthor").asText();

        return new IdentifiedPlant(bestMatch, score, commonNames, gbifId, imageUrl, imagePath, speciesScientificNameWithoutAuthor, familyScientificNameWithoutAuthor, gardener);
    }

    /**
     * Gets all the plant names for Identified plant in the database
     * @return all the plant names in the database
     */
    public List<String> getAllPlantNames(Gardener gardener) {
        return identifiedPlantRepository.getAllPlantNames(gardener.getId());
    }

    /**
     * Gets all the scientific names for Identified plant in the database
     * @return all the scientific names for Identified plant in the database
     */
    public List<String> getAllSpeciesScientificNames(Gardener gardener) {
        return identifiedPlantRepository.getAllSpeciesScientificName(gardener.getId());
    }

    /**
     * Gets the plant details according to plant name
     * @param name the plant name to search
     * @return the plant details in the database
     */
    public List<Map<String, String>> getPlantDetailsWithPlantNames(String name) {
        return identifiedPlantRepository.getPlantDetailsWithPlantNames(name)
                .stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    /**
     * Gets the plant details according to Species Scientific Plant name
     * @param name the specie scientific name to search
     * @return the plant details in the database
     */
    public List<Map<String, String>> getPlantDetailsWithSpeciesScientificName(String name) {
        return identifiedPlantRepository.getPlantDetailsWithSpeciesScientificName(name)
                .stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    /**
     * Converts object IdentifiedPlant to the Map with string
     * @param plant the plant to be converted to Map data style
     * @return Map contains plant details
     */
    private Map<String, String> convertToMap(IdentifiedPlant plant) {
        Map<String, String> map = new HashMap<>();
        map.put("name", plant.getName());
        map.put("scientificName", plant.getSpeciesScientificNameWithoutAuthor());
        map.put("description", plant.getDescription());
        map.put("dateUploaded", plant.getDateUploaded());
        return map;
    }




}