package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardensController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Optional;

/**
 * Controller class responsible for handling main-page related HTTP requests
 */
@Controller
public class MainPageController {

    Logger logger = LoggerFactory.getLogger(GardensController.class);
    private final GardenService gardenService;
    private final GardenerFormService gardenerFormService;
    private final RelationshipService relationshipService;
    private final RequestService requestService;
    private final GardenVisitService gardenVisitService;
    private Gardener gardener;

    /**
     * Constructor used to create a new instance of the MainPageController.
     * Autowires various service objects
     *
     * @param gardenService the garden service used to interact with the database
     * @param gardenerFormService the gardener form service used to interact with the database
     * @param relationshipService the relationship service used to manage gardener relationships
     * @param requestService the request service used to manage HTTP requests
     * @param gardenVisitService the garden visit service used to log visits to gardens
     */
    @Autowired
    public MainPageController(
            GardenService gardenService,
            GardenerFormService gardenerFormService,
            RelationshipService relationshipService,
            RequestService requestService, GardenVisitService gardenVisitService) {
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.relationshipService = relationshipService;
        this.requestService = requestService;
        this.gardenVisitService = gardenVisitService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication. We will always have to
     * check whether the gardener was retrieved in the calling method, so the return type was left as
     * an optional
     *
     * @return An optional of the requested gardener
     */
    public Optional<Gardener> getGardenerFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return gardenerFormService.findByEmail(currentUserEmail);
    }

    /**
     * Handles GET requests to the "/home" URL and returns the main page view template
     *
     * @param model the model for passing attributes to the view
     * @param request the request used to find the current URI
     * @param response the response used to set headers
     * @return mainPageTemplate
     */
    @GetMapping("/home")
    public String getMainPage(
          Model model,
          HttpServletRequest request,
          HttpServletResponse response) {

        logger.info("GET /home");
        // Prevent caching of the page so that we always reload it when we reach it (mainly for when you
        // use the browser back button)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        List<Garden> gardens;
        List<Garden> recentGardens;
        gardens = gardenService.getGardensByGardenerId(gardener.getId());
        recentGardens = gardenVisitService.findRecentGardensByGardenerId(gardener.getId());
        model.addAttribute("gardener", gardener);
        model.addAttribute("gardens", gardens);
        model.addAttribute("recentGardens", recentGardens);
        model.addAttribute("requestURI", requestService.getRequestURI(request));

        return "mainPageTemplate";
    }
}
