package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service class for managing Plant entities
 */
@Service
public class TagService {

    private final TagRepository tagRepository;

    private final WriteEmail writeEmail;
    private final ExecutorService executorService;

    /**
     * Constructs a TagService with the TagRepository
     *
     * @param tagRepository is the repo for managing Tags
     */
    public TagService(TagRepository tagRepository, WriteEmail writeEmail) {

        this.tagRepository = tagRepository;
        this.writeEmail = writeEmail;
        this.executorService = Executors.newCachedThreadPool();
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

  /**
   * Get all the tags within the certain garden
   * @param id garden's id
   * @return the list of the name of tags in the certain garden
   */
  public List<String> getTags(Long id) {
    return tagRepository.getTagsByGardenId(id);
  }

  /**
   * Find a tag with its name and garden
   * @param name tag name that needs to be searched
   * @param garden garden that has the tag
   * @return the tag that matches the name and garden
   */
  public Optional<Tag> findTagByNameAndGarden(String name, Garden garden) {
    return tagRepository.findByNameAndGarden(name, garden);
  }

  /**
   * Delete a specific tag with tag's name and garden Id
   * @param tagName tag name that needs to be deleted
   * @param gardenId Garden Id that has the tag that needs to be deleted
   */
  public void deleteTagByGardenAndName( String tagName, Long gardenId) {
    tagRepository.deleteByGardenIdAndName(tagName, gardenId);
  }


  /**
   * Gets a list of all unique tags in the system that do not exist in the specified garden
   *
   * @param id the id of the garden
   * @return a set of all unique tags in the system that do not exist in the specified garden
   */
  public Set<String> getUniqueTagNames(Long id) {
    Set<String> uniqueTagNames = new HashSet<>();
    List<Tag> tags = getAllTags();
    List<String> tagNamesInGarden = getTags(id);
    for(Tag tag : tags) {
      if(!tagNamesInGarden.contains(tag.getName())) {
        uniqueTagNames.add(tag.getName());
      }
    }
    return uniqueTagNames;
  }

    /**
     * Check the number of bad words that the user tried to use,
     * if it is 5th time, send warning email and show a relevant message,
     * if it is 6th time, un-log the user from the system and block the account for 7 days and send a relevant email.
     * otherwise, show the bad word message to let the user know.
     *
     * @param gardener the gardener of the current user
     * @return bad word warning message
     */
    public String addBadWordCount(Gardener gardener) {

        gardener.setBadWordCount(gardener.getBadWordCount() + 1);

        if (gardener.getBadWordCount() == 5) {
            //This runAsync is added because error message was returning after sending an email which was quite slow considering UX.
            //This will enable system sending email in the background asynchronously and return the message asap.
            CompletableFuture.runAsync(() -> writeEmail.sendTagWarningEmail(gardener), executorService);

            return "You have added an inappropriate tag for the fifth time. If you add one more, your account will be blocked for one week.";
        } else if (gardener.getBadWordCount() == 6) {
            gardener.banGardener();
            CompletableFuture.runAsync(() -> writeEmail.sendBanUserEmail(gardener), executorService);
            return "BANNED";
        }
        return "Submitted tag fails moderation requirements";
    }



  /**
   * Gets all the tag names in the database
   * @return all the tag names in the database
   */
  public List<String> getAllTagNames() {
    return tagRepository.getAllTagNames();
  }


}
