package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

/**
 * Entity class reflecting an entry of firstName, optional lastName, date of birth, email, and password
 * Note the @link{Entity} annotation required for declaring this as a persistence entity
 */
@Entity
@Table(name = "relationships")
public class Relationships {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relationship_id")
    private Long id;

    @Column(name = "gardener_id", nullable = false)
    private Long gardenerId;

    @Column(name = "friend_id", nullable = false)
    private Long friendId;

    @Column(nullable = false)
    private String status;

    /**
     * JPA required no-args constructor
     */
    protected Relationships() {}

    /**
     * Creates a new Relationship object
     * @param gardenerId the id of the logged-in user
     * @param friendId the id of the gardener the relationship is with
     * @param status status of the relationship between gardeners e.g. "accepted" or "pending"
     */
    public Relationships(Long gardenerId, Long friendId, String status) {
        this.gardenerId = gardenerId;
        this.friendId = friendId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getGardenerId() {
        return gardenerId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setGardenerId(Long gardenerId) { this.gardenerId = gardenerId; }

    public void setFriendId(Long friendId) { this.friendId = friendId; }

    public void setStatus(String status) { this.status = status; }


    @Override
    public String toString() {
        String relationshipString = "Relationship{" +
                "relationshipId = " + id +
                ": Gardener (id) = " + gardenerId + '\'' +
                "Friend (id) = " + friendId + '\'' +
                "Relationship Status = " + status;

        return relationshipString;
    }
}
