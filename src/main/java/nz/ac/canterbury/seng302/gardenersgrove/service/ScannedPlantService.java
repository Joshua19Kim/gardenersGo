package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.ScannedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ScannedPlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for scanned plants, for interacting with the repository
 */
@Service
public class ScannedPlantService {

    private ScannedPlantRepository scannedPlantRepository;

    public ScannedPlantService(ScannedPlantRepository scannedPlantRepository) {
        this.scannedPlantRepository = scannedPlantRepository;
    }

    /**
     * @return a list of all scanned plants in the repository
     */
    public List<ScannedPlant> getAllScannedPlant() {
        return scannedPlantRepository.findAll();
    }

    /**
     * @param scannedPlant the new scanned plant that is to be added to the repository
     * @return the scanned plant
     */
    public ScannedPlant addScannedPlant(ScannedPlant scannedPlant) {
        return scannedPlantRepository.save(scannedPlant);
    }

    /**
     * @param id the id of the plant that is being retrieved
     * @return an optional scanned plant
     */
    public Optional<ScannedPlant> getScannedPlant(Long id) {return scannedPlantRepository.findById(id);}

}
