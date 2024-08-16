package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

/**
 * Represents a plant that a user has scanned (and/or added to their collection).
 *
 * ########## more to be added when clear what API sends #######################
 */
@Entity
public class ScannedPlant {

    /** Unique ID of the scanned plant*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Plant species of the scanned plant*/
    @ManyToOne
    @JoinColumn(name = "plant_species_id")
    private PlantSpecies plantSpecies;

    /** name of the scanned plant*/
    @Column
    private String name;

    protected ScannedPlant() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlantSpecies getPlantSpecies() {
        return plantSpecies;
    }

    public void setPlantSpecies(PlantSpecies plantSpecies) {
        this.plantSpecies = plantSpecies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
