package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Tag repository accessor using Spring's @link{CrudRepository}. These (basic) methods are provided
 * for us without the need to write our own implementations
 */
@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

  Optional<Tag> findById(long id);

  List<Tag> findAll();

  /**
   * @param gardenId find all tags for a garden given garden id
   * @return a list of all tags for the garden
   */

  @Query(value = "SELECT tag_name FROM tag WHERE garden = ?1 ", nativeQuery = true)
  List<String> getTagsByGardenId(long gardenId);

  /**
   * @param name find a tag with a tag name matching name
   * @param garden find tag with a garden matching garden
   * @return a tag or empty if no matching tag
   */
  Optional<Tag> findByNameAndGarden(String name, Garden garden);

  /**
   * Gets all the tag names in the database
   * @return all the tag names in the database
   */
  @Query(value = "select DISTINCT tag_name from tag", nativeQuery = true)
  List<String> getAllTagNames();

  /**
   * Delete a specific tag with tag's name and garden Id, which is in the database
   * @param tagName tag's name that needs to be deleted
   * @param gardenId Garden's Id that has the tag that needs to be deleted
   */
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM tag WHERE tag_name = :tagName AND garden = :gardenId", nativeQuery = true)
  void deleteByGardenIdAndName(@Param("tagName") String tagName, @Param("gardenId") Long gardenId);

}
