package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
public class ScannedPlant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plant_species_id")
    private PlantSpecies plantSpecies;

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
