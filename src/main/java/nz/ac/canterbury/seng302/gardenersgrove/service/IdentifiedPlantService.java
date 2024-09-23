package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpeciesImpl;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

}
