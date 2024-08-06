package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.MainPageLayout;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Main page layout repository accessor using Spring's @link{CrudRepository}
 */
@Repository
public interface MainPageLayoutRepository extends CrudRepository<MainPageLayout, Long> {

    /**
     * @param gardenerId find layout for a given gardener id
     * @return the users main page layout
     */

    @Query(value= "select * from main_page_layout where gardener_id = ?1", nativeQuery = true)
    MainPageLayout findByGardenerId(long gardenerId);
}
