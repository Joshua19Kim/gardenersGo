package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing IdentifiedPlant entities
 */
@Repository
public interface IdentifiedPlantRepository extends CrudRepository<IdentifiedPlant, Long> {
}