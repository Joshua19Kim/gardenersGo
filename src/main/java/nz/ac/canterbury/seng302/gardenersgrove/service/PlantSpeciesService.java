package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantSpeciesRepository;
import org.h2.table.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for PlantSpecies. Links with repository to retrieve PlantSpecies
 */
@Service
public class PlantSpeciesService {

     private PlantSpeciesRepository plantSpeciesRepository;

    public PlantSpeciesService(PlantSpeciesRepository plantSpeciesRepository) {
        this.plantSpeciesRepository = plantSpeciesRepository;
    }

    /**
     * @return a list of all plant species in the repository
     */
    public List<PlantSpecies> getAllPlantSpecies() {
        return plantSpeciesRepository.findAll();
    }

    /**
     * @param plantSpecies a species of plant being added to the repository
     * @return plant species after saving it to the repository
     */
    public PlantSpecies addPlantSpecies(PlantSpecies plantSpecies) {
        return plantSpeciesRepository.save(plantSpecies);
    }

    /**
     * @param id of the plant species that you want retrieved
     * @return plant species matching the id passed
     */
    public Optional<PlantSpecies> getPlantSpecies(Long id) {return plantSpeciesRepository.findById(id);}

    /**
     * Retrieves a page type of plantspecies from the repository
     * used for pagination on the my collection page
     *
     * @param pageNo the page number that you want to see
     * @param pageSize the number of elements on a page of plant species
     * @return page type
     */
    public Page<PlantSpecies> getAllPlantSpeciesPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return plantSpeciesRepository.findAll(pageable);
    }

}
