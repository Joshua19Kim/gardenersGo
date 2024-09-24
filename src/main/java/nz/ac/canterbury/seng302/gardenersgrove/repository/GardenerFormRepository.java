package nz.ac.canterbury.seng302.gardenersgrove.repository;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Gardener repository accessor using Spring's @link{CrudRepository}.
 * These (basic) methods are provided for us without the need to write our own implementations
 */
@Repository
public interface GardenerFormRepository extends CrudRepository<Gardener, Long> {
    Optional<Gardener> findById(long id);

    List<Gardener> findAll();

    Optional<Gardener> findByEmail(String email);

    // The following method is only used when testing the ban functionality
    @Transactional
    @Modifying
    @Query(value = "UPDATE gardener SET ban_expiry_date = ?2 WHERE email = ?1", nativeQuery = true)
    void addBanExpiryDateByEmail(String email, Date banExpiryDate);

}
