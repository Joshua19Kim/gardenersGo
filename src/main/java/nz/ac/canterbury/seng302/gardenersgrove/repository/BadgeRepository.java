package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Used to interact with the badge database
 */
@Repository
public interface BadgeRepository extends CrudRepository<Badge, Long> {

    /**
     * Gets the badge with the specified id
     * @param id id of the badge
     * @return a badge
     */
    Optional<Badge> findById(long id);

    /**
     * Gets the badge with the id and gardener id
     * @param id the id of the badge
     * @param gardenerId the id of the gardener
     * @return the badge
     */
    Optional<Badge> findByIdAndGardenerId(long id, long gardenerId);

    /**
     * gets all the badges
     * @return all the badges
     */
    List<Badge> findAll();

    /**
     * Retrieves a list of badges by their owner's gardener ID.
     *
     * @param gardenerId The identifier of the garden's owner.
     * @return A list of all badges with the specified owner stored in the repository.
     */
    List<Badge> findByGardenerId(Long gardenerId);

    /**
     * Finds the badge by its name
     * @param name the name of the badge
     * @return the badge with the given name
     */
    Optional<Badge> findByNameAndGardenerId(String name, Long gardenerId);

    /**
     * Finds the badges with the specified type
     * @param badgeType the badge type
     * @return the badges with the given type
     */
    List<Badge> findByBadgeType(BadgeType badgeType);

    /**
     * Find the most recent badges that the certain gardener earned
     * @param gardenerId The identifier of the garden's owner.
     * @param pageable Pagination information to limit and order the results.
     * @return A list of Garden, representing the most recently visited gardens by the specified gardener,
     *         ordered by the most recent visit time in descending order.
     */
    @Query("SELECT badge.id FROM Badge badge WHERE badge.gardener.id = :gardenerId GROUP BY badge.id ORDER BY MAX(badge.dateEarned) DESC")
    List<Badge> findRecentByGardenerId(@Param("gardenerId") Long gardenerId, Pageable pageable);


}
