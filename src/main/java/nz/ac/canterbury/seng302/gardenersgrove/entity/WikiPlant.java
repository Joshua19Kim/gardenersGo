package nz.ac.canterbury.seng302.gardenersgrove.entity;


import java.util.List;

/**
 * This class stores all the information received from the API for a plant that will appear in the wiki.
 */
public class WikiPlant {

    private long id;

    private String name;

    private List<String> scientificName;
    private List<String> otherNames;
    private String cycle;
    private String watering;
    private List<String> sunlight;
    private String imagePath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WikiPlant(Long id, String name, List<String> scientificName, List<String> otherNames, String cycle, String watering, List<String> sunlight, String imagePath) {
        this.id = id;
        this.name = name;
        this.scientificName = scientificName;
        this.otherNames = otherNames;
        this.cycle = cycle;
        this.watering = watering;
        this.sunlight = sunlight;
        this.imagePath = imagePath;
    }



    public String getName() {return name;}

    public List<String> getScientificName() {return scientificName;}

    public void setName(String name) {
        this.name = name;
    }

    public void setScientificName(List<String> scientificName) {
        this.scientificName = scientificName;
    }

    public List<String> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(List<String> otherNames) {
        this.otherNames = otherNames;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getWatering() {
        return watering;
    }

    public void setWatering(String watering) {
        this.watering = watering;
    }

    public List<String> getSunlight() {
        return sunlight;
    }

    public void setSunlight(List<String> sunlight) {
        this.sunlight = sunlight;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
