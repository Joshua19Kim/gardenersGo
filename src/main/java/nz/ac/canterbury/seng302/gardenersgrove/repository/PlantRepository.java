package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Plant entities.
 */
@Repository
public interface PlantRepository extends CrudRepository<Plant, Long> {

    /**
     * Retrieves a plant by its unique identifier.
     *
     * @param id The unique identifier of the plant.
     * @return An Optional containing the plant if found, otherwise empty.
     */
    Optional<Plant> findById(long id);

    /**
     * Retrieves all plants stored in the repository.
     *
     * @return A list of all plants stored in the repository.
     */
    List<Plant> findAll();

    List<Plant> findTop3ByGardenGardenerIdOrderByIdDesc(Long gardenerId);
}

