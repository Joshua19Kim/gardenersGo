package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.util.List;

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

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "uploaded_image")
    private String uploadedImage;

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
