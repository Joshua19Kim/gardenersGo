package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.be.I;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AddPlantToCollectionStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IdentifiedPlantService identifiedPlantService;

    @Autowired
    private GardenerFormService gardenerFormService;

    @Autowired
    private IdentifiedPlantRepository identifiedPlantRepository;

    private IdentifiedPlant identifiedPlant;

    private ResultActions resultActions;

    private Gardener gardener;

    private MockMultipartFile imageFile;

    @After("@U7003")
    public void tearDown() {
        identifiedPlantRepository.deleteAll();
    }

    @Given("I am manually adding a plant to my collection")
    public void i_am_manually_adding_a_plant_to_my_collection() throws Exception {
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        identifiedPlant = new IdentifiedPlant();

        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection"))
                .andExpect(view().name("myCollectionTemplate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorOccurred", false))
                .andExpect(model().attribute("showModal", false));
    }
    @When("I enter the plant name {string}")
    public void i_enter_the_plant_name(String plantName) {
        identifiedPlant.setName(plantName);
    }
    @When("I enter the plant species {string}")
    public void i_enter_the_plant_species(String plantSpecies) {
        identifiedPlant.setSpeciesScientificNameWithoutAuthor(plantSpecies);
    }
    @When("I enter the plant description {string}")
    public void i_enter_the_plant_description(String description) {
        identifiedPlant.setDescription(description);
    }
    @When("I enter the uploaded date {string}")
    public void i_enter_the_uploaded_date(String date) {
        identifiedPlant.setDateUploaded(date);
    }

    @When("I enter valid location")
    public void i_enter_valid_location() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I enter invalid location")
    public void i_enter_invalid_location() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }



    @When("I upload no image")
    public void i_upload_no_image() {
        identifiedPlant.setUploadedImage("/images/placeholder.jpg");
        imageFile = new MockMultipartFile("plantImage", "image.jpg", "image/jpeg", new byte[0]);
    }

    @When("I save the plant to my collection")
    public void i_save_the_plant_to_my_collection() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/myCollection")
                        .file(imageFile)
                        .param("plantName", identifiedPlant.getName())
                        .param("description", identifiedPlant.getDescription())
                        .param("scientificName",  identifiedPlant.getSpeciesScientificNameWithoutAuthor())
                        .param("uploadedDate", String.valueOf(LocalDate.parse(identifiedPlant.getDateUploaded(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                        .param("isDateInvalid", "false")
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCollection"));
    }
    @Then("the plant is added to my collection")
    public void the_plant_is_added_to_my_collection() {
        List<IdentifiedPlant> identifiedPlants = identifiedPlantService.getGardenerPlantsBySpeciesPaginated(0,
                10, gardener.getId(), identifiedPlant.getSpeciesScientificNameWithoutAuthor()).getContent();
        IdentifiedPlant actualIdentifiedPlant = identifiedPlants.get(0);
        Assertions.assertEquals(identifiedPlant.getName(), actualIdentifiedPlant.getName());
        Assertions.assertEquals(identifiedPlant.getDateUploaded(), actualIdentifiedPlant.getDateUploaded());
        Assertions.assertEquals(identifiedPlant.getDescription(), actualIdentifiedPlant.getDescription());
        Assertions.assertEquals(identifiedPlant.getSpeciesScientificNameWithoutAuthor(), actualIdentifiedPlant.getSpeciesScientificNameWithoutAuthor());
        Assertions.assertEquals(identifiedPlant.getUploadedImage(), actualIdentifiedPlant.getUploadedImage());

    }

    @Then("the plant is not added to my collection")
    public void the_plant_is_not_added_to_my_collection() {
        List<IdentifiedPlant> identifiedPlants = identifiedPlantService.getGardenerPlantsBySpeciesPaginated(0,
                10, gardener.getId(), identifiedPlant.getSpeciesScientificNameWithoutAuthor()).getContent();
        Assertions.assertTrue(identifiedPlants.isEmpty());
    }
    @Then("I get the plant name error message {string}")
    public void i_get_the_plant_name_error_message(String errorMessage) throws Exception {
        resultActions.andExpect(flash().attribute("plantNameError", errorMessage));
    }
    @Then("I get the plant species error message {string}")
    public void i_get_the_plant_species_error_message(String errorMessage) throws Exception {
        resultActions.andExpect(flash().attribute("scientificNameError", errorMessage));
    }

    @Then("I get the location error message {string}")
    public void i_get_the_location_error_message(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
