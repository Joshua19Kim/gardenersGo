package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
     *
     * @param gardener object to persist
     */
    public Gardener addGardener(Gardener gardener) {
        return gardenerFormRepository.save(gardener);
    }
    public void removeGardener(Gardener gardener) {
        gardenerFormRepository.delete(gardener);
    }

    public Optional<Gardener> findById(long id) {
        return gardenerFormRepository.findById(id);
    }

    public Optional<Gardener> findByEmail(String email) {
        return gardenerFormRepository.findByEmail(email);
    }

    public Optional<Gardener> getUserByEmailAndPassword(String email, String password) {
        Optional<Gardener> gardener = gardenerFormRepository.findByEmail(email);
        if (gardener.isPresent()) {
            if (gardener.get().comparePassword(password)) {
                return gardener;
            }
        }
        return Optional.empty();
    }

    public List<Gardener> getGardenersById (List<Long> ids) {
        List<Gardener> allGardeners = new ArrayList<>();
        for (long id : ids) {
            allGardeners.add(findById(id).get());
        }
        return allGardeners;
    }


}
