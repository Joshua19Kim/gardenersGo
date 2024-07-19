package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Optional;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers.PlantAddFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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

@WebMvcTest(controllers = {GardenFormController.class, PlantAddFormController.class})
public class PlantAddFormControllerTest {
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
  public void GardenDetailsRequested_ExistentIdGiven_PlantDetailsProvided() throws Exception {
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
    Plant plant = new Plant("My Plant", garden);
    garden.getPlants().add(plant);
    when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/gardens/details").param("gardenId", "1").with(csrf()))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("My Plant")));
  }

  @Test
  @WithMockUser
  public void PlantFormDisplayed_DefaultValues_ModelAttributesPresent() throws Exception {
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
    when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/gardens/details/plants/form")
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("plantsFormTemplate"))
        .andExpect(content().string(containsString("Name:")))
        .andExpect(content().string(containsString("Plant Count:")))
        .andExpect(content().string(containsString("Description:")))
        .andExpect(content().string(containsString("Date Planted:")));
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "Apple Tree:::",
        "Apple Tree:2::",
        "Apple Tree::My first tree in my garden:",
        "Apple Tree:::2024-04-10",
        "Apple Tree:1:My first tree in my garden:",
        "Apple Tree:1::2024-04-10",
        "Apple Tree::My first tree in my garden:2024-04-10",
        "Apple Tree:1:My first tree in my garden:2024-04-10",
        "Apple Tree:2:Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt:2024-04-10"
      },
      delimiter = ':')
  @WithMockUser
  public void plantFormSubmitted_validInputs_plantAddedAndViewUpdated(
      String name, String count, String description, String date) throws Exception {
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
    when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

    Plant plant = new Plant(name, count, description, date, garden);
    when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

    MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                .file(emptyFile)
                .param("name", name)
                .param("count", count)
                .param("description", description)
                .param("date", date)
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

    verify(plantService, times(2)).addPlant(any(Plant.class));
  }

  @Test
  @WithMockUser
  public void PlantFormSubmitted_EmptyName_ErrorMessageAddedAndViewUpdated() throws Exception {
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
    when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

    String name = "";
    String count = "2";
    String description = "A Plant in My Garden";
    String date = "10/03/2024";
    Plant plant = new Plant(name, count, description, date, garden);
    when(plantService.addPlant(any(Plant.class))).thenReturn(plant);
    when(gardenerFormService.findByEmail(Mockito.anyString()))
            .thenReturn(Optional.of(testGardener));
    MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                .file(emptyFile)
                .param("name", name)
                .param("count", "2.0")
                .param("description", description)
                .param("date", "2024-03-10")
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("plantsFormTemplate"))
        .andExpect(model().attributeExists("nameError", "name", "count", "description", "date"))
        .andExpect(model().attribute("name", name))
        .andExpect(model().attribute("count", "2.0"))
        .andExpect(model().attribute("description", description))
        .andExpect(model().attribute("date", "2024-03-10"))
        .andExpect(
            model()
                .attribute(
                    "nameError",
                    "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes"));

    verify(plantService, never()).addPlant(any(Plant.class));
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
  public void plantFormSubmitted_invalidInput_errorMessageAndViewUpdated(
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
    when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

    Plant plant = new Plant(name, count, description, date, garden);
    when(plantService.addPlant(any(Plant.class))).thenReturn(plant);
    when(gardenerFormService.findByEmail(Mockito.anyString()))
        .thenReturn(Optional.of(testGardener));

    MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                .file(emptyFile)
                .param("name", name)
                .param("count", count)
                .param("description", description)
                .param("date", date)
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("plantsFormTemplate"))
        .andExpect(model().attributeExists("name", "count", "description", "date"))
        .andExpect(model().attribute("name", name))
        .andExpect(model().attribute("count", count))
        .andExpect(model().attribute("description", description))
        .andExpect(model().attribute("date", "2024-04-10"))
        .andExpect(model().attribute(errorName, errorMessage));

    verify(plantService, never()).addPlant(any(Plant.class));
  }
}
