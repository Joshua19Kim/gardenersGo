package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.BrowseGardensController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
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

import static java.lang.Math.ceil;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BrowseGardensController.class)
public class BrowseGardensControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private TagService tagService;

    private Gardener testGardener;

    private int defaultPageNumber;

    private int defaultPageSize;

    private List<Garden> gardens;

    private List<String> allTags;

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
        allTags = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            allTags.add("tag" + i);
        }

    }


    @Test
    @WithMockUser
    public void BrowseGardensPageRequested_NoPageNumberSpecified_DefaultPageReturned() throws Exception {
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
    public void BrowseGardensPageRequested_PageNumberSpecified_PageReturned() throws Exception {
        int pageNumber = 1;
        List<Integer> expectedPageNumbers = List.of(1,2);
        Pageable pageable = PageRequest.of(pageNumber, defaultPageSize);
        Page<Garden> gardenPage = new PageImpl<>(gardens.subList(defaultPageSize, gardens.size()), pageable, gardens.size());
        Mockito.when(gardenService.getGardensPaginated(pageNumber, defaultPageSize)).thenReturn(gardenPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens")
                        .param("pageNo", String.valueOf(pageNumber)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardensPage", gardenPage))
                .andExpect(model().attribute("pageNumbers", expectedPageNumbers))
                .andExpect(view().name("browseGardensTemplate"));
    }

    @Test
    @WithMockUser
    public void BrowseGardensPageRequested_NoGardens_PageReturned() throws Exception {
        int pageNumber = 1;
        Pageable pageable = PageRequest.of(pageNumber, defaultPageSize);
        List<Garden> emptyList = new ArrayList<>();
        Page<Garden> gardenPage = new PageImpl<>(emptyList, pageable, emptyList.size());
        Mockito.when(gardenService.getGardensPaginated(pageNumber, defaultPageSize)).thenReturn(gardenPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens")
                        .param("pageNo", String.valueOf(pageNumber)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardensPage", gardenPage))
                .andExpect(model().attributeDoesNotExist("pageNumbers"))
                .andExpect(view().name("browseGardensTemplate"));
    }

    @Test
    @WithMockUser
    public void TagAdded_ValidTag_RedirectToBrowseGardens() throws Exception {
        String tag = "tag2";
        List<String> updatedAllTags = new ArrayList<>(allTags);
        updatedAllTags.remove(tag);
        Mockito.when(tagService.getAllTagNames()).thenReturn(allTags);
        mockMvc.perform(MockMvcRequestBuilders.post("/browseGardens/addTag")
                .param("tag-input", tag)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browseGardens"))
                .andExpect(flash().attribute("allTags", updatedAllTags))
                .andExpect(flash().attribute("tags", List.of(tag)))
                .andExpect(flash().attribute("pageNo", String.valueOf(defaultPageNumber)));
    }

    @Test
    @WithMockUser
    public void TagAdded_InvalidTag_RedirectToBrowseGardens() throws Exception {
        String tag = "tag20";
        String errorMessage = "No tag matching " + tag;
        Mockito.when(tagService.getAllTagNames()).thenReturn(allTags);
        mockMvc.perform(MockMvcRequestBuilders.post("/browseGardens/addTag")
                        .param("tag-input", tag)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browseGardens"))
                .andExpect(flash().attribute("allTags", allTags))
                .andExpect(flash().attribute("tags", List.of()))
                .andExpect(flash().attribute("tag", tag))
                .andExpect(flash().attribute("tagValid", errorMessage))
                .andExpect(flash().attribute("pageNo", String.valueOf(defaultPageNumber)));

    }

    @Test
    @WithMockUser
    public void TagAdded_TagsExist_RedirectToBrowseGardens() throws Exception {
        String tag = "tag3";
        int pageNo = 2;
        List<String> existingTags = new ArrayList<>(allTags.subList(0, 4));
        List<String> updatedAllTags = new ArrayList<>(allTags.subList(4, allTags.size()));
        Mockito.when(tagService.getAllTagNames()).thenReturn(allTags);
        mockMvc.perform(MockMvcRequestBuilders.post("/browseGardens/addTag")
                        .param("tag-input", tag)
                        .param("tags", existingTags.get(0))
                        .param("tags", existingTags.get(1))
                        .param("tags", existingTags.get(2))
                        .param("pageNo", String.valueOf(pageNo))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browseGardens"))
                .andExpect(flash().attribute("allTags", updatedAllTags))
                .andExpect(flash().attribute("tags", existingTags))
                .andExpect(flash().attribute("pageNo", String.valueOf(pageNo)));
    }

}