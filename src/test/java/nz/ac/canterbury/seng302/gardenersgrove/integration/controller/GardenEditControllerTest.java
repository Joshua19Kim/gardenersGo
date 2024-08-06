package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenEditController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GardenEditController.class)
public class GardenEditControllerTest {
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
        assertEquals("Rose Garden", garden.getName());
        assertEquals("5 test address", garden.getLocation());
        assertEquals("Ilam", garden.getSuburb());
        assertEquals("Christchurch", garden.getCity());
        assertEquals("New Zealand", garden.getCountry());
        assertEquals("8888", garden.getPostcode());
        assertEquals(null, garden.getSize());
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
        assertEquals("Rose Garden", garden.getName());
        assertEquals("5 test address", garden.getLocation());
        assertEquals("", garden.getSuburb());
        assertEquals("Christchurch", garden.getCity());
        assertEquals("New Zealand", garden.getCountry());
        assertEquals("8888", garden.getPostcode());
        assertEquals(null, garden.getSize());
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
        assertEquals("Rose Garden", garden.getName());
        assertEquals("5 test address", garden.getLocation());
        assertEquals("Ilam", garden.getSuburb());
        assertEquals("Christchurch", garden.getCity());
        assertEquals("New Zealand", garden.getCountry());
        assertEquals("", garden.getPostcode());
        assertEquals(null, garden.getSize());
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
        assertEquals("Rose Garden", garden.getName());
        assertEquals("", garden.getLocation());
        Assertions.assertNull(garden.getSize());
        assertEquals("", garden.getLocation());
        assertEquals("Ilam", garden.getSuburb());
        assertEquals("Christchurch", garden.getCity());
        assertEquals("New Zealand", garden.getCountry());
        assertEquals("8888", garden.getPostcode());
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
                .andExpect(model().attribute("cityError", "City is required"));
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
                .andExpect(model().attribute("countryError", "Country is required"));
        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_InvalidAddresses_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(garden));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                // 84 characters for testing location
                                .param("location", "666666666666666666666666666666666666666666666666666666666666666666666666666666666666")
                                // 93 characters for testing suburb
                                .param("suburb", "666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666")
                                // 183 characters for testing city
                                .param("city", "777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777")
                                // 66 characters for testing country
                                .param("country", "666666666666666666666666666666666666666666666666666666666666666666666666666666666666")
                                // 16 characters for testing postcode
                                .param("postcode", "7777777777777777")
                                .param("size", "9")
                                .param("description", ""))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editGardensFormTemplate"))
                .andExpect(model().attributeExists("locationError","cityError", "countryError", "suburbError", "postcodeError", "name", "location", "suburb", "country", "postcode", "size"))
                .andExpect(model().attribute("cityError", "Please enter a city without only numerical characters <br/>Please enter a city less than 180 characters"))
                .andExpect(model().attribute("suburbError", "Please enter a suburb without only numerical characters <br/>Please enter a suburb less than 90 characters"))
                .andExpect(model().attribute("countryError", "Please enter a country without only numerical characters <br/>Please enter a country less than 60 characters"))
                .andExpect(model().attribute("postcodeError", "Please enter a postcode less than 10 characters"))
                .andExpect(model().attribute("locationError", "Please enter a street number and name without only numerical characters <br/>Please enter a street number and name less than 60 characters"));
        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_InvalidAddressesWithOnlySpecialCharacters_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "999", testGardener, "");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(garden));
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "%&*%$^&*$%^&")
                                .param("suburb", "%&*%$^&*$%^&")
                                .param("city", "%&*%$^&*$%^&")
                                .param("country", "%&*%$^&*$%^&")
                                .param("postcode", "%&*%$^&*$%^&")
                                .param("size", "9")
                                .param("description", ""))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editGardensFormTemplate"))
                .andExpect(model().attributeExists("locationError","cityError", "countryError", "suburbError", "postcodeError", "name", "location", "suburb", "country", "postcode", "size"))
                .andExpect(model().attribute("cityError", "City must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>City must contain at least one alphanumeric character <br/>"))
                .andExpect(model().attribute("suburbError", "Suburb must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>Suburb must contain at least one alphanumeric character <br/>"))
                .andExpect(model().attribute("countryError", "Country must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>Country must contain at least one alphanumeric character <br/>"))
                .andExpect(model().attribute("postcodeError", "Postcode must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>Postcode must contain at least one alphanumeric character <br/>Please enter a postcode less than 10 characters"))
                .andExpect(model().attribute("locationError", "Street number and name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes <br/>Street number and name must contain at least one alphanumeric character <br/>"));

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



}