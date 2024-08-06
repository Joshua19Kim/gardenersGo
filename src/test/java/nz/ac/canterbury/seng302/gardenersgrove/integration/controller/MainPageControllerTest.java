package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.MainPageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenVisit;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenVisitRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MainPageController.class)
public class MainPageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenRepository gardenRepository;
    @MockBean
    private GardenerFormRepository gardenerFormRepository;
    @MockBean
    private GardenVisitRepository gardenVisitRepository;
    @MockBean
    private GardenService gardenService;
    @MockBean
    private RelationshipService relationshipService;
    @MockBean
    private RequestService requestService;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private GardenVisitService gardenVisitService;
    @MockBean
    private GardenVisit gardenVisit;
    private Gardener testGardener;
    private Garden testGarden;

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
    @AfterEach
    public void tearDown() {
        gardenVisitRepository.deleteAllGardenVisitsByGardenerId(testGardener.getId());
        gardenRepository.deleteAll();
        gardenerFormRepository.deleteAll();
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




    @Test
    @WithMockUser
    public void MainPageDisplayed_NewUserHasNotVisitedAnyGarden_NoGardenInRecentGardenList() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", Matchers.empty()));
    }

    @Test
    @WithMockUser
    public void MainPageDisplayed_UserHasVisitedOneGarden_ShowGardenInRecentGardenList() throws Exception {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);

        List<Garden> recentGardenList = new ArrayList<>();
        recentGardenList.add(testBotanicalGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        when(gardenVisitService.findRecentGardensByGardenerId(testGardener.getId())).thenReturn(recentGardenList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", recentGardenList));
    }
    @Test
    @WithMockUser
    public void MainPageDisplayed_UserHasVisitedTwoGarden_ShowsTwoGardensInMostRecentOrder() throws Exception {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);
        Garden testRoseGarden = new Garden("Rose Garden",
                "22 Kirkwood street", null, "Christchurch", "New Zealand", null, "25", testGardener, "");
        gardenService.addGarden(testRoseGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testRoseGarden, currentTime));

        List<Garden> recentGardenList = new ArrayList<>();
        recentGardenList.add(testBotanicalGarden);
        recentGardenList.add(testRoseGarden);
        when(gardenVisitService.findRecentGardensByGardenerId(testGardener.getId())).thenReturn(recentGardenList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", recentGardenList));
    }

    @Test
    @WithMockUser
    public void MainPageDisplayed_UserHasVisitedThreeGarden_ShowsThreeGardensInMostRecentOrder() throws Exception {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);
        Garden testRoseGarden = new Garden("Rose Garden",
                "22 Kirkwood street", null, "Christchurch", "New Zealand", null, "25", testGardener, "");
        gardenService.addGarden(testRoseGarden);
        Garden testTulipGarden = new Garden("Tulip Garden",
                "100 Lovely street", null, "Busan", "South Korea", null, "3000", testGardener, "");
        gardenService.addGarden(testTulipGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testRoseGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testTulipGarden, currentTime));

        List<Garden> recentGardenList = new ArrayList<>();
        recentGardenList.add(testBotanicalGarden);
        recentGardenList.add(testRoseGarden);
        recentGardenList.add(testTulipGarden);
        when(gardenVisitService.findRecentGardensByGardenerId(testGardener.getId())).thenReturn(recentGardenList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", recentGardenList));
    }

    @Test
    @WithMockUser
    public void MainPageDisplayed_UserHasVisitedOneGardenMultipleTimes_ShowOneGardenInRecentGardenList() throws Exception {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        // Visit one garden multiple times
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime.plusSeconds(5)));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime.plusSeconds(10)));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime.plusSeconds(15)));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime.plusSeconds(20)));

        List<Garden> recentGardenList = new ArrayList<>();
        recentGardenList.add(testBotanicalGarden);
        when(gardenVisitService.findRecentGardensByGardenerId(testGardener.getId())).thenReturn(recentGardenList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", recentGardenList));
    }


    @Test
    @WithMockUser
    public void MainPageDisplayed_UserHasVisitedOneGardenTwiceAndAnotherGardenOnce_ShowsTwoGardensInMostRecentOrder() throws Exception {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);
        Garden testRoseGarden = new Garden("Rose Garden",
                "22 Kirkwood street", null, "Christchurch", "New Zealand", null, "25", testGardener, "");
        gardenService.addGarden(testRoseGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        // Visit one garden twice
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime.plusSeconds(5)));
        // Visit Another garden once
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testRoseGarden, currentTime));

        List<Garden> recentGardenList = new ArrayList<>();
        recentGardenList.add(testBotanicalGarden);
        recentGardenList.add(testRoseGarden);
        when(gardenVisitService.findRecentGardensByGardenerId(testGardener.getId())).thenReturn(recentGardenList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", recentGardenList));
    }

    @Test
    @WithMockUser
    public void MainPageDisplayed_UserHasVisitedThreeGardensAndRevisitedFirstGarden_ShowsThreeGardensInMostRecentOrder() throws Exception {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);
        Garden testRoseGarden = new Garden("Rose Garden",
                "22 Kirkwood street", null, "Christchurch", "New Zealand", null, "25", testGardener, "");
        gardenService.addGarden(testRoseGarden);
        Garden testTulipGarden = new Garden("Tulip Garden",
                "100 Lovely street", null, "Busan", "South Korea", null, "3000", testGardener, "");
        gardenService.addGarden(testTulipGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testRoseGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testTulipGarden, currentTime));
        gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));

        List<Garden> recentGardenList = new ArrayList<>();
        recentGardenList.add(testRoseGarden);
        recentGardenList.add(testTulipGarden);
        recentGardenList.add(testBotanicalGarden);
        when(gardenVisitService.findRecentGardensByGardenerId(testGardener.getId())).thenReturn(recentGardenList);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/home")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPageTemplate"))
                .andExpect(model().attribute("recentGardens", recentGardenList));
    }





}
