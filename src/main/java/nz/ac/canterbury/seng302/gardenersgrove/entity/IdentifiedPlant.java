package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Entity representing a plant identified by the system.
 * It stores details about the plant identification, including scientific names, common names, the associated gardener,
 * and the images used for identification.
 */
@Entity
@Table(name = "IDENTIFIEDPLANT")
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
    @CollectionTable(name = "common_names", joinColumns = @JoinColumn(name = "IDENTIFIEDPLANT_id"))
    @Column(name = "common_name")
    private List<String> commonNames;

    @Column(name = "gbif_id")
    private String gbifId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "uploaded_image")
    private String uploadedImage;

    @Column(name = "date_uploaded")
    private String dateUploaded;

    /**
     * Name of the plant.
     */
    @Column(length=64)
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Description of the plant.
     */
    @Column(length=512)
    private String description;

    /**
     * JPA required no-args constructor
     */
    public IdentifiedPlant() {}

    /**
     * Constructs an IdentifiedPlant with the specified identification details.
     *
     * @param bestMatch the best match for the plant identification
     * @param score the matching score for the plant identified plant
     * @param commonNames the list of common names for the identified plant
     * @param speciesScientificNameWithoutAuthor The species Scientific name of the identified plant without Author
     * @param familyScientificNameWithoutAuthor The family Scientific name of the identified plant without Author
     * @param imagePath the path to the uploaded image used for identification
     * @param gardener the gardener associated with the identified plant
     */
    public IdentifiedPlant(String bestMatch, Double score, List<String> commonNames,
                           String gbifId, String imageUrl, String imagePath,
                           String speciesScientificNameWithoutAuthor, String familyScientificNameWithoutAuthor,
                           Gardener gardener) {
        this.gardener = gardener;
        this.bestMatch = bestMatch;
        this.score = score;
        this.speciesScientificNameWithoutAuthor = speciesScientificNameWithoutAuthor;
        this.familyScientificNameWithoutAuthor = familyScientificNameWithoutAuthor;
        this.commonNames = commonNames;
        this.gbifId = gbifId;
        this.imageUrl = imageUrl;
        this.uploadedImage = imagePath;
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateUploaded = currentDate.format(formatter);

    }


    /**
     * Constructs an IdentifiedPlant with the new plant details.
     *
     * @param plantName the name of the new plant
     * @param description the description of the new plant
     * @param speciesScientificNameWithoutAuthor the scientific name of the new plant
     * @param uploadedDate the date of the plant uploaded
     * @param gardener the gardener associated with the new plant
     */
    public IdentifiedPlant(String plantName, String description,
                           String speciesScientificNameWithoutAuthor, LocalDate uploadedDate,
                           Gardener gardener) {

        this.name = plantName;
        this.description = description;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateUploaded = uploadedDate.format(formatter);
        this.speciesScientificNameWithoutAuthor = speciesScientificNameWithoutAuthor;
        this.gardener = gardener;


    }

    /**
     * A constructor for the required parameters of identified plant
     * @param plantName the name of the plant
     * @param gardener the gardener it belongs to
     */
    public IdentifiedPlant(String plantName, Gardener gardener) {
        this.name = plantName;
        this.gardener = gardener;
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
    public String getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(String dateUploaded) {
        this.dateUploaded = dateUploaded;
    }
}
