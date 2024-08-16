package nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers;

import nz.ac.canterbury.seng302.gardenersgrove.service.PlantInfoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.http.HttpResponse;

public class PlantInfoController {
    private final Logger logger = LoggerFactory.getLogger(PlantInfoController.class);
    private final PlantInfoService plantInfoService;
    private static HttpResponse<String> response;


    public PlantInfoController(PlantInfoService plantInfoService) {
        this.plantInfoService = plantInfoService;

    }

    @GetMapping("/sendPlantRequest")
    public String getData(@RequestParam String query) throws IOException, InterruptedException {
            logger.info("GET /Send Request");
            response = plantInfoService.sendRequest(query);
            return response.body();

    }
}
