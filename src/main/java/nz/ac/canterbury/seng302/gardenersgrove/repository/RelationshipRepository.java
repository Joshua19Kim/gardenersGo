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
    @Query("SELECT CASE WHEN gardenerId = ? THEN friend_id ELSE gardener_id END AS friendId FROM Relationships WHERE gardener_id=? or friend_id=? and status = 'accepted'")
    List<Relationships> getGardenerFriends(Long id);

    @Query()
    List<Relationships> getGardenerPending(long id);



    Optional<Relationships> findRelationshipsByGardenerIdAndFriendId(long gardenerId, long friendId);


}
