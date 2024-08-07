package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Garden entities.
 */
@Service
public class GardenService {

    private GardenRepository gardenRepository;

    /**
     * Constructs a GardenService with the provided GardenRepository.
     *
     * @param gardenRepository The repository used for accessing garden data.
     */
    public GardenService(GardenRepository gardenRepository) {
        this.gardenRepository = gardenRepository;
    }

    /**
     * Retrieves a list of all gardens stored in the repository.
     *
     * @return A list of all gardens stored in the repository.
     */
    public List<Garden> getGardenResults() {
        return gardenRepository.findAll();
    }

    /**
     * Adds a new garden to the repository.
     *
     * @param garden The garden to be added.
     * @return The added garden.
     */
    public Garden addGarden(Garden garden) {
        return gardenRepository.save(garden);
    }

    /**
     * Gets the garden from the database by its id
     *
     * @param id the id of the garden
     * @return the garden associated with the id
     */
    public Optional<Garden> getGarden(long id) {
        return gardenRepository.findById(id);
    }

    /**
     * Retrieves a list of all gardens from the repository with the specified gardener ID.
     *
     * @param gardenerId The identifier of the garden's owner.
     * @return A list of all gardens with the specified owner stored in the repository.
     */
    public List<Garden> getGardensByGardenerId(Long gardenerId) {
        return gardenRepository.findByGardenerId(gardenerId);
    }

    /**
     * Updates the last notified date of a garden by its id
     *
     * @param gardenId The identifier of the garden
     * @param date     The date to set as the last notified date
     */
    public void updateLastNotifiedbyId(Long gardenId, LocalDate date) {
        gardenRepository.updateLastNotifiedbyId(gardenId, date);

    }

    /**
     * Gets a page of the specified size and number
     * @param pageNo the page number
     * @param pageSize the page size
     * @return a page of garden objects
     */
    public Page<Garden> getGardensPaginated(int pageNo, int pageSize)  {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("creation_date").descending());
        return gardenRepository.findAllPublicGardens(pageable);
    }

    /**
     * Gets a page of the specified size and number containing gardens matching the search term
     * @param pageNo the page number
     * @param pageSize the page size
     * @param searchTerm the term to search garden and plant names for
     * @param tagCount the number of tags in the search query
     * @return a page of garden objects
     */
    public Page<Garden> getSearchResultsPaginated(int pageNo, int pageSize, String searchTerm, List<String> tags, Long tagCount)  {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("creation_date").descending());
        return gardenRepository.findGardensBySearchTerm(pageable, searchTerm, tags, tagCount);
    }
}

