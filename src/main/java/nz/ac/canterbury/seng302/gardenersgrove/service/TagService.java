package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Service class for managing Plant entities */
@Service
public class TagService {

  private TagRepository tagRepository;

  /**
   * Constructs a TagService with the TagRepository
   *
   * @param tagRepository is the repo for managing Tags
   */
  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  /**
   * @return a list of all tags
   */
  public List<Tag> getAllTags() {
    return tagRepository.findAll();
  }

  /**
   * @param tag the tag (consisting of string and garden id)
   * @return the added tag
   */
  public Tag addTag(Tag tag) {
    return tagRepository.save(tag);
  }

  /**
   * @param id of the tag
   * @return the tag that matches id
   */
  public Optional<Tag> getTag(Long id) {
    return tagRepository.findById(id);
  }

  public List<String> getTags(Long id) {
    return tagRepository.getTagsByGardenId(id);
  }

  public Optional<Tag> findTagByNameAndGarden(String name, Garden garden) {
    return tagRepository.findByNameAndGarden(name, garden);
  }
}