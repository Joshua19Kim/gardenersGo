package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

/**
 * A tag is a descriptive string a user can apply to their garden
 */
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private long id;

    @Column(name = "tag_name", length = 25, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "garden")
    private Garden garden;

    protected Tag() {}

    public Tag(String name, Garden garden) {
        this.name = name;
        this.garden = garden;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }
}


