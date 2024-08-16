package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantSpeciesRepository;
import org.h2.table.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantSpeciesService {

     private PlantSpeciesRepository plantSpeciesRepository;

    public PlantSpeciesService(PlantSpeciesRepository plantSpeciesRepository) {
        this.plantSpeciesRepository = plantSpeciesRepository;
    }


    public List<PlantSpecies> getAllPlantSpecies() {
        return plantSpeciesRepository.findAll();
    }


    public PlantSpecies addPlantSpecies(PlantSpecies plantSpecies) {
        return plantSpeciesRepository.save(plantSpecies);
    }

    public Optional<PlantSpecies> getPlantSpecies(Long id) {return plantSpeciesRepository.findById(id);}

    public Page<PlantSpecies> getAllPlantSpeciesPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return plantSpeciesRepository.findAll(pageable);
    }

}
