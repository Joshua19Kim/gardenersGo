package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for FormResults, defined by the @link{Service} annotation.
 * This class links automatically with @link{FormRepository}, see the @link{Autowired} annotation below
 */
@Service
public class RelationshipService {
    private final RelationshipRepository relationshipRepository;

    @Autowired
    public RelationshipService(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
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

    public Optional<Relationships> findById(long id) {
        return relationshipRepository.findById(id);
    }

    public boolean relationshipExists(int gardenerId, int friendId) {
        return relationshipRepository.existsByGardenerIdAndFriendId(gardenerId, friendId);
    }

}
