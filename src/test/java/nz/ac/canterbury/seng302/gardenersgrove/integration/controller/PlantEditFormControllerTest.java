package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers.PlantEditFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {GardenFormController.class, PlantEditFormController.class})
public class PlantEditFormControllerTest {
  Gardener testGardener =
      new Gardener(
          "Test", "Gardener", LocalDate.of(2024, 4, 1), "testgardener@gmail.com", "Password1!");

  @Autowired private MockMvc mockMvc;

  @MockBean private GardenService gardenService;

  @MockBean
  // This is not explicitly used but is necessary for adding gardeners to the repository for testing
  private GardenerFormService gardenerFormService;

  @MockBean private PlantService plantService;

  @MockBean private ImageService imageService;
  @MockBean private RequestService requestService;

  @MockBean private RelationshipService relationshipService;

  @MockBean private TagService tagService;

  @MockBean private WeatherService weatherService;

  @Test
  @WithMockUser
  public void EditPlantFormRequested_NonExistentPlantId_GoBackToMyGardens() throws Exception {
    String plantId = "1";
    when(gardenService.getGardenResults()).thenReturn(new ArrayList<>());
    when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.empty());

    mockMvc
        .perform(
            (MockMvcRequestBuilders.get("/gardens/details/plants/edit")
                .param("plantId", plantId)
                .with(csrf())))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens"));
  }

  @Test
  @WithMockUser
  public void EditPlantFormRequested_ExistentPlantId_GoToEditPlantForm() throws Exception {
    Garden garden =
        new Garden(
            "Test garden",
            "Ilam",
            null,
            "Christchurch",
            "New Zealand",
            null,
            "9999",
            testGardener,
            "");
    Plant plant = new Plant("My Plant", "2", "Rose", "12/06/2004", garden);
    String plantId = "2";
    List<Garden> gardens = new ArrayList<>();
    when(gardenService.getGardenResults()).thenReturn(gardens);
    when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));
    when(requestService.getRequestURI(any(HttpServletRequest.class)))
        .thenReturn("/gardens/details/plants/edit");

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/gardens/details/plants/edit")
                .param("plantId", plantId)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("editPlantFormTemplate"))
        .andExpect(model().attributeExists("plant", "garden", "gardens", "requestURI"))
        .andExpect(model().attribute("plant", plant))
        .andExpect(model().attribute("garden", garden))
        .andExpect(model().attribute("gardens", gardens))
        .andExpect(model().attribute("requestURI", "/gardens/details/plants/edit"));
  }

  @Test
  @WithMockUser
  public void EditPlantFormSubmitted_AllValidChanges_PlantUpdatedAndBackToGardenDetails()
      throws Exception {
    Garden garden =
        new Garden(
            "Test garden",
            "Ilam",
            null,
            "Christchurch",
            "New Zealand",
            null,
            "9999",
            testGardener,
            "");
    String name = "My Plant 2";
    String plantId = "1";
    String count = "3";
    String description = "Daisy";
    String date = "10/03/2024";
    Plant plant = new Plant("My Plant", "2", "Rose", "10/10/2023", garden);
    when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));
    when(plantService.addPlant(plant)).thenReturn(plant);

    MockMultipartFile mockMultipartFile =
        new MockMultipartFile("file", "image.jpg", "image/jpeg", "image content".getBytes());
    when(imageService.savePlantImage(mockMultipartFile, plant)).thenReturn(Optional.empty());
    when(imageService.checkValidImage(mockMultipartFile)).thenReturn(Optional.empty());

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/gardens/details/plants/edit")
                .file(mockMultipartFile)
                .param("name", name)
                .param("count", count)
                .param("description", description)
                .param("date", "2024-03-10")
                .param("plantId", plantId)
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens/details?gardenId=" + garden.getId()));
    verify(imageService, times(1)).savePlantImage(mockMultipartFile, plant);
    Assertions.assertEquals(name, plant.getName());
    Assertions.assertEquals(count, plant.getCount());
    Assertions.assertEquals(description, plant.getDescription());
    Assertions.assertEquals(date, plant.getDatePlanted());
  }

  @ParameterizedTest
  @WithMockUser
  @CsvSource(
      value = {
        "'':2:My first tree in my garden:2024-04-10:nameError:Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes",
        "@pple Tree:2:My first tree in my garden:2024-04-10:nameError:Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes",
        "Apple Tree:two:My first tree in my garden:2024-04-10:countError:Plant count must be a positive number",
        "Apple Tree:-2:My first tree in my garden:2024-04-10:countError:Plant count must be a positive number",
        "Apple Tree:2:Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt.:2024-04-10:descriptionError:Plant description must be less than 512 characters"
      },
      delimiter = ':')
  public void editPlantFormSubmitted_invalidInput_errorMessagesAdded(
      String name,
      String count,
      String description,
      String date,
      String errorName,
      String errorMessage)
      throws Exception {
    Garden garden =
        new Garden(
            "Test garden",
            "Ilam",
            null,
            "Christchurch",
            "New Zealand",
            null,
            "9999",
            testGardener,
            "");
    String plantId = "1";
    Plant plant = new Plant("My Plant", "2", "Rose", "10/10/2023", garden);
    when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));

    MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
    when(gardenerFormService.findByEmail(Mockito.anyString()))
        .thenReturn(Optional.of(testGardener));
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/gardens/details/plants/edit")
                .file(emptyFile)
                .param("name", name)
                .param("count", count)
                .param("description", description)
                .param("date", date)
                .param("plantId", plantId)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("editPlantFormTemplate"))
        .andExpect(
            model()
                .attributeExists(
                     "name", "count", "description", "date", "plant", "garden"))
        .andExpect(model().attribute("name", name))
        .andExpect(model().attribute("count", count))
        .andExpect(model().attribute("description", description))
        .andExpect(model().attribute("date", "2024-04-10"))
        .andExpect(model().attribute(errorName, errorMessage))
        .andExpect(model().attribute("plant", plant))
        .andExpect(model().attribute("garden", garden));

    verify(plantService, never()).addPlant(plant);
    verify(imageService, never()).savePlantImage(emptyFile, plant);
  }
}
