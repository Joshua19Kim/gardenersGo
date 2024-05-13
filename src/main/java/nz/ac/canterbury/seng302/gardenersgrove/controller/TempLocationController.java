package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * This is temporary controller to test location autocomplete API
 * It is linked with locationAuto page
 */
@Controller
public class TempLocationController {

    private final Logger logger = LoggerFactory.getLogger(TempLocationController.class);

    @GetMapping("/locationAuto")
    public String getLocationForm() {
        logger.info("GET /locationAuto");

        return "/locationAuto";
    }


}
