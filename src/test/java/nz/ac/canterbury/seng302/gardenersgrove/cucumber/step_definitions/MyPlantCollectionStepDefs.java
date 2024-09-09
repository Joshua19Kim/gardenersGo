package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantSpeciesRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantSpeciesService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MyPlantCollectionStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlantSpeciesService plantSpeciesService;

    @Autowired
    private GardenerFormService gardenerFormService;

    @Autowired
    private PlantSpeciesRepository plantSpeciesRepository;

    private Gardener gardener;

    private List<PlantSpecies> expectedPlantSpeciesList;

    private String genericImage;

    private ResultActions resultActions;

    @Before("@U7002")
    public void setUp() {
        expectedPlantSpeciesList = new ArrayList<>();
        genericImage = "/images/placeholder.jpg";
    }

    @After("@U7002")
    public void tearDown() {
        plantSpeciesRepository.deleteAll();
    }


    @Given("I have added the plant species with name {string} and count {string}")
    public void i_have_added_the_plant_species_with_name_and_count(String name, String count) {
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        PlantSpecies plantSpecies = new PlantSpecies(name, Integer.parseInt(count), genericImage, gardener);
        plantSpeciesService.addPlantSpecies(plantSpecies);
        expectedPlantSpeciesList.add(plantSpecies);

    }
    @When("I go to my collection page")
    public void i_go_to_my_collection_page() throws Exception{
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/myCollection"))
                .andExpect(view().name("myCollectionTemplate"))
                .andExpect(status().isOk());
    }
    @Then("I should be able to see all the identified plant species")
    public void i_should_be_able_to_see_all_the_identified_plant_species() throws Exception{
        Page<PlantSpecies> actualPlantSpeciesPage = (Page<PlantSpecies>) resultActions.andReturn().getModelAndView().getModel().get("plantSpeciesList");
        List<PlantSpecies> plantSpeciesList = actualPlantSpeciesPage.getContent();
        Assertions.assertEquals(expectedPlantSpeciesList.size(), plantSpeciesList.size());
        for(int i = 0; i < expectedPlantSpeciesList.size(); i++ ) {
            PlantSpecies expectedPlantSpecies = expectedPlantSpeciesList.get(i);
            PlantSpecies actualPlantSpecies = plantSpeciesList.get(i);
            Assertions.assertEquals(expectedPlantSpecies.getName(), actualPlantSpecies.getName());
            Assertions.assertEquals(expectedPlantSpecies.getCount(), actualPlantSpecies.getCount());
        }
    }
}
