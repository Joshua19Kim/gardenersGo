package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SearchService {

    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormRepository gardenerFormRepository;

    @Autowired
    public SearchService(GardenerFormRepository gardenerFormRepository) {
        this.gardenerFormRepository = gardenerFormRepository;
    }

    public Optional<Gardener> searchGardeners(String searchQuery) {
        logger.info(gardenerFormRepository.findByEmail(searchQuery).toString());
        return gardenerFormRepository.findByEmail(searchQuery);
    }
}
