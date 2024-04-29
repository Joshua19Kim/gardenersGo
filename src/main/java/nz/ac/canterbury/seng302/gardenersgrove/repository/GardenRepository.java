package nz.ac.canterbury.seng302.gardenersgrove.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing Garden entities.
 */
@Repository
public interface GardenRepository extends CrudRepository<Garden, Long> {
    /**
     * Retrieves a garden by its unique identifier.
     *
     * @param id The unique identifier of the garden.
     * @return An Optional containing the garden if found, otherwise empty.
     */
    Optional<Garden> findById(long id);

    /**
     * Retrieves all gardens stored in the repository.
     *
     * @return A list of all gardens stored in the repository.
     */
    List<Garden> findAll();

    /**
     * Retrieves a list of gardens by their owner's gardener ID.
     * Gardens with a null gardener ID (those from the last sprint) are displayed for all users.
     *
     * @param gardenerId The identifier of the garden's owner.
     * @return A list of all gardens with the specified owner stored in the repository.
     */
    @Query(value = "SELECT * FROM Garden g WHERE g.gardener_id = :gardenerId OR g.gardener_id IS NULL", nativeQuery = true)
    List<Garden> findByGardenerId(Long gardenerId);
}
