package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.ScannedPlant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScannedPlantRepository extends CrudRepository<ScannedPlant, Long> {

    Optional<ScannedPlant> findById(long id);
    List<ScannedPlant> findAll();
}
