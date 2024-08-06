package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.MainPageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(controllers = MainPageController.class)
public class MainPageControllerTest {
    private Gardener testGardener;
    private Garden testGarden;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private GardenVisitService gardenVisitService;

    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private RelationshipService relationshipService;


    @BeforeEach
    void setUp() {
        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        Mockito.reset(gardenerFormService);
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));

        testGarden = new Garden("Test garden", "99 test address", null,
                "Christchurch", "New Zealand", null, "9999", testGardener, "");
    }

    @Test
    @WithMockUser
    public void GivenUserHasAGarden_WhenTheyVisitTheHomePage_GardenListShown() throws Exception {
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));

        List<Garden> ownedGardens = new ArrayList<>();
        ownedGardens.add(testGarden);
        when(gardenService.getGardensByGardenerId(anyLong())).thenReturn(ownedGardens);

        mockMvc
                .perform((MockMvcRequestBuilders.get("/home")))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attributeExists("gardens"))
                .andExpect(model().attribute("gardens", ownedGardens));

        verify(gardenerFormService, times(1)).findByEmail(anyString());
        verify(gardenService, times(1)).getGardensByGardenerId(anyLong());
    }

    @Test
    @WithMockUser
    public void GivenUserHasVisitedAGarden_WhenTheyVisitTheHomePage_RecentlyVisitedGardensListShown() throws Exception {
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));

        List<Garden> recentlyVisitedGardens = new ArrayList<>();
        recentlyVisitedGardens.add(testGarden);
        when(gardenVisitService.findRecentGardensByGardenerId(anyLong())).thenReturn(recentlyVisitedGardens);

        mockMvc
                .perform((MockMvcRequestBuilders.get("/home")))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attributeExists("recentGardens"))
                .andExpect(model().attribute("recentGardens", recentlyVisitedGardens));

        verify(gardenerFormService, times(1)).findByEmail(anyString());
        verify(gardenVisitService, times(1)).findRecentGardensByGardenerId(anyLong());
    }

//    @Test
//    @WithMockUser
//    public void GivenUserHasAPlant_WhenTheyVisitTheHomePage_RecentlyAddedPlantsShown() throws Exception {
//        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));
//
//        Plant plant = new Plant("test plant", testGarden);
//        List<Plant> recentlyAddedPlants = new ArrayList<>();
//        recentlyAddedPlants.add(plant);
//        // Yet to be added
////        when(newestPlantsService.findNewestPlantsByGardenerId(anyLong())).thenReturn(recentlyAddedPlants);
//
//        mockMvc
//                .perform((MockMvcRequestBuilders.get("/home")))
//                .andExpect(status().isOk())
//                .andExpect(view().name("mainPageTemplate"))
//                .andExpect(model().attributeExists("newestPlants"))
//                .andExpect(model().attribute("newestPlants", recentlyAddedPlants));
//
//        verify(gardenerFormService, times(1)).findByEmail(anyString());
////        verify(newestPlantsService, times(1)).findNewestPlantsByGardenerId(anyLong());
//    }

    @Test
    @WithMockUser
    public void GivenUserHasFriends_WhenTheyVisitTheHomePage_FriendsListShown() throws Exception {
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));

        Gardener friend = new Gardener("Friend", "Gardener",
                LocalDate.of(2024, 4, 1), "friendgardener@gmail.com",
                "Password1!");
        List<Gardener> friendsList = new ArrayList<>();
        friendsList.add(friend);
        when(relationshipService.getCurrentUserRelationships(anyLong())).thenReturn(friendsList);

        mockMvc
                .perform((MockMvcRequestBuilders.get("/home")))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attribute("friends", friendsList));

        verify(gardenerFormService, times(1)).findByEmail(anyString());
        verify(relationshipService, times(1)).getCurrentUserRelationships(anyLong());
    }

}
