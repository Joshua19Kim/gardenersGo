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
     *
     * @param id
     * @returns a list of all the accepted relationships of the user
     */
    @Query(value = "SELECT CASE WHEN gardener_id = ?1 THEN friend_id ELSE gardener_id END AS friendId FROM Relationships WHERE gardener_id=?1 or friend_id=?1 and status = 'accepted'", nativeQuery = true)
    List<Long> getGardenerFriends(Long id);

    @Query(value = "Select friend_id FROM Relationships WHERE gardener_id = ?1 and status = 'pending'", nativeQuery = true)
    List<Long> getGardenerPending(long id);

    @Query(value = "Select gardener_id FROM Relationships WHERE friend_id = ?1 and status = 'pending'", nativeQuery = true)
    List<Long> getGardenerIncoming(long id);

    @Query(value = "Select friend_id FROM Relationships WHERE gardener_id = ?1 and status = 'declined'", nativeQuery = true)
    List<Long> getGardenerDeclinedRequests(long id);

    @Query(value = "UPDATE Relationships SET status = ?1 WHERE gardener_id = ?2 and friend_id = ?3 ", nativeQuery = true)
    void updateRelationshipStatus(String action, long gardenerId, long friendId);




    Optional<Relationships> findRelationshipsByGardenerIdAndFriendId(long gardenerId, long friendId);


}
