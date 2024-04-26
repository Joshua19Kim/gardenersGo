package nz.ac.canterbury.seng302.gardenersgrove.repository;

import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LostPasswordTokenRepository extends CrudRepository<LostPasswordToken, Long> {
    Optional<LostPasswordToken> findByToken(String token);
}
