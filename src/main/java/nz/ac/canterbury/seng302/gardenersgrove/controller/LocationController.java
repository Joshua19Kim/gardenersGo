package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController {
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);
    @Autowired
    LocationService locationService;

    @GetMapping("/sendRequest")
    public String getData(String query) throws IOException, InterruptedException {
        logger.info("GET /Send Request");
        HttpResponse<String> result = locationService.sendRequest(query);
        return result.body();
    }

}
