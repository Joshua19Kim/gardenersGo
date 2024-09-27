package nz.ac.canterbury.seng302.gardenersgrove.entity;

/**
 * This class represents a species of identified plants. It has a image for the species and keeps track of the number
 * of identified plants that are part of that species.
 */
public class IdentifiedPlantSpecies {
    private String speciesName;
    private String imageUrl;
    private Long count;


    public IdentifiedPlantSpecies(String speciesName, String imageUrl, Long count) {
        this.speciesName = speciesName;
        this.imageUrl = imageUrl;
        this.count = count;
    }

    protected IdentifiedPlantSpecies() {
    }


    public String getSpeciesName() {
        return speciesName;
    }

    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}