package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;
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
    public void MyGardensRequested_DefaultValues_GardenDetailsProvided() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        List<Garden> gardens = new ArrayList<>();
        gardens.add(garden);
        when(gardenerFormService.findByEmail(any())).thenReturn(Optional.of(testGardener));
        when(gardenService.getGardensByGardenerId(any())).thenReturn(gardens);

        mockMvc
                .perform((MockMvcRequestBuilders.get("/gardens")))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensTemplate"))
                .andExpect(model().attributeExists("gardens"))
                .andExpect(model().attribute("gardens", gardens));

        verify(gardenerFormService, times(1)).findByEmail(any());
        verify(gardenService, times(1)).getGardensByGardenerId(any());
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
    public void EditGardenDetailsRequested_ExistentIdGiven_GoToEditGardenForm() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        mockMvc
                .perform((MockMvcRequestBuilders.get("/gardens/edit").param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(view().name("editGardensFormTemplate"))
                .andExpect(model().attributeExists("garden"))
                .andExpect(model().attribute("garden", garden));

        verify(gardenService, times(1)).getGarden(1L);
    }

    @Test
    @WithMockUser
    public void EditGardenDetailsRequested_NonExistentIdGiven_GoBackToMyGardens() throws Exception {
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.empty());

        mockMvc
                .perform((MockMvcRequestBuilders.get("/gardens/edit").param("gardenId", "1")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));

        verify(gardenService, times(1)).getGarden(anyLong());
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_ValidValuesWithSize_GardenDetailsUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "100", testGardener, "the greatest garden");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "5 test address")
                                .param("suburb", "Ilam")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                                .param("postcode", "8888")
                                .param("size", "12")
                                .param("description", "the greatest garden"))
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertFalse(garden.getIsGardenPublic());
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("5 test address", garden.getLocation());
        Assertions.assertEquals("Ilam", garden.getSuburb());
        Assertions.assertEquals("Christchurch", garden.getCity());
        Assertions.assertEquals("New Zealand", garden.getCountry());
        Assertions.assertEquals("8888", garden.getPostcode());
        Assertions.assertEquals("12", garden.getSize());
        Assertions.assertEquals("the greatest garden", garden.getDescription());
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
    public void EditedGardenDetailsSubmitted_ValidValuesWithNoSize_GardenDetailsUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "5 test address")
                                .param("suburb", "Ilam")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                                .param("postcode", "8888")
                                .param("size", "")
                                .param("description", ""))
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("5 test address", garden.getLocation());
        Assertions.assertEquals("Ilam", garden.getSuburb());
        Assertions.assertEquals("Christchurch", garden.getCity());
        Assertions.assertEquals("New Zealand", garden.getCountry());
        Assertions.assertEquals("8888", garden.getPostcode());
        Assertions.assertEquals(null, garden.getSize());
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_ValidValuesWithNoSuburb_GardenDetailsUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "5 test address")
                                .param("suburb", "")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                                .param("postcode", "8888")
                                .param("size", "")
                                .param("description", ""))
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("5 test address", garden.getLocation());
        Assertions.assertEquals("", garden.getSuburb());
        Assertions.assertEquals("Christchurch", garden.getCity());
        Assertions.assertEquals("New Zealand", garden.getCountry());
        Assertions.assertEquals("8888", garden.getPostcode());
        Assertions.assertEquals(null, garden.getSize());
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_ValidValuesWithNoPostcode_GardenDetailsUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "5 test address")
                                .param("suburb", "Ilam")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                                .param("postcode", "")
                                .param("size", ""))
                                .param("description", "")
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("5 test address", garden.getLocation());
        Assertions.assertEquals("Ilam", garden.getSuburb());
        Assertions.assertEquals("Christchurch", garden.getCity());
        Assertions.assertEquals("New Zealand", garden.getCountry());
        Assertions.assertEquals("", garden.getPostcode());
        Assertions.assertEquals(null, garden.getSize());
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_ValidValuesWithNoLocation_GardenDetailsUpdated()
            throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "")
                                .param("suburb", "Ilam")
                                .param("city", "Christchurch")
                                .param("country", "New Zealand")
                                .param("postcode", "8888")
                                .param("description", "")
                                .param("size", ""))
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertFalse(garden.getIsGardenPublic());
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("", garden.getLocation());
        Assertions.assertNull(garden.getSize());
        Assertions.assertEquals("", garden.getLocation());
        Assertions.assertEquals("Ilam", garden.getSuburb());
        Assertions.assertEquals("Christchurch", garden.getCity());
        Assertions.assertEquals("New Zealand", garden.getCountry());
        Assertions.assertEquals("8888", garden.getPostcode());
        Assertions.assertNull(garden.getSize());
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_EmptyCity_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(garden));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "88 test address")
                                .param("suburb", "Ilam")
                                .param("city", "")
                                .param("country", "New Zealand")
                                .param("postcode", "8888")
                                .param("description", "")
                                .param("size", "9"))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editGardensFormTemplate"))
                .andExpect(model().attributeExists("cityError", "name", "location", "suburb", "country", "postcode", "size"))
                .andExpect(model().attribute("cityError", "City is required."));
        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_EmptyCountry_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(garden));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "88 test address")
                                .param("suburb", "Ilam")
                                .param("city", "Chch")
                                .param("country", "")
                                .param("postcode", "8888")
                                .param("size", "9")
                                .param("description", ""))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editGardensFormTemplate"))
                .andExpect(model().attributeExists("countryError", "name", "location", "suburb", "city", "postcode", "size"))
                .andExpect(model().attribute("countryError", "Country is required."));
        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_WithBadWord_ErrorMessageAddedAndViewUpdated() throws Exception {
        String badWordDescription = "this is fucking great garden";
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(garden));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "88 test address")
                                .param("suburb", "Ilam")
                                .param("city", "")
                                .param("country", "New Zealand")
                                .param("postcode", "8888")
                                .param("description", badWordDescription)
                                .param("size", "9"))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editGardensFormTemplate"))
                .andExpect(model().attributeExists("descriptionError", "name", "location", "suburb", "country", "postcode", "size"))
                .andExpect(model().attribute("descriptionError", "The description does not match the language standards of the app."));
        verify(gardenService, never()).addGarden(any(Garden.class));

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
                .andExpect(model().attribute("cityError", "City is required."));

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
                .andExpect(model().attribute("countryError", "Country is required."));

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

    @Test
    @WithMockUser
    public void ViewFriendsGardensRequested_UserIsFriend_FriendsGardensViewed() throws Exception {
        Gardener currentUser = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        Gardener otherUser = new Gardener("Test", "Gardener 2", LocalDate.of(2000, 1, 1), "test2@test.com", "Password1!");
        currentUser.setId(1L);
        otherUser.setId(2L);
        gardenerFormService.addGardener(currentUser);
        gardenerFormService.addGardener(otherUser);

        List<Garden> testGardens = new ArrayList<>();
        Garden testGarden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", otherUser, "");
        testGardens.add(testGarden);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(currentUser.getEmail());

        List<Gardener> relationships = new ArrayList<>();
        relationships.add(otherUser);

        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(relationshipService.getCurrentUserRelationships(currentUser.getId())).thenReturn(relationships);
        when(gardenService.getGardensByGardenerId(2L)).thenReturn(testGardens);
        when(gardenerFormService.findById(2L)).thenReturn(Optional.of(otherUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/gardens").param("user", "2")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardens", testGardens))
                .andExpect(model().attribute("gardener", otherUser))
                .andExpect(view().name("gardensTemplate"));

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

        mockMvc.perform(MockMvcRequestBuilders.get("/gardens").param("user", "2")
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

        mockMvc.perform(MockMvcRequestBuilders.get("/gardens").param("user", "2")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));

    }

  @Test
  @WithMockUser
  public void NewTagSubmitted_ValidTagName_GardenDetailsUpdated() throws Exception {
    Garden garden = new Garden("Garden 1", "Location 1", "Sub 1", "city 1", "country 1",
            "postcode 1", "", testGardener, "");
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
    Garden garden = new Garden("Garden 1", "Location 1", "Sub 1", "city 1", "country 1",
            "postcode 1", "", testGardener, "");
    Tag tag = new Tag("Fuck", garden);

    when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
    when(tagService.addTag(any())).thenReturn(tag);

    mockMvc
        .perform(
            (MockMvcRequestBuilders.post("/gardens/addTag")
                .param("tag-input", "Fuck")
                .param("gardenId", "1")
                .with(csrf())))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens/details?gardenId=1&showModal=true"));
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

    Garden garden = new Garden("Garden 1", "Location 1", "Sub 1", "city 1", "country 1",
            "postcode 1", "", testGardener, "");

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
    Garden garden = new Garden("Garden 1", "Location 1", "Sub 1", "city 1", "country 1",
            "postcode 1", "", testGardener, "");
    when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/gardens/addTag")
                .param("tag-input", "Invalid@Tag")
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens/details?gardenId=1&showModal=true"))
        .andExpect(flash().attributeExists("tagValid"))
        .andExpect(
            flash()
                .attribute(
                    "tagValid",
                    "The tag name must only contain alphanumeric characters, spaces, -, _, ', or \""));
  }

  @Test
  @WithMockUser
  public void addTag_InvalidLongTagName_RedirectWithErrorMessage() throws Exception {
    Garden garden = new Garden("Garden 1", "Location 1", "Sub 1", "city 1", "country 1",
            "postcode 1", "", testGardener, "");
    when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/gardens/addTag")
                .param("tag-input", "ThisTagNameIsWayTooLongAndInvalid")
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens/details?gardenId=1&showModal=true"))
        .andExpect(flash().attributeExists("tagValid"))
        .andExpect(flash().attribute("tagValid", "A tag cannot exceed 25 characters"));
  }

  @Test
  @WithMockUser
  public void addTag_EmptyTagName_RedirectWithErrorMessage() throws Exception {
    Garden garden = new Garden("My Garden", "Location 1", "Sub 1", "city 1", "country 1",
              "postcode 1", "", testGardener, "");
    when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/gardens/addTag")
                .param("tag-input", "")
                .param("gardenId", "1")
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/gardens/details?gardenId=1&showModal=true"))
        .andExpect(flash().attributeExists("tagValid"))
        .andExpect(
            flash()
                .attribute(
                    "tagValid",
                    "The tag name must only contain alphanumeric characters, spaces, -, _, ', or \""));
  }

  @Test
  @WithMockUser
  public void addTag_SameTagNameInDifferentGardens() throws Exception {
    Garden garden1 = new Garden("Garden 1", "Location 1", "Sub 1", "city 1", "country 1",
            "postcode 1", "", testGardener, "");
    garden1.setId(1L);
    Garden garden2 = new Garden("Garden 2", "Location 2", "Sub 2", "city 2", "country 2",
            "postcode 2", "", testGardener, "");
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
  public void GetTemperatureOfCity_CityExists_WeatherInformationReturned() throws Exception {
    String[] forecastDates = new String[] {"Date1", "Date2", "Date3"};
    Float[] forecastTemperatures = new Float[] {1f, 2f, 3f};
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
        when(currentWeather.getForecastTemperatures()).thenReturn(List.of(forecastTemperatures));
        when(currentWeather.getForecastImages()).thenReturn(List.of(forecastImages));
        when(currentWeather.getForecastDescriptions()).thenReturn(List.of(forecastDescriptions));
        when(currentWeather.getForecastHumidities()).thenReturn(List.of(forecastHumidities));

        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        GardenFormController gardenFormController = new GardenFormController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenFormController).build();
        MOCK_MVC
                .perform((MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", "1")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("temperature", 12.0f))
                .andExpect(model().attribute("humidity", 50))
                .andExpect(model().attribute("weatherDescription", "Sunny"))
                .andExpect(model().attribute("weatherImage", "image"))
                .andExpect(model().attribute("forecastDates",List.of(forecastDates)))
                .andExpect(model().attribute("forecastTemperature",List.of(forecastTemperatures)))
                .andExpect(model().attribute("forecastWeatherImage",List.of(forecastImages)))
                .andExpect(model().attribute("forecastWeatherDescription",List.of(forecastDescriptions)))
                .andExpect(model().attribute("forcastHumidities",List.of(forecastHumidities)))
                .andExpect(model().attribute("garden", garden));
    }

    @Test
    @WithMockUser
    public void GetTemperatureOfCity_CityDoesntExist_WeatherInformationNotReturned() throws Exception {
        Garden garden = new Garden("Test garden", "FAKELOCATION!123", null, "Christchurch", "New Zealand", null, "9999", testGardener, "")
                ;
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        GardenFormController gardenFormController = new GardenFormController(gardenService, gardenerFormService,
                relationshipService, requestService, weatherService, tagService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(gardenFormController).build();
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
                .andExpect(model().attributeDoesNotExist("forcastHumidities"))
                .andExpect(model().attribute("garden", garden));

    }
}
