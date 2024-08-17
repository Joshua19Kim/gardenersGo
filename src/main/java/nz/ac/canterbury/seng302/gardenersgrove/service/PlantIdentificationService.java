package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    @Autowired
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

    public IdentifiedPlantResponse identifyPlant(MultipartFile image) throws IOException {
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
            saveImageFile(image);
            return objectMapper.readValue(response.getBody(), IdentifiedPlantResponse.class);
        } else {
            throw new IOException("Failed to identify plant. API returned status code: " + response.getStatusCode());
        }
    }

    private void saveImageFile(MultipartFile image) throws IOException {
        Path directory = Paths.get(IMAGE_DIRECTORY);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String filePath = IMAGE_DIRECTORY + image.getOriginalFilename();
        Path file = Paths.get(filePath);
        Files.write(file, image.getBytes());
    }

    private String buildQueryParameters() {
        return "?api-key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8) +
                "&include-related-images=" + INCLUDE_RELATED_IMAGES +
                "&no-reject=" + NO_REJECT +
                "&nb-results=" + NB_RESULTS +
                "&lang=" + URLEncoder.encode(LANGUAGE, StandardCharsets.UTF_8) +
                "&type=" + URLEncoder.encode(MODEL_TYPE, StandardCharsets.UTF_8);
    }

    public void savePlantIdentificationResult(IdentifiedPlantResponse identifiedPlantResponse) {
        JsonNode firstResult = identifiedPlantResponse.getResults().get(0);

        IdentifiedPlant identifiedPlant = new IdentifiedPlant();
        identifiedPlant.setBestMatch(identifiedPlantResponse.getBestMatch());
        identifiedPlant.setScore(firstResult.get("score").asDouble());

        JsonNode speciesNode = firstResult.get("species");
        identifiedPlant.setSpeciesScientificNameWithoutAuthor(speciesNode.get("scientificNameWithoutAuthor").asText());
        identifiedPlant.setSpeciesScientificNameAuthorship(speciesNode.get("scientificNameAuthorship").asText());
        identifiedPlant.setSpeciesScientificName(speciesNode.get("scientificName").asText());

        JsonNode genusNode = speciesNode.get("genus");
        identifiedPlant.setGenusScientificNameWithoutAuthor(genusNode.get("scientificNameWithoutAuthor").asText());
        identifiedPlant.setGenusScientificNameAuthorship(genusNode.get("scientificNameAuthorship").asText());
        identifiedPlant.setGenusScientificName(genusNode.get("scientificName").asText());

        JsonNode familyNode = speciesNode.get("family");
        identifiedPlant.setFamilyScientificNameWithoutAuthor(familyNode.get("scientificNameWithoutAuthor").asText());
        identifiedPlant.setFamilyScientificNameAuthorship(familyNode.get("scientificNameAuthorship").asText());
        identifiedPlant.setFamilyScientificName(familyNode.get("scientificName").asText());

        List<String> commonNames = objectMapper.convertValue(
                speciesNode.get("commonNames"),
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
        identifiedPlant.setCommonNames(commonNames);

        JsonNode firstImage = firstResult.get("images").get(0);
        identifiedPlant.setImageUrl(firstImage.get("url").get("o").asText());

        identifiedPlantRepository.save(identifiedPlant);
    }
}