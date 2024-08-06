package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Plant entities.
 */
@Service
public class PlantService {
    private PlantRepository plantRepository;

    /**
     * Constructs a PlantService with a PlantRepository.
     *
     * @param plantRepository The repository for managing Plant entities.
     */
    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    /**
     * Retrieves a list of all plants.
     *
     *
     * @return A list of all plants.
     */
    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    /**
     * Adds a new plant to the repository.
     *
     * @param plant The plant to be added.
     * @return The added plant.
     */
    public Plant addPlant(Plant plant) {
        return plantRepository.save(plant);
    }

    /**
     * Gets the plant by the id
     * @param id the id of the plant
     * @return the plant
     */
    public Optional<Plant> getPlant(Long id) {return plantRepository.findById(id);}

    public List<Plant> findNewestPlantsByGardenerId(Long id) { return plantRepository.findTop3ByGardenGardenerIdOrderByIdDesc(id);}
}
