package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
  @Query(value = "SELECT tag_name FROM Tag WHERE garden.id = ? ")
  List<String> getTagsByGardenId(long gardenId);
}
