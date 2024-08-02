package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a visit of a user to a garden.
 * Stores the garden, gardener, and access time.
 */
@Entity
@Table(name = "recent_gardens")
public class RecentGardens {

    /** The unique identifier for the visit to the garden. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The gardener who visited the garden. */
    @ManyToOne
    @JoinColumn(name = "gardener_id", nullable = false)
    private Gardener gardener;

    /** The garden that was visited. */
    @ManyToOne
    @JoinColumn(name = "garden_id", nullable = false)
    private Garden garden;

    /** The timestamp when the garden was visited. */
    @Column(name = "access_time", nullable = false)
    private LocalDateTime accessTime;

    /** Default constructor required by JPA. */
    protected RecentGardens() {}

    /**
     * Constructs a garden visit entry with the given gardener, garden, and access time.
     *
     * @param gardener The gardener who visited the garden.
     * @param garden The garden that was visited.
     * @param accessTime The timestamp when the garden was visited.
     */
    public RecentGardens(Gardener gardener, Garden garden, LocalDateTime accessTime) {
        this.gardener = gardener;
        this.garden = garden;
        this.accessTime = accessTime;
    }

    public Long getId() {
        return id;
    }

    public Gardener getGardener() {
        return gardener;
    }

    public void setGardener(Gardener gardener) {
        this.gardener = gardener;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    public LocalDateTime getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }
}