package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
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
public interface RelationshipRepository extends CrudRepository<Relationships, Long> {
    Optional<Relationships> findById(long id);

    List<Relationships> findAll();

    /**
     * @param id find all friends of gardener given gardener id
     * @return a list of all the accepted relationships of the user
     */
    @Query(value = "SELECT gardener_id FROM relationships WHERE friend_id = ?1 AND status='accepted' " +
            "UNION " +
            "SELECT friend_id FROM relationships WHERE gardener_id=?1 AND status='accepted'", nativeQuery = true)
    List<Long> getGardenerFriends(Long id);

    @Query(value = "Select friend_id FROM relationships WHERE gardener_id = ?1 and status = 'pending'", nativeQuery = true)
    List<Long> getGardenerPending(long id);

    @Query(value = "Select gardener_id FROM relationships WHERE friend_id = ?1 and status = 'pending'", nativeQuery = true)
    List<Long> getGardenerIncoming(long id);

    @Query(value = "Select friend_id FROM relationships WHERE gardener_id = ?1 and status = 'declined'", nativeQuery = true)
    List<Long> getGardenerDeclinedRequests(long id);

    Optional<Relationships> findRelationshipsByGardenerIdAndFriendId(long gardenerId, long friendId);


}
