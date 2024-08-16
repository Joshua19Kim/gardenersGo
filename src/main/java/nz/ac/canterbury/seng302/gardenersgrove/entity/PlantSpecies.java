package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "plant_species")
public class PlantSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long count;

    @Column
    private String name;

    @Column
    private String imageFilename;

    @OneToMany( mappedBy = "plantSpecies")
    private List<ScannedPlant> plants;

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


}
