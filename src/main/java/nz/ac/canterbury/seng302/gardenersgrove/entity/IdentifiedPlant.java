package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

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

    @Column(name = "species_scientific_name_authorship")
    private String speciesScientificNameAuthorship;

    @Column(name = "species_scientific_name")
    private String speciesScientificName;

    @Column(name = "genus_scientific_name_without_author")
    private String genusScientificNameWithoutAuthor;

    @Column(name = "genus_scientific_name_authorship")
    private String genusScientificNameAuthorship;

    @Column(name = "genus_scientific_name")
    private String genusScientificName;

    @Column(name = "family_scientific_name_without_author")
    private String familyScientificNameWithoutAuthor;

    @Column(name = "family_scientific_name_authorship")
    private String familyScientificNameAuthorship;

    @Column(name = "family_scientific_name")
    private String familyScientificName;

    @ElementCollection
    @CollectionTable(name = "common_names", joinColumns = @JoinColumn(name = "identified_plant_id"))
    @Column(name = "common_name")
    private List<String> commonNames;

    @Column(name = "gbif_id")
    private String gbifId;

    @Column(name = "powo_id")
    private String powoId;

    @Column(name = "iucn_id")
    private String iucnId;

    @Column(name = "iucn_category")
    private String iucnCategory;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "uploaded_image")
    private String uploadedImage;

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
        this.speciesScientificNameAuthorship = result.get("species").get("scientificNameAuthorship").asText();
        this.speciesScientificName = result.get("species").get("scientificName").asText();
        this.genusScientificNameWithoutAuthor = result.get("species").get("genus").get("scientificNameWithoutAuthor").asText();
        this.genusScientificNameAuthorship = result.get("species").get("genus").get("scientificNameAuthorship").asText();
        this.genusScientificName = result.get("species").get("genus").get("scientificName").asText();
        this.familyScientificNameWithoutAuthor = result.get("species").get("family").get("scientificNameWithoutAuthor").asText();
        this.familyScientificNameAuthorship = result.get("species").get("family").get("scientificNameAuthorship").asText();
        this.familyScientificName = result.get("species").get("family").get("scientificName").asText();
        this.commonNames = commonNames;
        this.gbifId = result.get("gbif").get("id").asText();
        this.powoId = result.get("powo").get("id").asText();
        this.imageUrl = result.get("images").get(0).get("url").get("o").asText();
        this.uploadedImage = imagePath;

        // These fields appear to only appear in some responses
        if (result.has("iucn")) {
            JsonNode iucn = result.get("iucn");
            this.iucnId = iucn.has("id") ? result.get("iucn").get("id").asText() : null;
            this.iucnCategory = iucn.has("category") ? result.get("iucn").get("category").asText() : null;
        }
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

    public String getSpeciesScientificNameAuthorship() {
        return speciesScientificNameAuthorship;
    }

    public void setSpeciesScientificNameAuthorship(String speciesScientificNameAuthorship) {
        this.speciesScientificNameAuthorship = speciesScientificNameAuthorship;
    }

    public String getSpeciesScientificName() {
        return speciesScientificName;
    }

    public void setSpeciesScientificName(String speciesScientificName) {
        this.speciesScientificName = speciesScientificName;
    }

    public String getGenusScientificNameWithoutAuthor() {
        return genusScientificNameWithoutAuthor;
    }

    public void setGenusScientificNameWithoutAuthor(String genusScientificNameWithoutAuthor) {
        this.genusScientificNameWithoutAuthor = genusScientificNameWithoutAuthor;
    }

    public String getGenusScientificNameAuthorship() {
        return genusScientificNameAuthorship;
    }

    public void setGenusScientificNameAuthorship(String genusScientificNameAuthorship) {
        this.genusScientificNameAuthorship = genusScientificNameAuthorship;
    }

    public String getGenusScientificName() {
        return genusScientificName;
    }

    public void setGenusScientificName(String genusScientificName) {
        this.genusScientificName = genusScientificName;
    }

    public String getFamilyScientificNameWithoutAuthor() {
        return familyScientificNameWithoutAuthor;
    }

    public void setFamilyScientificNameWithoutAuthor(String familyScientificNameWithoutAuthor) {
        this.familyScientificNameWithoutAuthor = familyScientificNameWithoutAuthor;
    }

    public String getFamilyScientificNameAuthorship() {
        return familyScientificNameAuthorship;
    }

    public void setFamilyScientificNameAuthorship(String familyScientificNameAuthorship) {
        this.familyScientificNameAuthorship = familyScientificNameAuthorship;
    }

    public String getFamilyScientificName() {
        return familyScientificName;
    }

    public void setFamilyScientificName(String familyScientificName) {
        this.familyScientificName = familyScientificName;
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

    public String getPowoId() {
        return powoId;
    }

    public void setPowoId(String powoId) {
        this.powoId = powoId;
    }

    public String getIucnId() {
        return iucnId;
    }

    public void setIucnId(String iucnId) {
        this.iucnId = iucnId;
    }

    public String getIucnCategory() {
        return iucnCategory;
    }

    public void setIucnCategory(String iucnCategory) {
        this.iucnCategory = iucnCategory;
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
