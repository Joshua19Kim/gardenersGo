package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

/**
 * Entity class reflecting an entry of firstName, optional lastName, date of birth, email, and password
 * Note the @link{Entity} annotation required for declaring this as a persistence entity
 */
@Entity
@Table(name = "follower")
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follower_id")
    private Long id;

    @Column(name = "gardener_id", nullable = false)
    private Long gardenerId;

    @Column(name = "garden_id", nullable = false)
    private Long gardenId;

    /**
     * JPA required no-args constructor
     */
    protected Follower() {}

    /**
     * Creates a new follower object
     * @param gardenerId the id of the logged-in user
     * @param gardenId the id of the garden to follow
     */
    public Follower(Long gardenerId, Long gardenId) {
        this.gardenerId = gardenerId;
        this.gardenId = gardenId;
    }

    public Long getId() {
        return id;
    }

    public Long getGardenerId() {
        return gardenerId;
    }

    public Long getGardenId() {
        return gardenId;
    }

    public void setGardenerId(Long gardenerId) { this.gardenerId = gardenerId; }

    public void setGardenId(Long gardenId) { this.gardenId = gardenId; }
}
