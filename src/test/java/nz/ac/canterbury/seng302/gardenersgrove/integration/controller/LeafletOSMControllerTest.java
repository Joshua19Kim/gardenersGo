package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.LeafletOSMController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LeafletOSMController.class)
class LeafletOSMControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdentifiedPlantService identifiedPlantService;

    @MockBean GardenService gardenService;

    @MockBean
    private GardenerFormService gardenerFormService;

    private Gardener gardener;

    @BeforeEach
    public void setUp() {
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardener.setId(1L);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gardener));
    }

    @Test
    @WithMockUser
    void ViewMap_UserHasScannedPlants_ScannedPlantsVisible() throws Exception {
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);
        identifiedPlant.setId(1L);
        List<IdentifiedPlant> scannedPlants = List.of(identifiedPlant);

        when(identifiedPlantService.getGardenerPlantsWithLocations(any())).thenReturn(scannedPlants);
        mockMvc.perform(MockMvcRequestBuilders.get("/map"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("scannedPlants"))
                .andExpect(model().attribute("scannedPlants", scannedPlants));
    }
}
