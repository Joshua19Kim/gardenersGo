package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SpeciesBadgeStepDefs {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GardenerFormService gardenerFormService;
    @Autowired
    private IdentifiedPlantRepository identifiedPlantRepository;

    @Autowired
    private IdentifiedPlantService identifiedPlantService;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private BadgeService badgeService;
    private ResultActions resultActions;
    private Gardener gardener;


    @Before("@U7009B")
    public void setUp() {badgeRepository.deleteAll();}
    @After("@U7009B")
    public void tearDown() {
        identifiedPlantRepository.deleteAll();
        badgeRepository.deleteAll();
    }

    @Given("I have collected {int} species,")
    public void i_have_collected_species(Integer speciesCount) {
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();

        for(int i = 0; i <speciesCount; i++) {
            IdentifiedPlant plant = new IdentifiedPlant("Plant " +i, gardener);
            plant.setSpeciesScientificNameWithoutAuthor("Species" + i);
            identifiedPlantService.saveIdentifiedPlantDetails(plant);
        }
    }
    @When("I have collected another species")
    public void i_have_collected_another_species() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("plantImage", "image.jpg", "image/jpeg", new byte[0]);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/myCollection")
                        .file(imageFile)
                        .param("plantName", "New Plant" )
                        .param("description", "Awesome description")
                        .param("scientificName",  "Sick species")
                        .param("uploadedDate", String.valueOf(LocalDate.parse("12/02/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                        .param("isDateInvalid", "false")
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCollection"));
    }
    @Then("I will be shown the species badge with name {string}")
    public void i_will_be_shown_the_species_badge_with_name(String speciesName) throws Exception {
        Optional<Badge> badge = badgeService.getMyBadgeByName(speciesName, gardener.getId());
        Assertions.assertTrue(badge.isPresent());
        Assertions.assertEquals(speciesName, badge.get().getName());
        resultActions.andExpect(flash().attributeExists("speciesBadge"));

    }

}
