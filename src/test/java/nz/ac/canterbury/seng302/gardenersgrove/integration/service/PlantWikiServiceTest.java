package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlantResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class PlantWikiServiceTest {


    private PlantWikiService plantWikiService;
    
    private ObjectMapper mockObjectMapper;

    private ObjectMapper objectMapper;

    @Value("${plantWiki.key}")
    private String apiKey;

    private String PERENUAL_API_URL = "https://perenual.com/api/species-list";


    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockObjectMapper = Mockito.mock(ObjectMapper.class);
        plantWikiService = new PlantWikiService(apiKey, mockObjectMapper);

        // used ChatGPT for this mock
        Mockito.when(mockObjectMapper.convertValue(any(JsonNode.class), any(TypeReference.class))) .thenAnswer(invocation -> {
            JsonNode node = invocation.getArgument(0);
            return objectMapper.convertValue(node, new TypeReference<List<String>>() {
            });
        });

    }

    @Test
    public void getPlantsRequested_NoQuery_WikiPlantsReturned() throws URISyntaxException, IOException {

        // Note: this is an actual json response from the api
        String jsonString = "{\"data\":[{\"id\":1,\"common_name\":\"European Silver Fir\",\"scientific_name\":" +
                "[\"Abies alba\"],\"other_name\":[\"Common Silver Fir\"],\"cycle\":\"Perennial\",\"watering\":\"Frequent\"" +
                ",\"sunlight\":[\"full sun\"],\"default_image\":{\"license\":45,\"license_name\":\"Attribution-ShareAlike " +
                "3.0 Unported (CC BY-SA 3.0)\",\"license_url\":\"https:\\/\\/creativecommons.org\\/licenses\\/by-sa\\/3.0" +
                "\\/deed.en\",\"original_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/1_abies_alba\\/og" +
                "\\/1536px-Abies_alba_SkalitC3A9.jpg\",\"regular_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image" +
                "\\/1_abies_alba\\/regular\\/1536px-Abies_alba_SkalitC3A9.jpg\",\"medium_url\":\"https:\\/\\/perenual.com\\" +
                "/storage\\/species_image\\/1_abies_alba\\/medium\\/1536px-Abies_alba_SkalitC3A9.jpg\",\"small_url\":\"" +
                "https:\\/\\/perenual.com\\/storage\\/species_image\\/1_abies_alba\\/small\\/1536px-Abies_alba_SkalitC3A9.jpg\"," +
                "\"thumbnail\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/1_abies_alba\\/thumbnail\\/1536px-Abies_alba" +
                "_SkalitC3A9.jpg\"}},{\"id\":2,\"common_name\":\"Pyramidalis Silver Fir\",\"scientific_name\":[\"Abies alba 'Pyramidalis'\"]" +
                ",\"other_name\":[],\"cycle\":\"Perennial\",\"watering\":\"Average\",\"sunlight\":[\"full sun\"],\"default_image\":" +
                "{\"license\":5,\"license_name\":\"Attribution-ShareAlike License\",\"license_url\":\"https:\\/\\/creativecommons.org\\" +
                "/licenses\\/by-sa\\/2.0\\/\",\"original_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/2_abies_alba_pyramidalis" +
                "\\/og\\/49255769768_df55596553_b.jpg\",\"regular_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\" +
                "/2_abies_alba_pyramidalis\\/regular\\/49255769768_df55596553_b.jpg\",\"medium_url\":\"https:\\/\\/perenual.com\\" +
                "/storage\\/species_image\\/2_abies_alba_pyramidalis\\/medium\\/49255769768_df55596553_b.jpg\",\"small_url\":\"" +
                "https:\\/\\/perenual.com\\/storage\\/species_image\\/2_abies_alba_pyramidalis\\/small\\/49255769768_df55596553_b.jpg\"" +
                ",\"thumbnail\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/2_abies_alba_pyramidalis\\/thumbnail\\/49255769768_df55596553_b.jpg\"}}]}";
        WikiPlant wikiPlant1 = new WikiPlant(1L, "European Silver Fir", List.of("Abies alba"), List.of("Common Silver Fir"),
                "Perennial", "Frequent", List.of("full sun"), "https://perenual.com/storage/species_image/1_abies_alba/small/1536px-Abies_alba_SkalitC3A9.jpg");
        WikiPlant wikiPlant2 = new WikiPlant(2L, "Pyramidalis Silver Fir", List.of("Abies alba 'Pyramidalis'"), List.of(),
                "Perennial", "Average", List.of("full sun"), "https://perenual.com/storage/species_image/2_abies_alba_pyramidalis/small/49255769768_df55596553_b.jpg");
        List<WikiPlant> expectedWikiPlants = List.of(wikiPlant1, wikiPlant2);
        String query = "";
        String uri = PERENUAL_API_URL +"?key="+ apiKey + "&q=" + query;
        URL url = new URI(uri).toURL();
        WikiPlantResponse expectedData = objectMapper.readValue(jsonString, WikiPlantResponse.class);

        Mockito.when(mockObjectMapper.readValue(url, WikiPlantResponse.class)).thenReturn(expectedData);

        List<WikiPlant> actualWikiPlants = plantWikiService.getPlants(query);
        for(int i = 0; i < expectedWikiPlants.size(); i++) {
            WikiPlant actualWikiPlant = actualWikiPlants.get(i);
            WikiPlant expectedWikiPlant = expectedWikiPlants.get(i);
            Assertions.assertEquals(expectedWikiPlant.getName(), actualWikiPlant.getName());
            Assertions.assertEquals(expectedWikiPlant.getId(), actualWikiPlant.getId());
            Assertions.assertEquals(expectedWikiPlant.getCycle(), actualWikiPlant.getCycle());
            Assertions.assertEquals(expectedWikiPlant.getSunlight(), actualWikiPlant.getSunlight());
            Assertions.assertEquals(expectedWikiPlant.getScientificName(), actualWikiPlant.getScientificName());
            Assertions.assertEquals(expectedWikiPlant.getImagePath(), actualWikiPlant.getImagePath());
            Assertions.assertEquals(expectedWikiPlant.getOtherNames(), actualWikiPlant.getOtherNames());
            Assertions.assertEquals(expectedWikiPlant.getWatering(), actualWikiPlant.getWatering());
        }

    }

    @Test
    public void GetPlantsRequested_NothingFound_NoResultsReturned() throws URISyntaxException, IOException {
        String query = "Hello World";
        String replacedQuery = query.replace(" ", "%20");
        String jsonString = "{\"data\":[]}";
        String uri = PERENUAL_API_URL +"?key="+ apiKey + "&q=" + replacedQuery;
        URL url = new URI(uri).toURL();
        WikiPlantResponse expectedData = objectMapper.readValue(jsonString, WikiPlantResponse.class);

        Mockito.when(mockObjectMapper.readValue(url, WikiPlantResponse.class)).thenReturn(expectedData);

        List<WikiPlant> actualWikiPlants = plantWikiService.getPlants(query);
        Assertions.assertEquals(0, actualWikiPlants.size());
    }

    @Test
    public void GetPlantsRequested_NoImagePath_NoImagePathAdded() throws URISyntaxException, IOException {
        String jsonString = "{\"data\":[{\"id\":1,\"common_name\":\"European Silver Fir\",\"scientific_name\":" +
                "[\"Abies alba\"],\"other_name\":[\"Common Silver Fir\"],\"cycle\":\"Perennial\",\"watering\":\"Frequent\"" +
                ",\"sunlight\":[\"full sun\"],\"default_image\":null}]}";

        WikiPlant expectedWikiPlant = new WikiPlant(1L, "European Silver Fir", List.of("Abies alba"), List.of("Common Silver Fir"),
                "Perennial", "Frequent", List.of("full sun"), "");

        String query = "";
        String uri = PERENUAL_API_URL +"?key="+ apiKey + "&q=" + query;
        URL url = new URI(uri).toURL();
        WikiPlantResponse expectedData = objectMapper.readValue(jsonString, WikiPlantResponse.class);

        Mockito.when(mockObjectMapper.readValue(url, WikiPlantResponse.class)).thenReturn(expectedData);
        List<WikiPlant> actualWikiPlants = plantWikiService.getPlants(query);
        WikiPlant actualWikiPlant = actualWikiPlants.get(0);
        Assertions.assertEquals(expectedWikiPlant.getName(), actualWikiPlant.getName());
        Assertions.assertEquals(expectedWikiPlant.getId(), actualWikiPlant.getId());
        Assertions.assertEquals(expectedWikiPlant.getCycle(), actualWikiPlant.getCycle());
        Assertions.assertEquals(expectedWikiPlant.getSunlight(), actualWikiPlant.getSunlight());
        Assertions.assertEquals(expectedWikiPlant.getScientificName(), actualWikiPlant.getScientificName());
        Assertions.assertEquals(expectedWikiPlant.getImagePath(), actualWikiPlant.getImagePath());
        Assertions.assertEquals(expectedWikiPlant.getOtherNames(), actualWikiPlant.getOtherNames());
        Assertions.assertEquals(expectedWikiPlant.getWatering(), actualWikiPlant.getWatering());
    }
}