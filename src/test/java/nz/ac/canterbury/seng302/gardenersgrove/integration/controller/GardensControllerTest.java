package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardensController;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GardensController.class)
public class GardensControllerTest {
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
                .andExpect(model().attribute("gardens", gardens));

        verify(gardenerFormService, times(2)).findByEmail(any()); // This is increased to 2 because it happens once in GlobalControllerAdvice
        verify(gardenService, times(1)).getGardensByGardenerId(any());
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
                .andExpect(model().attribute("gardener", otherUser))
                .andExpect(view().name("gardensTemplate"));
    }

    @Test
    @WithMockUser
    public void ViewPrivateGardensNotFriend_RedirectedBackToMyGardens() throws Exception {
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

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/gardens").param("user", "2").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));
    }




}