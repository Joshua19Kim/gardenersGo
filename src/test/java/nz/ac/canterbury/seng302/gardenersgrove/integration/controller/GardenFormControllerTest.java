package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GardenFormController.class)
public class GardenFormControllerTest {
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

    @BeforeEach
    void setUp() {
        Mockito.reset(gardenerFormService);
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));

    }

    @Test
    @WithMockUser
    public void GardenFormDisplayed_DefaultValues_ModelAttributesPresent() throws Exception {
        List<Garden> gardens = new ArrayList<>();
        gardens.add(new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, ""));
        when(gardenService.getGardensByGardenerId(any())).thenReturn(gardens);
        when(gardenerFormService.findByEmail(any())).thenReturn(Optional.of(testGardener));
        mockMvc.perform(MockMvcRequestBuilders.get("/gardens/form")
                        .param("redirect", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("requestURI", "gardens"))
                .andExpect(model().attribute("requestURI", ""))
                .andExpect(model().attribute("gardens", gardens))
                .andExpect(view().name("gardensFormTemplate"));
    }

    @Test
    @WithMockUser
    public void CreateGardenFormSubmitted_ValidInputs_GardenAddedAndViewUpdated() throws Exception {
        String name = "Test garden";
        String location = "99 test address";
        String suburb = "Ilam";
        String city = "Christchurch";
        String country = "New Zealand";
        String postcode = "9999";
        String size = "1.0";

        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", testGardener, "");
        garden.setId(1L);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(garden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("description", "")
                        .param("redirect", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(gardenService, times(1)).addGarden(any(Garden.class));
    }
    @Test
    @WithMockUser
    public void CreateGardenFormSubmitted_ValidInputsEmptyLocationSuburbPostcode_GardenAddedAndViewUpdated() throws Exception {
        String name = "Test garden";
        String location = "";
        String suburb = "";
        String city = "Christchurch";
        String country = "New Zealand";
        String postcode = "";
        String size = "1.0";

        Garden garden = new Garden("Test garden", "", "", "Christchurch", "New Zealand", "", "1.0", testGardener, "");
        garden.setId(1L);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(garden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("redirect", "")
                        .param("description", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(gardenService, times(1)).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void CreateGardenFormSubmitted_ValidInputsWithBadWordDescription_ErrorMessageAddedViewUpdate() throws Exception {
        String name = "Test garden";
        String location = "";
        String suburb = "";
        String city = "Christchurch";
        String country = "New Zealand";
        String postcode = "";
        String size = "1.0";
        String badWordDescription = "This is fuckking greatest garden";

        Garden garden = new Garden("Test garden", "", "", "Christchurch", "New Zealand", "", "1.0", testGardener, "The greatest garden.");
        garden.setId(1L);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(garden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("redirect", "")
                        .param("description", badWordDescription)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("descriptionError", "name", "location", "suburb", "country", "postcode", "size"))
                .andExpect(model().attribute("descriptionError", "The description does not match the language standards of the app."));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }



    @Test
    @WithMockUser
    public void GardenFormSubmitted_EmptyName_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "";
        String location = "99 test address";
        String suburb = "Ilam";
        String city = "Christchurch";
        String country = "New Zealand";
        String postcode = "9999";
        String size = "1.0";
        String redirectURI = "";


        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("description", "")
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("nameError", "name", "location", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("nameError", "Garden name cannot be empty"));


        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_InvalidName_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "*!&";
        String location = "Ilam";
        String suburb = "Ilam";
        String city = "Christchurch";
        String country = "New Zealand";
        String postcode = "9999";
        String size = "1.0";
        String redirectURI = "/gardens";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("description", "")
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("nameError", "name", "location", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("nameError",
                        "Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_EmptyCity_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "My Garden";
        String location = "Ilam";
        String suburb = "Ilam";
        String city = "";
        String country = "New Zealand";
        String postcode = "9999";
        String size = "1.0";
        String redirectURI = "";
        when(gardenerFormService.findByEmail("testEmail@gmail.com")).thenReturn(Optional.of(testGardener));
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("description", "")
                        .param("country", country)
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("cityError", "name", "location", "suburb", "country", "postcode", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("suburb", suburb))
                .andExpect(model().attribute("city", city))
                .andExpect(model().attribute("country", country))
                .andExpect(model().attribute("postcode", postcode))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("cityError", "City is required"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_EmptyCountry_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "My Garden";
        String location = "Ilam";
        String suburb = "Ilam";
        String city = "Christchurch";
        String country = "";
        String postcode = "9999";
        String size = "1.0";
        String redirectURI = "";
        when(gardenerFormService.findByEmail("testEmail@gmail.com")).thenReturn(Optional.of(testGardener));
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("description", "")
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("countryError", "name", "location", "suburb", "city", "postcode", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("suburb", suburb))
                .andExpect(model().attribute("city", city))
                .andExpect(model().attribute("country", country))
                .andExpect(model().attribute("postcode", postcode))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("countryError", "Country is required"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_InvalidSize_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "My Garden";
        String location = "20 kirkwood";
        String suburb = "Ilam";
        String city = "Christchurch";
        String country = "New Zealand";
        String postcode = "9999";
        String size = "-1.0";
        String redirectURI = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("suburb", suburb)
                        .param("city", city)
                        .param("country", country)
                        .param("description", "")
                        .param("postcode", postcode)
                        .param("size", size)
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("sizeError", "name", "location", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("sizeError", "Garden size must be a positive number"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }
}


