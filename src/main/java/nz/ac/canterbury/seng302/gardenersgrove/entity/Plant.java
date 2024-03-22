package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

/**
 * Represents a plant entity.
 */
@Entity
public class Plant {
    /**
     * Unique identifier for the plant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the plant.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Count of the plant.
     */
    @Column
    private float count;

    /**
     * Description of the plant.
     */
    @Column(length=512)
    private String description;

    /**
     * Date when the plant was planted.
     */
    @Column
    private String datePlanted;

    /**
     * The garden to which the plant belongs.
     */
    @ManyToOne
    @JoinColumn(name = "garden_id")
    private Garden garden;

    /**
     * Default constructor required by JPA.
     */
    protected Plant() {}

    /**
     * Constructs a plant with only name and garden specified.
     * @param name The name of the plant.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, Garden garden) {
        this.name = name;
        this.garden = garden;
    }

    /**
     * Constructs a plant with name, count, and garden specified.
     * @param name The name of the plant.
     * @param count The count of the plant.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, float count, Garden garden) {
        this.name = name;
        this.count = count;
        this.garden = garden;
    }

    /**
     * Constructs a plant with name, description, and garden specified.
     * @param name The name of the plant.
     * @param description The description of the plant.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, String description, Garden garden) {
        this.name = name;
        this.description = description;
        this.garden = garden;
    }

    /**
     * Constructs a plant with name, date planted, and garden specified.
     * @param name The name of the plant.
     * @param garden The garden where the plant is located.
     * @param datePlanted The date when the plant was planted.
     */
    public Plant(String name, Garden garden, String datePlanted) {
        this.name = name;
        this.datePlanted = datePlanted;
        this.garden = garden;
    }

    /**
     * Constructs a plant with name, count, description, and garden specified.
     * @param name The name of the plant.
     * @param count The count of the plant.
     * @param description The description of the plant.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, float count, String description, Garden garden) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.garden = garden;
    }

    /**
     * Constructs a plant with name, count, description, and garden specified.
     * @param name The name of the plant.
     * @param datePlanted The date when the plant was planted.
     * @param count The count of the plant.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, String datePlanted, float count, Garden garden) {
        this.name = name;
        this.count = count;
        this.datePlanted = datePlanted;
        this.garden = garden;
    }

    /**
     * Constructs a plant with name, count, description, and garden specified.
     * @param name The name of the plant.
     * @param description The description of the plant.
     * @param datePlanted The date when the plant was planted.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, String description, String datePlanted, Garden garden) {
        this.name = name;
        this.description = description;
        this.datePlanted = datePlanted;
        this.garden = garden;
    }

    /**
     * Constructs a plant with all attributes specified.
     * @param name The name of the plant.
     * @param count The count of the plant.
     * @param description The description of the plant.
     * @param datePlanted The date when the plant was planted.
     * @param garden The garden where the plant is located.
     */
    public Plant(String name, float count, String description, String datePlanted, Garden garden) {
        this.name = name;
        this.count = count;
        this.description = description;
        this.datePlanted = datePlanted;
        this.garden = garden;
    }

    /**
     * Gets the ID of the plant.
     * @return The ID of the plant.
     */
    public Long getId() { return id;}

    /**
     * Gets the name of the plant.
     * @return The name of the plant.
     */
    public String getName() { return name;}

    /**
     * Gets the count of the plant.
     * @return The count of the plant.
     */
    public float getCount() { return count;}

    /**
     * Gets the description of the plant.
     * @return The description of the plant.
     */
    public String getDescription() {return description;}

    /**
     * Gets the date when the plant was planted.
     * @return The date when the plant was planted.
     */
    public String getDatePlanted() {return datePlanted;}

    /**
     * Gets the garden where the plant is located.
     * @return The garden where the plant is located.
     */
    public Garden getGarden() {return garden;}

    /**
     * Sets the name of the plant
     * @param name the name
     */
    public void setName(String name) {this.name = name;}

    /**
     * Sets the description of the plant
     * @param description the description
     */
    public void setDescription(String description) {this.description = description;}

    /**
     * Sets the count of the plant
     * @param count the count
     */
    public void setCount(float count) {this.count = count;}

    /**
     * Sets the date planted
     * @param datePlanted the date planted
     */
    public void setDatePlanted(String datePlanted) {this.datePlanted = datePlanted;}
}

