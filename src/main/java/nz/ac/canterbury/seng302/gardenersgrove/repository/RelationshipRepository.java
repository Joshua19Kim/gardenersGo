package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Gardener repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
@Repository
public interface RelationshipRepository extends CrudRepository<Relationships, Long> {
    Optional<Relationships> findById(long id);
    List<Relationships> findAll();

    boolean existsByGardenerIdAndFriendId(long gardenerId, long friendId);

    boolean existsByFriendIdAndGardenerId(long friendId, long gardernerId);


}
