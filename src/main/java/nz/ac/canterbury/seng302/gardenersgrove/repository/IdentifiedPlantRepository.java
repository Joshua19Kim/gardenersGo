package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpecies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing IdentifiedPlant entities
 */
@Repository
public interface IdentifiedPlantRepository extends CrudRepository<IdentifiedPlant, Long> {
    /**
     * Retrieves a list of all identified plants stored in the database
     *
     * @return list of identified plants from the database
     */
    Page<IdentifiedPlant> findAll(Pageable pageable);

    /**
     * Find the identified plants by gardener id with pagination
     * @param id the id of the gardener
     * @param pageable object representing pagination
     * @return the identified plants that the gardener has
     */
    Page<IdentifiedPlant> findPlantSpeciesByGardenerId(long id, Pageable pageable);

    /**
     * Custom query to retrieve species by gardener id, with pagination
     *
     * @param id the id of the gardener
     * @param pageable object representing pagination
     * @return a page of species with count, species name, and image URL
     */
    @Query("SELECT new nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpecies(p.speciesScientificNameWithoutAuthor, p.imageUrl, COUNT(p)) " +
            "FROM IdentifiedPlant p WHERE p.gardener = :id " +
            "GROUP BY p.speciesScientificNameWithoutAuthor, p.imageUrl")
    Page<IdentifiedPlantSpecies> getSpeciesByGardenerId(long id, Pageable pageable);
}