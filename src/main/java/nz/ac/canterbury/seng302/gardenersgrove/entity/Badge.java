package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Class that stores all the details about a badge
 */
@Entity
public class Badge {


    /**
     * Auto generated id used as a primary key in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the badge
     */
    @Column
    private String name;

    /**
     * The type of badge it is, either Plants, Species, or Region
     */
    @Column
    private BadgeType badgeType;

    /**
     * The date the badge was earned
     */
    @Column
    private LocalDate dateEarned;

    /**
     * The filename of the image for the badge
     */
    @Column
    private String imageFilename;

    /**
     * The gardener that has the badge
     */
    @ManyToOne
    @JoinColumn(name = "gardener_id")
    private Gardener gardener;

    /**
     * Default constructor for JPA
     */
    public Badge() {
    }


    /**
     * Constructs a Badge object.
     *
     * @param name          the name of the badge
     * @param dateEarned    the date earned of the badge
     * @param badgeType     the type of the badge
     * @param gardener      the gardener that the badge belongs to
     * @param imageFilename the filename of the image
     */
    public Badge(String name, LocalDate dateEarned, BadgeType badgeType, Gardener gardener, String imageFilename) {
        this.name = name;
        this.dateEarned = dateEarned;
        this.badgeType = badgeType;
        this.gardener = gardener;
        this.imageFilename = imageFilename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public LocalDate getDateEarned() {
        return dateEarned;
    }

    public void setDateEarned(LocalDate dateEarned) {
        this.dateEarned = dateEarned;
    }

    public Gardener getGardener() {
        return gardener;
    }

    public void setGardener(Gardener gardener) {
        this.gardener = gardener;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }
}
