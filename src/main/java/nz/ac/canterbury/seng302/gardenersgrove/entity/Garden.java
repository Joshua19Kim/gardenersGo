package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Represents a garden entity with its name, location, size, and list of plants. */
@Entity
public class Garden {
  /** The unique identifier for the garden. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** The name of the garden. */
  @Column(length=64, nullable = false)
  private String name;

  /** The location of the garden. */
  @Column(length=128 ,nullable = false)
  private String location;

  /** The size of the garden in square units. */
  @Column(length=9)
  private String size;

  /** The list of plants in the garden. */
  @OneToMany(mappedBy = "garden")
  private List<Plant> plants;

  /** The gardener to which the garden belongs. */
  @ManyToOne
  @JoinColumn(name = "gardener_id")
  private Gardener gardener;

  /** Default constructor required by JPA. */
  protected Garden() {}

  /**
   * Constructs a garden with the given name, location and gardener.
   *
   * @param name The name of the garden.
   * @param location The location of the garden.
   * @param gardener The gardener id for the garden.
   */
  public Garden(String name, String location, Gardener gardener) {
    this.name = name;
    this.location = location;
    this.gardener = gardener;
    plants = new ArrayList<>();
  }

  /**
   * Constructs a garden with the given name, location, size and gardener.
   *
   * @param name The name of the garden.
   * @param location The location of the garden.
   * @param size The size of the garden.
   * @param gardener The gardener id for the garden.
   */
  public Garden(String name, String location, String size, Gardener gardener) {
    this.name = name;
    this.location = location;
    this.size = size;
    this.gardener = gardener;
    plants = new ArrayList<>();
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
   * Sets the name of the garden
   * @param name the name of the garden
   */
  public void setName(String name) {this.name = name;}

  /**
   * Sets the size of the garden
   * @param size the size of the garden
   */
  public void setSize(String size) {this.size = size; }

  /**
   * Sets the location of the garden
   * @param location the location of the garden
   */
  public void setLocation(String location) {this.location = location;}

  /**
   * Sets the owner of the garden.
   *
   * @param gardener The owner of the garden.
   */
  public void setGardener(Gardener gardener) {this.gardener = gardener;}

  /**
   * Sets the id of the garden
   * @param id the garden id
   */
  public void setId(Long id) {this.id = id;}
}
