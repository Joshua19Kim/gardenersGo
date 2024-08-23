package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}