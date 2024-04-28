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
        List<Long> allFriendIds = relationshipRepository.getGardenerFriends(id);
        return gardenerFormService.getGardenersById(allFriendIds);
    }

    public List<Gardener> getGardenerPending (Long id) {
        List<Long> allPendingFriendIds = relationshipRepository.getGardenerPending(id);
        return gardenerFormService.getGardenersById(allPendingFriendIds);
    }

    public List<Gardener> getGardenerIncoming (Long id) {
        List<Long> allIncomingFriendIds = relationshipRepository.getGardenerIncoming(id);
        return gardenerFormService.getGardenersById(allIncomingFriendIds);
    }


    public List<Gardener> getGardenerDeclinedRequests(Long id) {
        List<Long> allDeclinedFriendIds = relationshipRepository.getGardenerDeclinedRequests(id);
        return gardenerFormService.getGardenersById(allDeclinedFriendIds);
    }

    public void updateRelationshipStatus(String status, Long gardenerId, Long friendId) {
        relationshipRepository.updateRelationshipStatus(status, gardenerId, friendId);
    }

    public List<Gardener> getGardenersWithNoRelationship(List<Gardener> allRelationships, List<Gardener> allGardeners) {
        List<Gardener> noExistingRelationship = new ArrayList<>(allGardeners);
        noExistingRelationship.removeAll(allRelationships);

        return noExistingRelationship;
    }

}
