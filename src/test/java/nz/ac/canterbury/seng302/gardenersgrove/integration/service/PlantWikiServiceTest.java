package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import nz.ac.canterbury.seng302.gardenersgrove.controller.BrowseGardensController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.CollectionsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlantResponse;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@DataJpaTest
public class PlantWikiServiceTest {

    @MockBean
    private GardenerFormService gardenerFormService;

    private PlantWikiService plantWikiService;

    private ObjectMapper mockObjectMapper;

    private ObjectMapper objectMapper;


    private final String apiKey = "";

  private final String perenualApiUrl = "https://perenual.com/api/species-list";

  @BeforeEach
  void setUp() {
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
  void getPlantsRequested_NoQuery_WikiPlantsReturned() throws URISyntaxException, IOException {

    // Note: this is an actual json response from the api
    String jsonString =
        "{\"data\":[{\"id\":1,\"common_name\":\"European Silver Fir\",\"scientific_name\":"
            + "[\"Abies alba\"],\"other_name\":[\"Common Silver Fir\"],\"cycle\":\"Perennial\",\"watering\":\"Frequent\""
            + ",\"sunlight\":[\"full sun\"],\"default_image\":{\"license\":45,\"license_name\":\"Attribution-ShareAlike "
            + "3.0 Unported (CC BY-SA 3.0)\",\"license_url\":\"https:\\/\\/creativecommons.org\\/licenses\\/by-sa\\/3.0"
            + "\\/deed.en\",\"original_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/1_abies_alba\\/og"
            + "\\/1536px-Abies_alba_SkalitC3A9.jpg\",\"regular_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image"
            + "\\/1_abies_alba\\/regular\\/1536px-Abies_alba_SkalitC3A9.jpg\",\"medium_url\":\"https:\\/\\/perenual.com\\"
            + "/storage\\/species_image\\/1_abies_alba\\/medium\\/1536px-Abies_alba_SkalitC3A9.jpg\",\"small_url\":\""
            + "https:\\/\\/perenual.com\\/storage\\/species_image\\/1_abies_alba\\/small\\/1536px-Abies_alba_SkalitC3A9.jpg\","
            + "\"thumbnail\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/1_abies_alba\\/thumbnail\\/1536px-Abies_alba"
            + "_SkalitC3A9.jpg\"}},{\"id\":2,\"common_name\":\"Pyramidalis Silver Fir\",\"scientific_name\":[\"Abies alba 'Pyramidalis'\"]"
            + ",\"other_name\":[],\"cycle\":\"Perennial\",\"watering\":\"Average\",\"sunlight\":[\"full sun\"],\"default_image\":"
            + "{\"license\":5,\"license_name\":\"Attribution-ShareAlike License\",\"license_url\":\"https:\\/\\/creativecommons.org\\"
            + "/licenses\\/by-sa\\/2.0\\/\",\"original_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/2_abies_alba_pyramidalis"
            + "\\/og\\/49255769768_df55596553_b.jpg\",\"regular_url\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\"
            + "/2_abies_alba_pyramidalis\\/regular\\/49255769768_df55596553_b.jpg\",\"medium_url\":\"https:\\/\\/perenual.com\\"
            + "/storage\\/species_image\\/2_abies_alba_pyramidalis\\/medium\\/49255769768_df55596553_b.jpg\",\"small_url\":\""
            + "https:\\/\\/perenual.com\\/storage\\/species_image\\/2_abies_alba_pyramidalis\\/small\\/49255769768_df55596553_b.jpg\""
            + ",\"thumbnail\":\"https:\\/\\/perenual.com\\/storage\\/species_image\\/2_abies_alba_pyramidalis\\/thumbnail\\/49255769768_df55596553_b.jpg\"}}]}";
    WikiPlant wikiPlant1 =
        new WikiPlant(
            1L,
            "European Silver Fir",
            List.of("Abies alba"),
            List.of("Common Silver Fir"),
            "Perennial",
            "Frequent",
            List.of("full sun"),
            "https://perenual.com/storage/species_image/1_abies_alba/small/1536px-Abies_alba_SkalitC3A9.jpg");
    WikiPlant wikiPlant2 =
        new WikiPlant(
            2L,
            "Pyramidalis Silver Fir",
            List.of("Abies alba 'Pyramidalis'"),
            List.of(),
            "Perennial",
            "Average",
            List.of("full sun"),
            "https://perenual.com/storage/species_image/2_abies_alba_pyramidalis/small/49255769768_df55596553_b.jpg");
    List<WikiPlant> expectedWikiPlants = List.of(wikiPlant1, wikiPlant2);
    String query = "";
    String uri = perenualApiUrl + "?key=" + apiKey + "&q=" + query;
    URL url = new URI(uri).toURL();
    WikiPlantResponse expectedData = objectMapper.readValue(jsonString, WikiPlantResponse.class);

    Mockito.when(mockObjectMapper.readValue(url, WikiPlantResponse.class)).thenReturn(expectedData);

    Object result = plantWikiService.getPlants(query);

    for (int i = 0; i < expectedWikiPlants.size(); i++) {
      WikiPlant actualWikiPlant = ((List<WikiPlant>) result).get(i);
      WikiPlant expectedWikiPlant = expectedWikiPlants.get(i);
      Assertions.assertEquals(expectedWikiPlant.getName(), actualWikiPlant.getName());
      Assertions.assertEquals(expectedWikiPlant.getId(), actualWikiPlant.getId());
      Assertions.assertEquals(expectedWikiPlant.getCycle(), actualWikiPlant.getCycle());
      Assertions.assertEquals(expectedWikiPlant.getSunlight(), actualWikiPlant.getSunlight());
      Assertions.assertEquals(
          expectedWikiPlant.getScientificName(), actualWikiPlant.getScientificName());
      Assertions.assertEquals(expectedWikiPlant.getImagePath(), actualWikiPlant.getImagePath());
      Assertions.assertEquals(expectedWikiPlant.getOtherNames(), actualWikiPlant.getOtherNames());
      Assertions.assertEquals(expectedWikiPlant.getWatering(), actualWikiPlant.getWatering());
    }
  }

  @Test
  void GetPlantsRequested_NothingFound_NoResultsReturned() throws URISyntaxException, IOException {
    String query = "Hello World";
    String replacedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
    String jsonString = "{\"data\":[]}";
    String uri = perenualApiUrl + "?key=" + apiKey + "&q=" + replacedQuery;
    URL url = new URI(uri).toURL();
    WikiPlantResponse expectedData = objectMapper.readValue(jsonString, WikiPlantResponse.class);

    Mockito.when(mockObjectMapper.readValue(url, WikiPlantResponse.class)).thenReturn(expectedData);

    List<WikiPlant> actualWikiPlants = (List<WikiPlant>) plantWikiService.getPlants(query);
    Assertions.assertEquals(0, actualWikiPlants.size());
  }

  @Test
  void GetPlantsRequested_NoImagePath_NoImagePathAdded() throws URISyntaxException, IOException {
    String jsonString =
        "{\"data\":[{\"id\":1,\"common_name\":\"European Silver Fir\",\"scientific_name\":"
            + "[\"Abies alba\"],\"other_name\":[\"Common Silver Fir\"],\"cycle\":\"Perennial\",\"watering\":\"Frequent\""
            + ",\"sunlight\":[\"full sun\"],\"default_image\":null}]}";

    WikiPlant expectedWikiPlant =
        new WikiPlant(
            1L,
            "European Silver Fir",
            List.of("Abies alba"),
            List.of("Common Silver Fir"),
            "Perennial",
            "Frequent",
            List.of("full sun"),
            "");

    String query = "";
    String uri = perenualApiUrl + "?key=" + apiKey + "&q=" + query;
    URL url = new URI(uri).toURL();
    WikiPlantResponse expectedData = objectMapper.readValue(jsonString, WikiPlantResponse.class);

    Mockito.when(mockObjectMapper.readValue(url, WikiPlantResponse.class)).thenReturn(expectedData);
    List<WikiPlant> actualWikiPlants = (List<WikiPlant>) plantWikiService.getPlants(query);
    WikiPlant actualWikiPlant = actualWikiPlants.get(0);
    Assertions.assertEquals(expectedWikiPlant.getName(), actualWikiPlant.getName());
    Assertions.assertEquals(expectedWikiPlant.getId(), actualWikiPlant.getId());
    Assertions.assertEquals(expectedWikiPlant.getCycle(), actualWikiPlant.getCycle());
    Assertions.assertEquals(expectedWikiPlant.getSunlight(), actualWikiPlant.getSunlight());
    Assertions.assertEquals(
        expectedWikiPlant.getScientificName(), actualWikiPlant.getScientificName());
    Assertions.assertEquals(expectedWikiPlant.getImagePath(), actualWikiPlant.getImagePath());
    Assertions.assertEquals(expectedWikiPlant.getOtherNames(), actualWikiPlant.getOtherNames());
    Assertions.assertEquals(expectedWikiPlant.getWatering(), actualWikiPlant.getWatering());
  }
}
