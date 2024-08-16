package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.ScannedPlant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A repository interface for accessing and managing scanned plant entities
 */
@Repository
public interface ScannedPlantRepository extends CrudRepository<ScannedPlant, Long> {

    /**
     * Retrieves a scanned plant by its ID
     *
     * @param id of scanned plant
     * @return an optional of a scanned plant
     */
    Optional<ScannedPlant> findById(long id);

    /**
     * Retrieves a list of all scanned plants stored in the database
     *
     * @return list of scanned plants from the database
     */
    List<ScannedPlant> findAll();
}
