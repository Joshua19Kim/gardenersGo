package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a plant identified by the system.
 * It stores details about the plant identification, including scientific names, common names, the associated gardener,
 * and the images used for identification.
 */
@Entity
public class IdentifiedPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gardener_id", nullable = false)
    private Gardener gardener;

    @Column(name = "best_match")
    private String bestMatch;

    @Column(name = "score")
    private double score;

    @Column(name = "species_scientific_name_without_author")
    private String speciesScientificNameWithoutAuthor;


    @Column(name = "family_scientific_name_without_author")
    private String familyScientificNameWithoutAuthor;


    @ElementCollection
    @CollectionTable(name = "common_names", joinColumns = @JoinColumn(name = "identified_plant_id"))
    @Column(name = "common_name")
    private List<String> commonNames;

    @Column(name = "gbif_id")
    private String gbifId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "uploaded_image")
    private String uploadedImage;
    @Column(name = "date_uploaded")
    private Date dateUploaded;

    /**
     * JPA required no-args constructor
     */
    protected IdentifiedPlant() {}

    /**
     * Constructs an IdentifiedPlant with the specified identification details.
     *
     * @param bestMatch                  the best match for the plant identification
     * @param result                     the JSON node containing the identification results
     * @param commonNames                the list of common names for the identified plant
     * @param gardener                   the gardener associated with the identified plant
     * @param imagePath                  the path to the uploaded image used for identification
     */
    public IdentifiedPlant(String bestMatch, JsonNode result, List<String> commonNames, Gardener gardener, String imagePath) {
        this.gardener = gardener;
        this.bestMatch = bestMatch;
        this.score = result.get("score").asDouble();
        this.speciesScientificNameWithoutAuthor = result.get("species").get("scientificNameWithoutAuthor").asText();
        this.familyScientificNameWithoutAuthor = result.get("species").get("family").get("scientificNameWithoutAuthor").asText();
        this.commonNames = commonNames;
        this.gbifId = result.get("gbif").get("id").asText();
        this.imageUrl = result.get("images").get(0).get("url").get("o").asText();
        this.uploadedImage = imagePath;
        this.dateUploaded = new Date();

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

    public String getBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(String bestMatch) {
        this.bestMatch = bestMatch;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSpeciesScientificNameWithoutAuthor() {
        return speciesScientificNameWithoutAuthor;
    }

    public void setSpeciesScientificNameWithoutAuthor(String speciesScientificNameWithoutAuthor) {
        this.speciesScientificNameWithoutAuthor = speciesScientificNameWithoutAuthor;
    }


    public String getFamilyScientificNameWithoutAuthor() {
        return familyScientificNameWithoutAuthor;
    }

    public void setFamilyScientificNameWithoutAuthor(String familyScientificNameWithoutAuthor) {
        this.familyScientificNameWithoutAuthor = familyScientificNameWithoutAuthor;
    }

    public List<String> getCommonNames() {
        return commonNames;
    }

    public void setCommonNames(List<String> commonNames) {
        this.commonNames = commonNames;
    }

    public String getGbifId() {
        return gbifId;
    }

    public void setGbifId(String gbifId) {
        this.gbifId = gbifId;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(String uploadedImage) {
        this.uploadedImage = uploadedImage;
    }
}
