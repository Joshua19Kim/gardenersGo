package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenVisit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardenVisitRepository extends CrudRepository<GardenVisit, Long> {
    @Query("SELECT gv.garden FROM GardenVisit gv WHERE gv.gardener.id = :gardenerId GROUP BY gv.garden ORDER BY MAX(gv.accessTime) DESC")
    List<Garden> findRecentGardensByGardenerId(@Param("gardenerId") Long gardenerId, Pageable pageable);
}
