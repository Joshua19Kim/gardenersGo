package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private TagService tagService;

    @Test
    @WithMockUser
    public void MyGardensRequested_DefaultValues_GardenDetailsProvided() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam", testGardener);
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
        Garden garden = new Garden("My Garden", "Ilam", testGardener);
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
        Garden garden = new Garden("My Garden", "Ilam", testGardener);
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
        Garden garden = new Garden("My Garden", "Ilam", "32", testGardener);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "Riccarton")
                                .param("size", "100"))
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("Riccarton", garden.getLocation());
        Assertions.assertEquals("100", garden.getSize());
    }

    @Test
    @WithMockUser
    public void EditedGardenDetailsSubmitted_ValidValuesWithNoSize_GardenDetailsUpdated()
            throws Exception {
        Garden garden = new Garden("My Garden", "Ilam", "32", testGardener);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));
        when(gardenService.addGarden(garden)).thenReturn(garden);
        mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/edit")
                                .param("gardenId", "1")
                                .param("name", "Rose Garden")
                                .param("location", "Riccarton")
                                .param("size", ""))
                                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
        verify(gardenService, times(1)).getGarden(1L);
        verify(gardenService, times(1)).addGarden(garden);
        Assertions.assertEquals("Rose Garden", garden.getName());
        Assertions.assertEquals("Riccarton", garden.getLocation());
        Assertions.assertEquals(null, garden.getSize());
    }

    @Test
    @WithMockUser
    public void GardenFormDisplayed_DefaultValues_ModelAttributesPresent() throws Exception {
        List<Garden> gardens = new ArrayList<>();
        gardens.add(new Garden("My Garden", "Ilam", "32", testGardener));
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
        String name = "My Garden";
        String location = "Ilam";
        String size = "1.0";
        Garden garden = new Garden(name, location, size, testGardener);
        garden.setId(1L);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(garden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("size", size)
                        .param("redirect", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(gardenService, times(1)).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_EmptyName_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "";
        String location = "Ilam";
        String size = "1.0";
        String redirectURI = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("size", size)
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
        String size = "1.0";
        String redirectURI = "/gardens";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("size", size)
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("nameError", "name", "location", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("nameError", "Garden name must only include letters, numbers, spaces, dots, hyphens, or apostrophes"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_EmptyLocation_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "My Garden";
        String location = "";
        String size = "1.0";
        String redirectURI = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("size", size)
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("locationError", "name", "location", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("locationError", "Location cannot be empty"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_InvalidLocation_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "My Garden";
        String location = "*!&";
        String size = "1.0";
        String redirectURI = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
                        .param("size", size)
                        .param("redirect", redirectURI)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("gardensFormTemplate"))
                .andExpect(model().attributeExists("locationError", "name", "location", "size", "requestURI"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("location", location))
                .andExpect(model().attribute("size", size))
                .andExpect(model().attribute("requestURI", redirectURI))
                .andExpect(model().attribute("locationError", "Location name must only include letters, numbers, spaces, commas, dots, hyphens or apostrophes"));

        verify(gardenService, never()).addGarden(any(Garden.class));
    }

    @Test
    @WithMockUser
    public void GardenFormSubmitted_InvalidSize_ErrorMessageAddedAndViewUpdated() throws Exception {
        String name = "My Garden";
        String location = "Ilam";
        String size = "-1.0";
        String redirectURI = "";
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", name)
                        .param("location", location)
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
        Garden testGarden = new Garden("My Garden", "Ilam", otherUser);
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
}
