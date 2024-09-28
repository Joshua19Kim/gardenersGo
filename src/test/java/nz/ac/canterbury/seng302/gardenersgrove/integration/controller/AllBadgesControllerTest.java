package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.gardenersgrove.controller.AllBadgesController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantWikiController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers= AllBadgesController.class)
public class AllBadgesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private GardenerFormService gardenerFormService;

    private Gardener testGardener;

    @BeforeEach
    void setUp() {
        testGardener = new Gardener("Test", "Gardener", LocalDate.of(2024, 4, 1), "testgardener@gmail.com", "Password1!");

        // Mock gardener retrieval by email (authentication)
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));
        // todo: Badgify this
//        for(int i = 0; i < totalWikiPlants; i++) {
//            WikiPlant expectedWikiPlant;
//            if(i >= totalWikiPlants - totalSearchWikiPlants) {
//                expectedWikiPlant = new WikiPlant((long) i, "Pine tree" + i, List.of("Pine"), List.of("Common Silver Fir"), "Perennial", "Frequent", List.of("full sun"), "randomImage.jpeg");
//                expectedSearchWikiPlants.add(expectedWikiPlant);
//            } else {
//                expectedWikiPlant = new WikiPlant((long) i, "European Silver Fir " + i, List.of("Abies alba"), List.of("Common Silver Fir"), "Perennial", "Frequent", List.of("full sun"), "randomImage.jpeg");
//            }
//            expectedWikiPlants.add(expectedWikiPlant);
//        }
    }

// todo: badgify

//    @Test
//    @WithMockUser
//    void PlantWikiPageRequested_ValidRequest_PlantWikiPageReturned() throws Exception{
//        String query = "";
//        Mockito.when(plantWikiService.getPlants(query)).thenReturn(expectedWikiPlants);
//        mockMvc.perform(MockMvcRequestBuilders.get("/plantWiki"))
//                .andExpect(view().name("plantWikiTemplate"))
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("resultPlants", expectedWikiPlants));
//    }

}