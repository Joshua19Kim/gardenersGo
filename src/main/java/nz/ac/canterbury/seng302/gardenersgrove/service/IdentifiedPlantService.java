package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpeciesImpl;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import java.util.List;

@Service
public class IdentifiedPlantService {
    private final IdentifiedPlantRepository identifiedPlantRepository;
    public IdentifiedPlantService(IdentifiedPlantRepository identifiedPlantRepository) {
        this.identifiedPlantRepository = identifiedPlantRepository;
    }
    /**
     * Saves the details of an identified plant entity in the database.
     *
     * @param identifiedPlant The entity containing the details to be saved.
     * @return The saved IdentifiedPlant entity
     */
    public IdentifiedPlant saveIdentifiedPlantDetails(IdentifiedPlant identifiedPlant) {
        return identifiedPlantRepository.save(identifiedPlant);
    }

    /**
     * Gets the IdentifiedPlants by species name that are owned by the gardener in paginated form
     * @param pageNo the page number
     * @param pageSize the size of the page
     * @param gardenerId the id of the gardener
     * @param speciesName the name of the plant species
     * @return the page of IdentifiedPlants matching the species name
     */
    public Page<IdentifiedPlant> getGardenerPlantsBySpeciesPaginated(int pageNo, int pageSize, Long gardenerId, String speciesName) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return identifiedPlantRepository.getPlantByGardenerIdAndSpecies(gardenerId, speciesName, pageable);
    }

    /**
     * Gets the IdentifiedPlants that are owned by the gardener in list form
     * @param gardenerId the id of the gardener
     * @return all the IdentifiedPlants the gardener owns/ has scanned
     */
    public List<IdentifiedPlant> getGardenerPlants(Long gardenerId) {
        return identifiedPlantRepository.getPlantByGardenerId(gardenerId);
    }

    /**
     * Gets the IdentifiedPlant species that are owned by the gardener in paginated form
     * @param pageNo the page number
     * @param pageSize the size of the page
     * @param gardenerId the id of the gardener
     * @return the page of IdentifiedPlant species
     */
    public Page<IdentifiedPlantSpeciesImpl> getGardenerPlantSpeciesPaginated(int pageNo, int pageSize, Long gardenerId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return identifiedPlantRepository.getSpeciesByGardenerId(gardenerId, pageable);
    }

    /**
     * Gets a plant by its id
     * @param id the id of the plant
     * @return a plant if found
     */
    public IdentifiedPlant getCollectionPlantById(long id) {
        return identifiedPlantRepository.findById(id);
    }

    /**
     * Gets all plants for a gardener
     * @param id gardener's id
     * @return returns the count for a gardener
     */
    public Integer getCollectionPlantCount(long id) { return identifiedPlantRepository.getIdentifiedPlantByGardenerId(id).size();}

    /**
     * Gets a count of all the species in the database
     * @param id the id of the gardener
     * @return a count of all the species
     */
    public int getSpeciesCount(long id) { return identifiedPlantRepository.getSpeciesCountByGardenerId(id);}

    /**
     * This validates all the details of a plant that is manually added to the collection
     * @param plantName the plant name
     * @param scientificName the scientific name (species)
     * @param description the description
     * @param isDateInvalid indicates whether the date is valid or not from HTML
     * @param redirectAttributes used to add flash attributes when the page is redirected
     * @return a boolean value indicating whether the plant is added or not
     */
    public boolean validateManuallyAddedPlantDetails(String plantName, String scientificName, String description, boolean isDateInvalid, RedirectAttributes redirectAttributes) {
        String validatedPlantName = ValidityChecker.validatePlantName(plantName);
        String validatedScientificName = ValidityChecker.validateScientificPlantName(scientificName);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        boolean isValid = true;

        if (isDateInvalid) {
            String dateError = "Date is not in valid format, DD/MM/YYYY";
            redirectAttributes.addFlashAttribute("dateError", dateError);
            isValid = false;
        }

        if (!Objects.equals(plantName, validatedPlantName)) {
            redirectAttributes.addFlashAttribute("plantNameError", validatedPlantName);
            isValid = false;
        }
        if (!Objects.equals(scientificName, validatedScientificName)) {
            redirectAttributes.addFlashAttribute("scientificNameError", validatedScientificName);
            isValid = false;
        }
        if (!Objects.equals(description, validatedPlantDescription)) {
            redirectAttributes.addFlashAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Adds all the optional details to a manually added plant
     * @param identifiedPlant the identified plant
     * @param description description
     * @param scientificName species
     * @param uploadedDate uploaded date
     * @return the identified plant
     */
    public IdentifiedPlant createManuallyAddedPlant(IdentifiedPlant identifiedPlant, String description, String scientificName, LocalDate uploadedDate) {
        if (description != null && !description.trim().isEmpty()) {
            identifiedPlant.setDescription(description);
        }
        if (scientificName != null && !scientificName.trim().isEmpty()) {
            identifiedPlant.setSpeciesScientificNameWithoutAuthor(scientificName);
        } else {
            identifiedPlant.setSpeciesScientificNameWithoutAuthor("No Species");
        }
        if (uploadedDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            identifiedPlant.setDateUploaded(uploadedDate.format(formatter));
        }
        return identifiedPlant;
    }

}
