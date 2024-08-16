package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.ScannedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ScannedPlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScannedPlantService {

    private ScannedPlantRepository scannedPlantRepository;

    public ScannedPlantService(ScannedPlantRepository scannedPlantRepository) {
        this.scannedPlantRepository = scannedPlantRepository;
    }


    public List<ScannedPlant> getAllScannedPlant() {
        return scannedPlantRepository.findAll();
    }


    public ScannedPlant addScannedPlant(ScannedPlant scannedPlant) {
        return scannedPlantRepository.save(scannedPlant);
    }

    public Optional<ScannedPlant> getScannedPlant(Long id) {return scannedPlantRepository.findById(id);}

}
