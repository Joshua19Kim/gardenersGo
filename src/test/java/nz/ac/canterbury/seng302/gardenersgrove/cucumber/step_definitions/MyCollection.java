package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantSpeciesRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantSpeciesService;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MyCollection {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlantSpeciesRepository plantSpeciesRepository;

    @Autowired
    private PlantSpeciesService plantSpeciesService;



    @When("I am on the my collections page")
    public void i_am_on_the_my_collections_page() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/myCollection"))
                .andExpect(view().name("myCollectionTemplate"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("The plants in my collection are shown")
    public void the_plants_in_my_collection_are_shown() throws Exception {
        Gardener gardener = new Gardener("John", "Doe", LocalDate.of(2000, 1, 1), "a@gmail.com", "Password1!");
        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection")
                        .with(SecurityMockMvcRequestPostProcessors.user(gardener.getEmail())))
                .andExpect(status().isOk())
                .andExpect(view().name("myCollectionTemplate"))
                .andExpect(model().attributeExists("plantSpeciesList"))
                .andExpect(model().attribute("plantSpeciesList", Matchers.not(Matchers.empty())));
    }

}
