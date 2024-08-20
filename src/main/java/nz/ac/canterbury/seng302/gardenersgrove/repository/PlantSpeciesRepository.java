package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing PlantSpecies entities
 */
@Repository
public interface PlantSpeciesRepository extends JpaRepository<PlantSpecies, Long> {

    /**
     * Retrieves a plant species by its ID
     *
     * @param id of plant species
     * @return an optional of a plant species
     */
    Optional<PlantSpecies> findById(long id);

    /**
     * @return a list of all plant species in the database
     */
    List<PlantSpecies> findAll();

    /**
     * returns page of plant species
     * @param pageable a pageable object used for paginationb
     * @return a page of plant species
     */
    Page<PlantSpecies> findAll(Pageable pageable);

    /**
     * Find the plant species by gardener id with pagination
     * @param id the id of the gardener
     * @param pageable object representing pagination
     * @return the plant species that the gardener has
     */
    Page<PlantSpecies> findPlantSpeciesByGardenerId(long id, Pageable pageable);


}
