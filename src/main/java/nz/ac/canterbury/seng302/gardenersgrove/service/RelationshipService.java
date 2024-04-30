package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Service class for Relationships, defined by the @link{Service} annotation.
 * This class links automatically with @link{RelationshipRepository}, see the @link{Autowired} annotation below
 */
@Service
public class RelationshipService {
    private final RelationshipRepository relationshipRepository;
    private final GardenerFormService gardenerFormService;

    @Autowired
    public RelationshipService(RelationshipRepository relationshipRepository, GardenerFormService gardenerFormService) {
        this.relationshipRepository = relationshipRepository;
        this.gardenerFormService = gardenerFormService;

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
     *
     * @param relationships object to persist
     */
    public void addRelationship(Relationships relationships) {
        relationshipRepository.save(relationships);
    }

    public Optional<Relationships> getRelationShip(long gardenerId, long friendId) {
        return relationshipRepository.findRelationshipsByGardenerIdAndFriendId(gardenerId, friendId);
    }


    /**
     * Gets all the users that the user with id is currently friends with
     * @param id the id of the user
     * @return the users that are friends with the user with id
     */
    public List<Gardener> getCurrentUserRelationships (Long id) {
        List<Long> allFriendIds = relationshipRepository.getGardenerFriends(id);
        return gardenerFormService.getGardenersById(allFriendIds);
    }

    /**
     * Gets all the requests that have been sent from this user to other users
     * @param id the id of the user
     * @return the users that have received requests from the user with id
     */
    public List<Gardener> getGardenerPending (Long id) {
        List<Long> allPendingFriendIds = relationshipRepository.getGardenerPending(id);
        return gardenerFormService.getGardenersById(allPendingFriendIds);
    }

    /**
     * Gets all the requests that have been sent to this user from another user
     * @param id the id of the user
     * @return the users which have sent request to the user with id
     */
    public List<Gardener> getGardenerIncoming (Long id) {
        List<Long> allIncomingFriendIds = relationshipRepository.getGardenerIncoming(id);
        return gardenerFormService.getGardenersById(allIncomingFriendIds);
    }


    /**
     * Gets the requests that the user has sent out that have been declined by the other user
     * @param id the id of the user
     * @return the users which have declined the user with the id
     */
    public List<Gardener> getGardenerDeclinedRequests(Long id) {
        List<Long> allDeclinedFriendIds = relationshipRepository.getGardenerDeclinedRequests(id);
        return gardenerFormService.getGardenersById(allDeclinedFriendIds);
    }

    /**
     * Updates the relationship status for the relationship between the gardener and the friend
     * @param status the status which can be accepted, pending or declined
     * @param gardenerId the id of the gardener
     * @param friendId the id of the friend
     */
    public void updateRelationshipStatus(String status, Long gardenerId, Long friendId) {
        Optional<Relationships> potentialRelationship = relationshipRepository.findRelationshipsByGardenerIdAndFriendId(gardenerId, friendId);
        if(potentialRelationship.isPresent()) {
            Relationships relationship = potentialRelationship.get();
            relationship.setStatus(status);
            relationshipRepository.save(relationship);
        }

    }

    /**
     * Gets all the gardeners that do not exist in the relationship table
     * @param allRelationships all gardeners that are in relationships
     * @param allGardeners all gardeners in the database
     * @return all gardeners that dont have a relationship
     */
    public List<Gardener> getGardenersWithNoRelationship(List<Gardener> allRelationships, List<Gardener> allGardeners) {
        List<Gardener> noExistingRelationship = new ArrayList<>(allGardeners);
        noExistingRelationship.removeAll(allRelationships);
        // remove current user

        return noExistingRelationship;
    }

    /**
     * Deletes the relationship if it exists
     * @param gardenerId the gardener in the relationship
     * @param friendId the user in the relationship
     */
    public void deleteRelationShip(long gardenerId, long friendId) {
        Optional<Relationships> potentialRelationship = relationshipRepository.findRelationshipsByGardenerIdAndFriendId(gardenerId, friendId);
        if(potentialRelationship.isPresent()) {
            Relationships relationship = potentialRelationship.get();
            relationshipRepository.deleteById(relationship.getId());
        }
    }

}
