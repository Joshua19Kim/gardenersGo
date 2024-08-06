package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenVisit;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenVisitRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing entries of recent garden visits.
 */
@Service
public class GardenVisitService {

    private final GardenVisitRepository gardenVisitRepository;

    /**
     * Constructs a RecentGardensService with the provided RecentGardensRepository.
     *
     * @param gardenVisitRepository The RecentGardensRepository used for accessing recently visited gardens data.
     */
    public GardenVisitService(GardenVisitRepository gardenVisitRepository) {
        this.gardenVisitRepository = gardenVisitRepository;
    }

    /**
     * Adds a new garden visit to the repository.
     *
     * @param gardenVisit The garden visit to be added.
     * @return The added garden visit.
     */
    public GardenVisit addGardenVisit(GardenVisit gardenVisit) {
        return gardenVisitRepository.save(gardenVisit);
    }

    /**
     * Retrieves the three most recently accessed gardens for a given gardener.
     *
     * @param gardenerId The id of the gardener to retrieve the most recently accessed gardens.
     * @return A list containing the three most recently accessed gardens.
     */
    public List<Garden> findRecentGardensByGardenerId(Long gardenerId) {
        return gardenVisitRepository.findRecentGardensByGardenerId(gardenerId, PageRequest.of(0, 3));
    }
}
