package nz.ac.canterbury.seng302.gardenersgrove.repository;


import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpeciesImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing IdentifiedPlant entities
 */
@Repository
public interface IdentifiedPlantRepository extends CrudRepository<IdentifiedPlant, Long> {
    /**
     * Retrieves a list of all identified plants stored in the database
     *
     * @return list of identified plants from the database
     */
    Page<IdentifiedPlant> findAll(Pageable pageable);

    /**
     * Retrieves an identified plant by id
     * @param id id to retrieve
     * @return IdentifiedPlant object
     */
    IdentifiedPlant findById(long id);

    /**
     * Custom query to retrieve identified plants by gardener id and species name, with pagination
     *
     * @param gardenerId the id of the gardener
     * @param speciesName the name of the plant species
     * @param pageable object representing pagination
     * @return the identified plants matching the species name that the gardener has
     */
    @Query(value = "SELECT *  FROM identifiedplant p WHERE p.gardener_id = :gardenerId AND p.species_scientific_name_without_author = :speciesName", nativeQuery = true)
    Page<IdentifiedPlant> getPlantByGardenerIdAndSpecies(long gardenerId, String speciesName, Pageable pageable);

    /**
     * Custom query to retrieve identified plants by gardener id
     *
     * @param gardenerId the id of the gardener
     * @return the identified plants that the gardener has
     */
    @Query(value = "SELECT *  FROM identifiedplant p WHERE p.gardener_id = :gardenerId", nativeQuery = true)
    List<IdentifiedPlant> getPlantByGardenerId(long gardenerId);

    /**
     * Custom query to retrieve species by gardener id, with pagination
     *
     * @param id the id of the gardener
     * @param pageable object representing pagination
     * @return a page of species with count, species name, and image URL
     */

    @Query(value = "SELECT p.species_scientific_name_without_author AS speciesName, " +
            "p.image_url AS imageUrl, COUNT(*) AS count " +
            "FROM identifiedplant p " +
            "JOIN gardener g ON p.gardener_id = g.gardener_id " +
            "WHERE g.gardener_id = :id " +
            "GROUP BY p.species_scientific_name_without_author, p.image_url",
            nativeQuery = true)

    Page<IdentifiedPlantSpeciesImpl> getSpeciesByGardenerId(long id, Pageable pageable);

    /**
     * Gets all the plant names for Identified plant in the database
     * @return all the plant names in the database
     */
    @Query(value = "SELECT DISTINCT name FROM identifiedplant WHERE gardener_id = :gardenerId", nativeQuery = true)
    List<String> getAllPlantNames(Long gardenerId);

    /**
     * Gets all the scientific names for Identified plant in the database
     * @return all the scientific names for Identified plant in the database
     */
    @Query(value = "SELECT DISTINCT species_scientific_name_without_author FROM identifiedplant WHERE gardener_id = :gardenerId", nativeQuery = true)
    List<String> getAllSpeciesScientificName(Long gardenerId);

    /**
     * Gets the plant details according to plant name
     * @param name the plant name to search
     * @return the plant details in the database
     */
    @Query(value = "SELECT * FROM identifiedplant WHERE name = :name" , nativeQuery = true)
    List<IdentifiedPlant> getPlantDetailsWithPlantNames(String name);

    /**
     * Gets the plant details according to Species Scientific Plant name
     * @param name the specie scientific name to search
     * @return the plant details in the database
     */
    @Query(value = "SELECT * FROM identifiedplant WHERE species_scientific_name_without_author = :name", nativeQuery = true)
    List<IdentifiedPlant> getPlantDetailsWithSpeciesScientificName(String name);

}