package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.RateLimiterService;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * LocationController is the important link between the page has a location autocomplete function and back-end side.
 * It will retrieve the query value from users and show the result of autocomplete from server.
 */
@RestController
public class LocationController {
    private final Logger logger = LoggerFactory.getLogger(LocationController.class);
    private final RateLimiterService rateLimiterService;

    /**
     * Constructor of LocationController
     *
     * @param rateLimiterService The service providing the function that can limit the amount of requests.
     */
    public LocationController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    /**
     * This function receive the query from HTML which the user entered on the page and pass it to LocationService.
     *
     * @param query query The address query entered by the user.
     * @return The autocomplete result from the server, or a message indicating the search status.
     * @throws IOException          If an I/O error occurs during the request.
     * @throws InterruptedException If the thread is interrupted while waiting for the request.
     */
    @GetMapping("/sendRequest")
    public String getData(@RequestParam String query) throws IOException, InterruptedException {
        rateLimiterService.setQuery(query);
        return rateLimiterService.sendRequest();
    }

}
