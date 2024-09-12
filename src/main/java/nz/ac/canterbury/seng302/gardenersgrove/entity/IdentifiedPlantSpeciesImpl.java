package nz.ac.canterbury.seng302.gardenersgrove.entity;

public class IdentifiedPlantSpeciesImpl extends IdentifiedPlantSpecies {
    private String speciesName;
    private String imageUrl;
    private Long count;

    public IdentifiedPlantSpeciesImpl(String speciesName, String imageUrl, Long count) {
        this.speciesName = speciesName;
        this.imageUrl = imageUrl;
        this.count = count;
    }

    @Override
    public String getSpeciesName() {
        return speciesName;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Long getCount() {
        return count;
    }

}
