package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenDetailsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GardenDetailsController.class)
public class GardenDetailsControllerTest {
    Gardener testGardener = new Gardener("Test", "Gardener",
            LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
            "Password1!");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private RelationshipService relationshipService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private TagService tagService;

    @MockBean
    private WriteEmail writeEmail;

    @MockBean
    private LocationService locationService;

    @MockBean
    private GardenVisitService gardenVisitService;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        Mockito.reset(gardenerFormService);
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        when(response.body()).thenReturn("test");
        when(locationService.sendRequest(Mockito.anyString())).thenReturn(response);

    }

    @Test
    @WithMockUser
    public void GardenDetailsRequested_ExistentIdGiven_GardenDetailsProvided() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        mockMvc
                .perform((MockMvcRequestBuilders.get("/gardens/details").param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"))
                .andExpect(model().attributeExists("garden"))
                .andExpect(model().attribute("garden", garden));

        verify(gardenService, times(1)).getGarden(1L);
    }

    @Test
    @WithMockUser
    public void GardenDetailsRequested_NonExistentIdGiven_StayOnMyGardens() throws Exception {
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.empty());

        mockMvc
                .perform((MockMvcRequestBuilders.get("/gardens/details").param("gardenId", "1")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));

        verify(gardenService, times(1)).getGarden(anyLong());
    }

    @Test
    @WithMockUser
    public void GardenPublicCheckboxTicked_GardenPublicityUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "100", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        // Can be extrapolated
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail(any())).thenReturn(Optional.of(testGardener));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/details?gardenId=1")
                                .param("isGardenPublic", "true")
                                .with(csrf())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertTrue(garden.getIsGardenPublic());
    }

    @Test
    @WithMockUser
    public void GardenPublicCheckboxUnticked_GardenPublicityUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "100", testGardener, "");
        garden.setIsGardenPublic(true);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        // Can be extrapolated
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail(any())).thenReturn(Optional.of(testGardener));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/details?gardenId=1")
                                .param("isGardenPublic", "false")
                                .with(csrf())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertFalse(garden.getIsGardenPublic());
    }

    @Test
    @WithMockUser
    public void NewTagSubmitted_ValidTagName_GardenDetailsUpdated() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        Tag tag = new Tag("My tag", garden);

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(tagService.addTag(any())).thenReturn(tag);

        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "My tag")
                                .param("gardenId", "1")
                                .with(csrf())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(tagService, times(1)).addTag(any());
    }

    @Test
    @WithMockUser
    public void NewTagSubmitted_OffensiveTagName_TagNotAdded() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        Tag tag = new Tag("Fuck", garden);

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(tagService.addTag(any())).thenReturn(tag);
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "Fuck")
                                .param("gardenId", "1")
                                .with(csrf())))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"));
        verify(tagService, times(0)).addTag(any());
    }

    @Test
    @WithMockUser
    public void GardenDetailsRequested_TagExists_TagDisplayed() throws Exception {
        Gardener currentUser =
                new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        currentUser.setId(1L);
        gardenerFormService.addGardener(currentUser);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(currentUser.getEmail());

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");

        List<String> tags = new ArrayList<>();
        tags.add("My tag");
        tags.add("Another tag");

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(tagService.getTags(any())).thenReturn(tags);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/gardens/details")
                                .param("gardenId", "1")
                                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tags", tags))
                .andExpect(view().name("gardenDetailsTemplate"));
        verify(tagService, times(1)).getTags(any());
    }

    @Test
    @WithMockUser
    public void addTag_InvalidTagName_RedirectWithErrorMessage() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "Invalid@Tag")
                                .param("gardenId", "1")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"))
                .andExpect(model().attributeExists("tagValid"))
                .andExpect(model().attribute("tagValid", "The tag name must only contain alphanumeric characters, spaces, -, _, ', or \" <br/>"));
    }

    @Test
    @WithMockUser
    public void addTag_InvalidLongTagName_RedirectWithErrorMessage() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "ThisTagNameIsWayTooLongAndInvalid")
                                .param("gardenId", "1")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"))
                .andExpect(model().attributeExists("tagValid"))
                .andExpect(model().attribute("tagValid", "A tag cannot exceed 25 characters <br/>"));
    }

    @Test
    @WithMockUser
    public void addTag_EmptyTagName_RedirectWithErrorMessage() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "")
                                .param("gardenId", "1")
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"))
                .andExpect(model().attributeExists("tagValid"))
                .andExpect(model().attribute("tagValid", "The tag name must only contain alphanumeric characters, spaces, -, _, ', or \" <br/>The tag name must contain at least one alphanumeric character"));
    }

    @Test
    @WithMockUser
    public void addTag_SameTagNameInDifferentGardens() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        Garden garden1 = new Garden("Test garden 1", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        garden1.setId(1L);
        Garden garden2 = new Garden("Test garden 2", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        garden2.setId(2L);

        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden1));
        when(gardenService.getGarden(2L)).thenReturn(Optional.of(garden2));
        when(tagService.findTagByNameAndGarden(Mockito.eq("SameTag"), Mockito.any(Garden.class)))
                .thenReturn(Optional.empty());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "Tag")
                                .param("gardenId", "1")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "Tag")
                                .param("gardenId", "2")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=2"));
    }

    @Test
    @WithMockUser
    public void NewTagSubmitted_ValidTagName_BadWordsCounterNotChanged() throws Exception {

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        Tag tag = new Tag("My tag", garden);

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(tagService.addTag(any())).thenReturn(tag);
        int currentBadWords = testGardener.getBadWordCount();
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", "My tag")
                                .param("gardenId", "1")
                                .with(csrf())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        assertEquals(currentBadWords, testGardener.getBadWordCount());
    }

    @Test
    @WithMockUser
    public void NewTagSubmitted_OffensiveTagName_BadWordCounterIncreased() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        Tag tag = new Tag("Fuck", garden);

        when(tagService.addTag(any())).thenReturn(tag);

        int initialBadWordCount = 0;
        testGardener.setBadWordCount(initialBadWordCount);

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        //This code is generated by Claude AI.
        when(tagService.addBadWordCount(testGardener)).thenAnswer(invocation -> {
            Gardener gardener = invocation.getArgument(0);
            gardener.setBadWordCount(gardener.getBadWordCount() + 1);
            return "Submitted tag fails moderation requirements";
        });

        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);

        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/addTag")
                        .param("tag-input", "Fuck")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"))
                .andExpect(model().attributeExists("tagWarning"))
                .andExpect(model().attribute("tagWarning", "Submitted tag fails moderation requirements"));

        verify(tagService).addBadWordCount(testGardener);
        verify(tagService, never()).addTag(any());
    }


    @Test
    @WithMockUser
    public void GetTemperatureOfCity_CityExists_WeatherInformationReturned() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"sunny", "rainy", "cloudy"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        PrevWeather prevWeather = Mockito.mock(PrevWeather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(prevWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(prevWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(prevWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(prevWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(prevWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);
        when(weatherService.getPrevWeather(any())).thenReturn(prevWeather);

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService, locationService, gardenVisitService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("temperature", 12.0f))
                .andExpect(model().attribute("humidity", 50))
                .andExpect(model().attribute("weatherDescription", "Sunny"))
                .andExpect(model().attribute("weatherImage", "image"))
                .andExpect(model().attribute("forecastDates",List.of(forecastDates)))
                .andExpect(model().attribute("forecastMinTemperature",List.of(forecastMinTemperatures)))
                .andExpect(model().attribute("forecastMaxTemperature",List.of(forecastMaxTemperatures)))
                .andExpect(model().attribute("forecastWeatherImage",List.of(forecastImages)))
                .andExpect(model().attribute("forecastWeatherDescription",List.of(forecastDescriptions)))
                .andExpect(model().attribute("forecastHumidities",List.of(forecastHumidities)))
                .andExpect(model().attribute("garden", garden));
    }

    @Test
    @WithMockUser
    public void GetTemperatureOfCity_CityDoesntExist_WeatherInformationNotReturned() throws Exception {
        Garden garden = new Garden("Test garden", "FAKELOCATION!123", null, "Christchurch", "New Zealand", null, "9999", testGardener, "")
                ;
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService, locationService, gardenVisitService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("date"))
                .andExpect(model().attributeDoesNotExist("temperature"))
                .andExpect(model().attributeDoesNotExist("humidity"))
                .andExpect(model().attributeDoesNotExist("weatherDescription"))
                .andExpect(model().attributeDoesNotExist("weatherImage"))
                .andExpect(model().attributeDoesNotExist("currentLocation"))
                .andExpect(model().attributeDoesNotExist("forecastDates"))
                .andExpect(model().attributeDoesNotExist("forecastTemperature"))
                .andExpect(model().attributeDoesNotExist("forecastWeatherImage"))
                .andExpect(model().attributeDoesNotExist("forecastWeatherDescription"))
                .andExpect(model().attributeDoesNotExist("forecastHumidities"))
                .andExpect(model().attribute("garden", garden));

    }

    @Test
    @WithMockUser
    public void GetWeather_WhenRaining_RainNotificationReturned() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"Sunny", "Sunny", "Clear"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Heavy Rain");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        PrevWeather prevWeather = Mockito.mock(PrevWeather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(prevWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(prevWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(prevWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(prevWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(prevWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);
        when(weatherService.getPrevWeather(any())).thenReturn(prevWeather);

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService, locationService, gardenVisitService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("wateringTip", "Outdoor plants don’t need any water today"));
    }

    @Test
    @WithMockUser
    public void GetPrevWeather_WhenPrevSunny_DryNotificationReturned() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"Sunny", "Sunny", "Clear"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Clear");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        PrevWeather prevWeather = Mockito.mock(PrevWeather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(prevWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(prevWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(prevWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(prevWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(prevWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);
        when(weatherService.getPrevWeather(any())).thenReturn(prevWeather);

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService, locationService, gardenVisitService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("wateringTip", "There hasn’t been any rain recently, make sure to water your plants if they need it"));
    }

    @Test
    @WithMockUser
    public void FifthTagSubmitted_OffensiveTagName_TagNotAddedWarningDisplayed() throws Exception {
        int initialBadWordCount = 4;
        testGardener.setBadWordCount(initialBadWordCount);

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        //This code is generated by Claude AI.
        when(tagService.addBadWordCount(testGardener)).thenAnswer(invocation -> {
            Gardener gardener = invocation.getArgument(0);
            gardener.setBadWordCount(gardener.getBadWordCount() + 1);
            return "You have added an inappropriate tag for the fifth time. If you add one more, your account will be blocked for one week.";
        });

        Weather currentWeather = Mockito.mock(Weather.class);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Sunny");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of("Date1", "Date2", "Date3"));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(1f, 2f, 3f));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(2f, 3f, 4f));
        when(currentWeather.getForecastImages()).thenReturn(List.of("image1", "image2", "image3"));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of("sunny", "rainy", "cloudy"));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(1, 2, 3));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);
        Mockito.doNothing().when(writeEmail).sendTagWarningEmail(Mockito.any(Gardener.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/addTag")
                        .param("tag-input", "Fuck")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardenDetailsTemplate"))
                .andExpect(model().attributeExists("tagWarning"))
                .andExpect(model().attribute("tagWarning", "You have added an inappropriate tag for the fifth time. If you add one more, your account will be blocked for one week."));

        verify(tagService).addBadWordCount(testGardener);
        verify(tagService, never()).addTag(any());
        assertEquals(initialBadWordCount + 1, testGardener.getBadWordCount());

    }

    @Test
    @WithMockUser
    public void GetDate_WhenNotificationClosed_NoNotificationReturned() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"Sunny", "Sunny", "Clear"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Clear");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        PrevWeather prevWeather = Mockito.mock(PrevWeather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(prevWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(prevWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(prevWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(prevWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(prevWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        LocalDate currentDate = LocalDate.now();
        garden.setLastNotified(currentDate);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);
        when(weatherService.getPrevWeather(any())).thenReturn(prevWeather);

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService, locationService, gardenVisitService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("wateringTip"));
        assertEquals(garden.getLastNotified(), currentDate);
    }
    @Test
    @WithMockUser
    public void GivenOnGardenDetails_WhenNotificationClosed_LastNotifiedDateUpdated() throws Exception {
        String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
        Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMinTemperatures = new Float[] {1f, 2f, 3f};
        Float[] forecastMaxTemperatures = new Float[] {2f, 3f, 4f};
        String[] forecastImages = new String[] {"image1", "image2", "image3"};
        String[] forecastDescriptions = new String[] {"Sunny", "Sunny", "Clear"};
        Integer[] forecastHumidities = new Integer[] {1, 2, 3};

        Weather currentWeather = Mockito.mock(Weather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(currentWeather.getTemperature()).thenReturn(12.0f);
        when(currentWeather.getHumidity()).thenReturn(50);
        when(currentWeather.getWeatherDescription()).thenReturn("Clear");
        when(currentWeather.getWeatherImage()).thenReturn("image");
        when(currentWeather.getCurrentLocation()).thenReturn("Christchurch");
        when(currentWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(currentWeather.getForecastMinTemperatures()).thenReturn(List.of(forecastMinTemperatures));
        when(currentWeather.getForecastMaxTemperatures()).thenReturn(List.of(forecastMaxTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        PrevWeather prevWeather = Mockito.mock(PrevWeather.class);
        when(weatherService.getWeather(Mockito.anyString())).thenReturn(currentWeather);
        when(prevWeather.getForecastDates()).thenReturn(List.of(forecastDates));
        when(prevWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(prevWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(prevWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(prevWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        LocalDate currentDate = LocalDate.now();
        garden.setLastNotified(currentDate);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(weatherService.getWeather(any())).thenReturn(currentWeather);
        when(weatherService.getPrevWeather(any())).thenReturn(prevWeather);

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService, locationService, gardenVisitService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.post("/gardens/details/dismissNotification")
                        .param("gardenId", "1")))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attributeDoesNotExist("wateringTip"));
        assertEquals(garden.getLastNotified(), currentDate);
    }

    @Test
    @WithMockUser
    public void ViewFriendsGardensRequested_UserIsNotFriend_RedirectedToOwnGardens() throws Exception {
        Gardener currentUser = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        Gardener otherUser = new Gardener("Test", "Gardener 2", LocalDate.of(2000, 1, 1), "test2@test.com", "Password1!");
        currentUser.setId(1L);
        otherUser.setId(2L);
        gardenerFormService.addGardener(currentUser);
        gardenerFormService.addGardener(otherUser);

        List<Gardener> relationships = new ArrayList<>();

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(currentUser.getEmail());

        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(relationshipService.getCurrentUserRelationships(currentUser.getId())).thenReturn(relationships);
        when(gardenerFormService.findById(2L)).thenReturn(Optional.of(otherUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/gardens/details").param("gardenId", "2")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));
    }

    @Test
    @WithMockUser
    public void ViewFriendsGardensRequested_FriendDoesNotExist_RedirectedToOwnGardens() throws Exception {
        Gardener currentUser = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        currentUser.setId(1L);
        gardenerFormService.addGardener(currentUser);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(currentUser.getEmail());

        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(gardenerFormService.findById(2L)).thenReturn(Optional.empty());

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/gardens/details").param("gardenId", "2").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));
    }

    @Test
    @WithMockUser
    public void UserEntersSixthBadWord_UserIsBanned_UserIsLoggedOut() throws Exception {
        Gardener currentUser = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        currentUser.setId(1L);
        currentUser.setBadWordCount(5);
        gardenerFormService.addGardener(currentUser);

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        Tag tag = new Tag("My tag", garden);

        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(tagService.addTag(any())).thenReturn(tag);
        when(tagService.addBadWordCount(any(Gardener.class))).thenReturn("BANNED");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/gardens/addTag").param("gardenId", "2").param("tag-input","fuck").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?banned"));
    }

}