package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService {
    private final GardenerFormRepository gardenerFormRepository;

    @Autowired
    public SearchService(GardenerFormRepository gardenerFormRepository) {
        this.gardenerFormRepository = gardenerFormRepository;
    }

    public Optional<Gardener> searchGardenersByEmail(String searchQuery) {
        return gardenerFormRepository.findByEmail(searchQuery);
    }

    public List<Gardener> searchGardenersByFullName(String searchQuery) {
        List<Gardener> allUsers = gardenerFormRepository.findAll();
        List<Gardener> results = new ArrayList<>();

        for (Gardener gardener  : allUsers) {
            String fullName = "";
            if (gardener.getLastName() == null) {
                fullName = gardener.getFirstName();
            } else {
                fullName = gardener.getFirstName() + " " + gardener.getLastName();

            }
            if (fullName.matches(searchQuery)) {
                results.add(gardener);
            }
        }

        return results;
    }
}
