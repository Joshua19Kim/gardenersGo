package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RelationshipRepository;
import org.aspectj.asm.internal.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for FormResults, defined by the @link{Service} annotation.
 * This class links automatically with @link{FormRepository}, see the @link{Autowired} annotation below
 */
@Service
public class RelationshipService {
    private final RelationshipRepository relationshipRepository;
    private final GardenerFormService gardenerFormService;
    private final GardenerFormRepository gardenerFormRepository;
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);


    @Autowired
    public RelationshipService(RelationshipRepository relationshipRepository, GardenerFormService gardenerFormService, GardenerFormRepository gardenerFormRepository) {
        this.relationshipRepository = relationshipRepository;
        this.gardenerFormService = gardenerFormService;
        this.gardenerFormRepository = gardenerFormRepository;
    }

    /**
     * Gets all Relationships from persistence
     * @return all Relationships currently saved in persistence
     */
    public List<Relationships> getRelationships() {
        return relationshipRepository.findAll();
    }

    /**
     * Adds a Relationship to persistence
     * @param relationships object to persist
     * @return the saved Relationship object
     */
    public Relationships addRelationship(Relationships relationships) {
        return relationshipRepository.save(relationships);
    }

    public List<Gardener> getCurrentUserRelationships (Long id) {
        List<Gardener> currentRelationships = new ArrayList<>();
        List<Relationships> allRelationships = relationshipRepository.findAll();
        for (Relationships relationship : allRelationships) {
            if (relationship.getGardenerId() == id) {
                currentRelationships.add(gardenerFormService.findById(relationship.getFriendId()).get());
            } else if (relationship.getFriendId() == id) {
                currentRelationships.add(gardenerFormService.findById(relationship.getGardenerId()).get());
            }
        }
        return currentRelationships;
    }

    public Optional<Relationships> findById(long id) {
        return relationshipRepository.findById(id);
    }

    public boolean relationshipExists(long gardenerId, long friendId) {
        return relationshipRepository.existsByGardenerIdAndFriendId(gardenerId, friendId) || relationshipRepository.existsByFriendIdAndGardenerId(friendId, gardenerId);
    }


//    public String getButtonLabel(long gardenerId, long friendId) {
//        String buttonLabel = "";
//        Optional<Relationships> relationship = relationshipRepository.findRelationshipsByGardenerIdAndFriendId(gardenerId, friendId);
//        if (relationship.get().getStatus() == "accepted") {
//            buttonLabel = "Remove friend";
//        }
//        if (!relationship.isPresent()) {
//            buttonLabel = "Add friend";
//        }
//        if (gardenerId == relationship.get().getGardenerId() && relationship.get().getStatus() == "pending") {
//            buttonLabel = "Cancel request";
//        }
//        if (gardenerId == relationship.get().getFriendId() && relationship.get().getStatus() == "pending") {
//            buttonLabel = "Remove friend";
//        }
//
//        return buttonLabel;
//    }

}
