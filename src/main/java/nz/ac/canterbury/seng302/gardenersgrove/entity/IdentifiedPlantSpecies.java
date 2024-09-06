package nz.ac.canterbury.seng302.gardenersgrove.entity;


public class IdentifiedPlantSpecies {
    private String speciesName;
    private String imageUrl;
    private Long count;

    public IdentifiedPlantSpecies(String speciesName, String imageUrl, Long count) {
        this.speciesName = speciesName;
        this.imageUrl = imageUrl;
        this.count = count;
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