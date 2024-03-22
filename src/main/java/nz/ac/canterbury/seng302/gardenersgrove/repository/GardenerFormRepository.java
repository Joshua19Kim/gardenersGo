package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Gardener repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
@Repository
public interface GardenerFormRepository extends CrudRepository<Gardener, Long> {
    Optional<Gardener> findById(long id);
    List<Gardener> findAll();
    Optional<Gardener> findByEmail(String email);
    Optional<Gardener> findByEmailAndPassword(String email, int password);

}
