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
  @Column(nullable = false)
  private String name;

  /** The location of the garden. */
  @Column(nullable = false)
  private String location;

  /** The size of the garden in square units. */
  @Column
  private float size;

  /** The list of plants in the garden. */
  @OneToMany(mappedBy = "garden")
  private List<Plant> plants;

  /** Default constructor required by JPA. */
  protected Garden() {}

  /**
   * Constructs a garden with the given name and location.
   *
   * @param name The name of the garden.
   * @param location The location of the garden.
   */
  public Garden(String name, String location) {
    this.name = name;
    this.location = location;
    plants = new ArrayList<>();
  }

  /**
   * Constructs a garden with the given name, location, and size.
   *
   * @param name The name of the garden.
   * @param location The location of the garden.
   * @param size The size of the garden.
   */
  public Garden(String name, String location, float size) {
    this.name = name;
    this.location = location;
    this.size = size;
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
  public float getSize() {
    return size;
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
  public void setSize(float size) {this.size = size; }

  /**
   * Sets the location of the garden
   * @param location the location of the garden
   */
  public void setLocation(String location) {this.location = location;}

  /**
   * Sets the id of the garden
   * @param id the garden id
   */
  public void setId(Long id) {this.id = id;}
}
