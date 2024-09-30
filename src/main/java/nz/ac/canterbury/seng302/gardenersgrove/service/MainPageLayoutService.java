package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.MainPageLayout;
import nz.ac.canterbury.seng302.gardenersgrove.repository.MainPageLayoutRepository;
import org.springframework.stereotype.Service;

@Service
public class MainPageLayoutService {

  private final MainPageLayoutRepository mainPageLayoutRepository;

    /**
     * Constructs a MainPageLayoutService with the provided mainPageLayoutRepository.
     *
     * @param mainPageLayoutRepository The repository used for accessing main page layout data.
     */
    public MainPageLayoutService(MainPageLayoutRepository mainPageLayoutRepository) {
        this.mainPageLayoutRepository = mainPageLayoutRepository;
    }

    /**
     * Adds a new main page layout to the repository. Used when new gardeners are registered.
     *
     * @param mainPageLayout The main page layout to be added.
     * @return The added main page layout.
     */
    public MainPageLayout addMainPageLayout(MainPageLayout mainPageLayout) {
        return mainPageLayoutRepository.save(mainPageLayout);
    }

    /**
     * Gets a main page layout by its relevant gardener ID.
     *
     * @param gardenerId The identifier of the main page layout's owner.
     * @return The main page layout with the specified owner stored in the repository.
     */
    public MainPageLayout getLayoutByGardenerId(Long gardenerId) {
        return mainPageLayoutRepository.findByGardenerId(gardenerId);
    }

}
