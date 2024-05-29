package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Represents a garden entity with its name, location, size, and list of plants. */
@Entity
@Table(name = "garden")
public class Garden {
  /** The unique identifier for the garden. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The name of the garden. */
  @Column(length=64, nullable = false)
  private String name;

  /** The location(Street number and name) of the garden. */
  @Column(length=60)
  private String location;

  /** The suburb of the garden. */
  @Column(length=90)
  private String suburb;

  /** The city of the garden. */
  @Column(length=180 ,nullable = false)
  private String city;

  /** The country of the garden. */
  @Column(length=60 ,nullable = false)
  private String country;

  /** The postcode of the garden. */
  @Column(length=10)
  private String postcode;


  /** The size of the garden in square units. */
  @Column(length=12)
  private String size;

  /** The description of the garden. */
  @Column(length=512)
  private String description;

  /** The list of plants in the garden. */
  @OneToMany(mappedBy = "garden")
  private List<Plant> plants;

  /** The gardener to which the garden belongs. */
  @ManyToOne
  @JoinColumn(name = "gardener_id")
  private Gardener gardener;

  /** The publicity of the garden */
  @Column
  private boolean publicGarden;

  @Column
  private LocalDate lastNotified;

  /** Default constructor required by JPA. */
  protected Garden() {}


  /**
   * Constructs a garden with the given name, location, size and gardener.
   * By default, a garden is private
   *
   * @param name The name of the garden (required).
   * @param location The street number and street name of the garden.
   * @param suburb The suburb of the garden.
   * @param city The city of the garden (required).
   * @param country The country of the garden (required).
   * @param postcode The postcode of the garden.
   * @param gardener The gardener id for the garden.
   */
  public Garden(String name, String location, String suburb, String city, String country, String postcode, Gardener gardener, String description) {
    this.name = name;
    this.location = location;
    this.suburb = suburb;
    this.city = city;
    this.country = country;
    this.postcode = postcode;
    this.gardener = gardener;
    this.description = description;
    plants = new ArrayList<>();
    this.publicGarden = false; // Defaults to private
    this.lastNotified = null; // Defaults to null
  }
  /**
   * Constructs a garden with the given name, location, size and gardener.
   *
   * @param name The name of the garden (required).
   * @param location The street number and street name of the garden.
   * @param suburb The suburb of the garden.
   * @param city The city of the garden (required).
   * @param country The country of the garden (required).
   * @param postcode The postcode of the garden.
   * @param size The size of the garden.
   * @param gardener The gardener id for the garden.
   */
  public Garden(String name, String location, String suburb, String city, String country, String postcode, String size, Gardener gardener, String description) {
    this.name = name;
    this.location = location;
    this.suburb = suburb;
    this.city = city;
    this.country = country;
    this.postcode = postcode;
    this.size = size;
    this.gardener = gardener;
    this.description = description;
    plants = new ArrayList<>();
    this.publicGarden = false; // Defaults to private
    this.lastNotified = null; // Defaults to null
  }

  /**
   * Retrieves the unique identifier of the garden.
   *
   * @return The unique identifier of the garden.
   */
  public Long getId() {
    return id;
  }

  /**
   * Retrieves the name of the garden.
   *
   * @return The name of the garden.
   */
  public String getName() {
    return name;
  }

  /**
   * Retrieves the location of the garden.
   *
   * @return The location of the garden.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Retrieves the suburb of the garden.
   *
   * @return The suburb of the garden.
   */
  public String getSuburb() {
    return suburb;
  }

  /**
   * Retrieves the city of the garden.
   *
   * @return The city of the garden.
   */
  public String getCity() {
    return city;
  }

  /**
   * Retrieves the country of the garden.
   *
   * @return The country of the garden.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Retrieves the postcode of the garden.
   *
   * @return The postcode of the garden.
   */
  public String getPostcode() {
    return postcode;
  }

  /**
   * Retrieves the size of the garden.
   *
   * @return The size of the garden.
   */
  public String getSize() {
    return size;
  }

  /**
   * Retrieves the owner of the garden.
   *
   * @return The owner of the garden.
   */
  public Gardener getGardener() {
    return gardener;
  }

  /**
   * Retrieves the list of plants in the garden.
   *
   * @return The list of plants in the garden.
   */
  public List<Plant> getPlants() {
    return plants;
  }

  /**
   * Retrieves the publicity status of the garden
   *
   * @return publicity of the garden
   */
  public boolean getIsGardenPublic() {
    return publicGarden;
  }

    /**
     * Retrieves the garden description.
     *
     * @return The garden description.
     */
    public String getDescription() {
        return description;
    }

  /**
   * Sets the name of the garden
   *
   * @param name the name of the garden
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the size of the garden
   *
   * @param size the size of the garden
   */
  public void setSize(String size) {
    this.size = size;
  }

  /**
   * Sets the location of the garden
   *
   * @param location the location of the garden
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Sets the suburb of the garden
   * @param suburb the location of the garden
   */
  public void setSuburb(String suburb) {this.suburb = suburb;}

  /**
   * Sets the city of the garden
   * @param city the location of the garden
   */
  public void setCity(String city) {this.city = city;}

  /**
   * Sets the country of the garden
   * @param country the location of the garden
   */
  public void setCountry(String country) {this.country = country;}

  /**
   * Sets the postcode of the garden
   * @param postcode the location of the garden
   */
  public void setPostcode(String postcode) {this.postcode = postcode;}

  /**
   * Sets the owner of the garden.
   *
   * @param gardener The owner of the garden.
   */
  public void setGardener(Gardener gardener) {
    this.gardener = gardener;
  }

  /**
   * Sets the id of the garden
   *
   * @param id the garden id
   */
  public void setId(Long id) {
      this.id = id;
  }

  /**
   * Sets the publicity status of the garden
   *
   * @param isGardenPublic publicity of the garden
   */
  public void setIsGardenPublic(boolean isGardenPublic) {
    this.publicGarden = isGardenPublic;
  }

  public void setLastNotified(LocalDate lastNotified){this.lastNotified = lastNotified;}
  public LocalDate getLastNotified(){return lastNotified;}

    /**
     * Sets the description of the garden
     *
     * @param description the description of the garden
     */
    public void setDescription(String description) {
        this.description = description;
    }


  /**
   * Gets the address, city, and location of the garden
   * if the address is null it just gets the city and country
   *
   * @return the full location
   */
  public String getFullLocation() {
    if(location == null || location.isEmpty()) {
      return city + ", " + country;
    } else {
      return location + ", " + city + ", " + country;
    }
  }

}
