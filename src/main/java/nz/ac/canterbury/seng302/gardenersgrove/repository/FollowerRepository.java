package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Following;
import org.springframework.data.jpa.repository.Query;
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
    @Query(value = "SELECT follower.garden_id AS garden_id, gardener.gardener_id AS follower_id, gardener.first_name AS follower_first_name, gardener.last_name AS follower_last_name, COUNT(follower.gardener_id) AS follower_count FROM follower JOIN gardener  ON follower.gardener_id = gardener.gardener_id WHERE follower.garden_id = :id GROUP BY follower.garden_id, gardener.gardener_id, gardener.first_name, gardener.last_name", nativeQuery = true)
    List<Following> findFollowersByGardenId(Long id);

}
