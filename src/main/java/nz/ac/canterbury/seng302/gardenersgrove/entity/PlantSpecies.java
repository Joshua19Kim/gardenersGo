package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.util.List;

/**
 * The Plant species. This is where the bulk of the information on a plant species is stored
 *
 * ############################ more to be added once clear what comes from API ####
 */
@Entity
@Table(name = "plant_species")
public class PlantSpecies {

    /** Unique ID of the plant species*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** the count of the plant species that a user has in their collection*/
    @Column
    private long count;

    /** name of the plant species*/
    @Column
    private String name;

    /** string filepath of the image that is stored for the plant species*/
    @Column
    private String imageFilename;

    /** an instance of the plant species can be used by many gardeners collections*/
    @OneToMany( mappedBy = "plantSpecies")
    private List<ScannedPlant> plants;

    /** The gardener to which the species belongs. */
    @ManyToOne
    @JoinColumn(name = "gardener_id")
    private Gardener gardener;

    protected PlantSpecies() {}

    public PlantSpecies(String name, int count, String imageFilename) {
        this.name = name;
        this.count = count;
        this.imageFilename = imageFilename;
    }

    public PlantSpecies(String name, int count, String imageFilename, Gardener gardener) {
        this.name = name;
        this.count = count;
        this.imageFilename = imageFilename;
        this.gardener = gardener;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public List<ScannedPlant> getPlants() {
        return plants;
    }

    public void setPlants(List<ScannedPlant> plants) {
        this.plants = plants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Gardener getGardener() {
        return gardener;
    }

    public void setGardener(Gardener gardener) {
        this.gardener = gardener;
    }
}
