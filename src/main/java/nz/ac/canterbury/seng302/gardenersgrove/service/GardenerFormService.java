package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for FormResults, defined by the @link{Service} annotation.
 * This class links automatically with @link{FormRepository}, see the @link{Autowired} annotation below
 */
@Service
public class GardenerFormService {
    private GardenerFormRepository gardenerFormRepository;
    private LostPasswordTokenRepository lostPasswordTokenRepository;

    @Autowired
    public GardenerFormService(GardenerFormRepository gardenerFormRepository,
                               LostPasswordTokenRepository lostPasswordTokenRepository) {
        this.gardenerFormRepository = gardenerFormRepository;
        this.lostPasswordTokenRepository = lostPasswordTokenRepository;
    }
    /**
     * Gets all FormResults from persistence
     * @return all FormResults currently saved in persistence
     */
    public List<Gardener> getGardeners() {
        return gardenerFormRepository.findAll();
    }

    /**
     * Adds a formResult to persistence
     * @param gardener object to persist
     * @return the saved formResult object
     */
    public Gardener addGardener(Gardener gardener) {
        return gardenerFormRepository.save(gardener);
    }

    public Optional<Gardener> findById(long id) {
        return gardenerFormRepository.findById(id);
    }

    public Optional<Gardener> findByEmail(String email) {
        return gardenerFormRepository.findByEmail(email);
    }

    public Optional<Gardener> getUserByEmailAndPassword(String email, int password) {
        return gardenerFormRepository.findByEmailAndPassword(email, password); // Creating some sort of thread problem?
    }

    public void createLostPasswordTokenForGardener(Gardener gardener, String token) {
        LostPasswordToken generatedToken = new LostPasswordToken(token, gardener);
        lostPasswordTokenRepository.save(generatedToken);
    }
}
