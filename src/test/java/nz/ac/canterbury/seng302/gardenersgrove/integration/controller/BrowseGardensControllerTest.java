package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.BrowseGardensController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BrowseGardensController.class)
public class BrowseGardensControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    private Gardener testGardener;

    private int defaultPageNumber;

    private int defaultPageSize;

    private List<Garden> gardens;

    @BeforeEach
    public void setUp() {
        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        defaultPageNumber = 0;
        defaultPageSize = 10;
        gardens = new ArrayList<>();
        for(int i = 0; i < 15; i++) {
            gardens.add(new Garden("Test garden" + i, "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, ""));
        }

    }


    @Test
    @WithMockUser
    public void BrowseGardensPageRequested_NoPageNumberAndSizeSpecified_DefaultPageReturned() throws Exception {
        Pageable pageable = PageRequest.of(defaultPageNumber, defaultPageSize);
        Page<Garden> gardenPage = new PageImpl<>(gardens, pageable, gardens.size());
        Mockito.when(gardenService.getGardensPaginated(defaultPageNumber, defaultPageSize)).thenReturn(gardenPage);
        List<Integer> expectedPageNumbers = List.of(1, 2);
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardensPage", gardenPage))
                .andExpect(model().attribute("pageNumbers", expectedPageNumbers))
                .andExpect(view().name("browseGardensTemplate"));
    }

    @Test
    @WithMockUser
    public void BrowseGardensPageRequested_PageNumberAndSizeSpecified_PageReturned() throws Exception {
        int pageNumber = 1;
        int pageSize = 3;
        List<Integer> expectedPageNumbers = List.of(1,2,3,4,5);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Garden> gardenPage = new PageImpl<>(gardens, pageable, gardens.size());
        Mockito.when(gardenService.getGardensPaginated(pageNumber, pageSize)).thenReturn(gardenPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens")
                        .param("pageNo", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardensPage", gardenPage))
                .andExpect(model().attribute("pageNumbers", expectedPageNumbers))
                .andExpect(view().name("browseGardensTemplate"));
    }

    @Test
    @WithMockUser
    public void BrowseGardensPageRequested_NoGardens_PageReturned() throws Exception {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Garden> emptyList = new ArrayList<>();
        Page<Garden> gardenPage = new PageImpl<>(emptyList, pageable, emptyList.size());
        Mockito.when(gardenService.getGardensPaginated(pageNumber, pageSize)).thenReturn(gardenPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens")
                        .param("pageNo", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardensPage", gardenPage))
                .andExpect(model().attributeDoesNotExist("pageNumbers"))
                .andExpect(view().name("browseGardensTemplate"));
    }

}
