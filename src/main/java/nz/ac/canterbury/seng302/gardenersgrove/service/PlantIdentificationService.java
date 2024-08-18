package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantResponse;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

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
     * @param apiKey                    the API key for authenticating with the external plant identification service
     * @param identifiedPlantRepository the repository for saving identified plants
     */
    public PlantIdentificationService(@Value("${plantnet.password}") String apiKey, IdentifiedPlantRepository identifiedPlantRepository) {
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
     * Then, a new entity is created to map the response data into and save in the database.
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
            return savePlantIdentificationResult(identifiedPlantResponse, gardener, imagePath);
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
     * Maps the response from the plant identification API into a JPA object and saves it in the database.
     *
     * @param identifiedPlantResponse the response from the API containing identification details, stored in a Java object
     * @param gardener                the gardener who uploaded the image
     * @param imagePath               the path to the uploaded image
     * @return the saved identified plant entity
     */
    public IdentifiedPlant savePlantIdentificationResult(IdentifiedPlantResponse identifiedPlantResponse, Gardener gardener, String imagePath) {
        JsonNode firstResult = identifiedPlantResponse.getResults().get(0);
        String bestMatch = identifiedPlantResponse.getBestMatch();
        List<String> commonNames = objectMapper.convertValue(
                firstResult.get("species").get("commonNames"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(bestMatch, firstResult, commonNames, gardener, imagePath);
        return identifiedPlantRepository.save(identifiedPlant);
    }
}