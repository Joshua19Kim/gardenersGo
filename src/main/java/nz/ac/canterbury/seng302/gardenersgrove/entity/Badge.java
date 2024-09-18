package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Class that stores all the details about a badge
 */
@Entity
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BadgeType badgeType;

    private LocalDate dateEarned;

    @ManyToOne
    @JoinColumn(name = "gardener_id")
    private Gardener gardener;

    public Badge(){}
    public Badge(String name, LocalDate dateEarned, BadgeType badgeType, Gardener gardener){
        this.name = name;
        this.dateEarned = dateEarned;
        this.badgeType = badgeType;
        this.gardener= gardener;
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
}
