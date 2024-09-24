package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Gardener repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
@Repository
public interface FollowerRepository extends CrudRepository<Follower, Long> {
    Optional<Follower> findById(long id);

    List<Follower> findAll();

    Optional<Follower> findByGardenerIdAndGardenId(long gardenerId, long gardenId);

    List<Follower> findAllByGardenerId(Long id);

    List<Follower> findAllByGardenId(Long id);

}
