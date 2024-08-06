package nz.ac.canterbury.seng302.gardenersgrove.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class responsible for handling main-page related HTTP requests
 */
@Controller
public class MainPageController {
    /**
     * Handles GET requests to the "/home" URL and returns the main page view template
     * @return mainPageTemplate
     */
    @GetMapping("/home")
    public String getMainPage() {
        return "mainPageTemplate";
    }
}
