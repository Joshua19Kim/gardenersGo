package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.BrowseGardensController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantSpeciesCollectionsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantSpeciesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlantSpeciesCollectionsController.class)

public class PlantSpeciesCollectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantSpeciesService plantSpeciesService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private GardenerFormService gardenerFormService;

    private int defaultPageNumber;

    private int defaultPageSize;

    private int totalPlantSpecies;

    private List<PlantSpecies> allPlantSpecies;

    @BeforeEach
    public void setUp() {
        totalPlantSpecies = 21;
        allPlantSpecies = new ArrayList<>();
        defaultPageNumber = 0;
        defaultPageSize = 12;
        for(int i = 0; i < totalPlantSpecies; i++) {
            PlantSpecies plantSpecies = new PlantSpecies("Plant" + i, i, "randomImage" + i + ".jpg");
            allPlantSpecies.add(plantSpecies);
            plantSpeciesService.addPlantSpecies(plantSpecies);
        }
    }

    @Test
    @WithMockUser
    public void MyCollectionPageRequested_NoPageNumberSpecified_DefaultPageReturned() throws Exception {
        Pageable pageable = PageRequest.of(defaultPageNumber, defaultPageSize);
        Page<PlantSpecies> plantSpeciesPage = new PageImpl<>(allPlantSpecies, pageable, totalPlantSpecies);
        Mockito.when(plantSpeciesService.getAllPlantSpeciesPaginated(defaultPageNumber, defaultPageSize)).thenReturn(plantSpeciesPage);
        Gardener mockedGardener = Mockito.mock(Gardener.class);
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(mockedGardener));
        Mockito.when(gardenService.getGardensByGardenerId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        List<Integer> expectedPageNumbers = List.of(1, 2);
        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("plantSpeciesList", plantSpeciesPage))
                .andExpect(model().attribute("pageNumbers", expectedPageNumbers))
                .andExpect(model().attributeExists("gardens"))
                .andExpect(view().name("myCollectionTemplate"));
    }

    @Test
    @WithMockUser
    public void MyCollectionPageRequested_PageNumberSpecified_PageReturned() throws Exception {
        int pageNumber = 1;
        List<Integer> expectedPageNumbers = List.of(1,2);
        Pageable pageable = PageRequest.of(pageNumber, defaultPageSize);
        Page<PlantSpecies> plantSpeciesPage = new PageImpl<>(allPlantSpecies.subList(defaultPageSize, totalPlantSpecies), pageable, totalPlantSpecies);
        Mockito.when(plantSpeciesService.getAllPlantSpeciesPaginated(pageNumber, defaultPageSize)).thenReturn(plantSpeciesPage);
        Gardener mockedGardener = Mockito.mock(Gardener.class);
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(mockedGardener));
        Mockito.when(gardenService.getGardensByGardenerId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection")
                        .param("pageNo", String.valueOf(pageNumber)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("plantSpeciesList", plantSpeciesPage))
                .andExpect(model().attribute("pageNumbers", expectedPageNumbers))
                .andExpect(view().name("myCollectionTemplate"));
    }


    @Test
    @WithMockUser
    public void MyCollectionPageRequested_NoPlantSpecies_PageReturned() throws Exception {
        int pageNumber = 1;
        Pageable pageable = PageRequest.of(pageNumber, defaultPageSize);
        List<PlantSpecies> emptyList = new ArrayList<>();
        Page<PlantSpecies> plantSpeciesPage = new PageImpl<>(emptyList, pageable, emptyList.size());
        Mockito.when(plantSpeciesService.getAllPlantSpeciesPaginated(pageNumber, defaultPageSize)).thenReturn(plantSpeciesPage);
        Gardener mockedGardener = Mockito.mock(Gardener.class);
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(mockedGardener));
        Mockito.when(gardenService.getGardensByGardenerId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection")
                        .param("pageNo", String.valueOf(pageNumber)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("plantSpeciesList", plantSpeciesPage))
                .andExpect(model().attributeDoesNotExist("pageNumbers"))
                .andExpect(model().attributeExists("gardens"))
                .andExpect(view().name("myCollectionTemplate"));
    }

}
