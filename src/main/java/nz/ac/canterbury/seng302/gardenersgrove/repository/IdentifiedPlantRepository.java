package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpeciesImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing IdentifiedPlant entities
 */
@Repository
public interface IdentifiedPlantRepository extends JpaRepository<IdentifiedPlant, Long> {

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
    @Query("SELECT p FROM IdentifiedPlant p WHERE p.gardener.id = :gardenerId AND p.speciesScientificNameWithoutAuthor = :speciesName")
    Page<IdentifiedPlant> getPlantByGardenerIdAndSpecies(@Param("gardenerId") long gardenerId, @Param("speciesName") String speciesName, Pageable pageable);

    /**
     * Custom query to retrieve species by gardener id, with pagination
     *
     * @param id the id of the gardener
     * @param pageable object representing pagination
     * @return a page of species with count, species name, and image URL
     */
    @Query("SELECT new nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpeciesImpl(p.speciesScientificNameWithoutAuthor, p.imageUrl, COUNT(p)) " +
            "FROM IdentifiedPlant p " +
            "WHERE p.gardener.id = :id " +
            "GROUP BY p.speciesScientificNameWithoutAuthor, p.imageUrl")
    Page<IdentifiedPlantSpeciesImpl> getSpeciesByGardenerId(@Param("id") long id, Pageable pageable);

    /**
     * Gets all the plant names for Identified plant in the database
     * @param gardenerId the id of the gardener
     * @return all the plant names in the database
     */
    @Query("SELECT DISTINCT p.name FROM IdentifiedPlant p WHERE p.gardener.id = :gardenerId")
    List<String> getAllPlantNames(@Param("gardenerId") Long gardenerId);

    /**
     * Gets all the scientific names for Identified plant in the database
     * @param gardenerId the id of the gardener
     * @return all the scientific names for Identified plant in the database
     */
    @Query("SELECT DISTINCT p.speciesScientificNameWithoutAuthor FROM IdentifiedPlant p WHERE p.gardener.id = :gardenerId")
    List<String> getAllSpeciesScientificName(@Param("gardenerId") Long gardenerId);

    /**
     * Gets the plant details according to plant name
     * @param name the plant name to search
     * @return the plant details in the database
     */
    @Query("SELECT p FROM IdentifiedPlant p WHERE p.name = :name")
    List<IdentifiedPlant> getPlantDetailsWithPlantNames(@Param("name") String name);

    /**
     * Gets the plant details according to Species Scientific Plant name
     * @param name the specie scientific name to search
     * @return the plant details in the database
     */
    @Query("SELECT p FROM IdentifiedPlant p WHERE p.speciesScientificNameWithoutAuthor = :name")
    List<IdentifiedPlant> getPlantDetailsWithSpeciesScientificName(@Param("name") String name);
}