package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityFormRepository extends CrudRepository<Authority, Long> {
    Optional<Authority> findById(long id);

    Optional<Authority> findAuthorityByGardener(Gardener gardener);
    List<Authority> findAll();

}
