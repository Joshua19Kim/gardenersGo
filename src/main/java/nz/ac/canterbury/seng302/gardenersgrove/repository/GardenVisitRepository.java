package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenVisit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GardenVisitRepository extends CrudRepository<GardenVisit, Long> {

    /**
     * Find the most recent gardens that the certain gardener visited
     * @param gardenerId The identifier of the garden's owner.
     * @param pageable Pagination information to limit and order the results.
     * @return A list of Garden, representing the most recently visited gardens by the specified gardener,
     *         ordered by the most recent visit time in descending order.
     */
    @Query("SELECT gv.garden FROM GardenVisit gv WHERE gv.gardener.id = :gardenerId GROUP BY gv.garden ORDER BY MAX(gv.accessTime) DESC")
    List<Garden> findRecentGardensByGardenerId(@Param("gardenerId") Long gardenerId, Pageable pageable);

    /**
     * Delete the garden visit records of the specific gardner with ID
     * @param gardenerId The identifier of the garden's owner.
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM garden_visit WHERE gardener_id = :gardenerId", nativeQuery = true)
    void deleteAllGardenVisitsByGardenerId(@Param("gardenerId") Long gardenerId);

}
