package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardensController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.MainPageLayout;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class responsible for handling main-page related HTTP requests
 */
@Controller
public class MainPageController {

    Logger logger = LoggerFactory.getLogger(GardensController.class);
    private final GardenService gardenService;
    private final PlantService plantService;
    private final GardenerFormService gardenerFormService;
    private final RelationshipService relationshipService;
    private final RequestService requestService;
    private final GardenVisitService gardenVisitService;
    private final MainPageLayoutService mainPageLayoutService;
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
            PlantService plantService,
            GardenerFormService gardenerFormService,
            RelationshipService relationshipService,
            RequestService requestService,
            GardenVisitService gardenVisitService,
            MainPageLayoutService mainPageLayoutService) {
        this.gardenService = gardenService;
        this.plantService = plantService;
        this.gardenerFormService = gardenerFormService;
        this.relationshipService = relationshipService;
        this.requestService = requestService;
        this.gardenVisitService = gardenVisitService;
        this.mainPageLayoutService = mainPageLayoutService;
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

        List<Garden> recentGardens = gardenVisitService.findRecentGardensByGardenerId(gardener.getId());
        List<Plant> newPlants = plantService.findNewestPlantsByGardenerId(gardener.getId());
        List<Gardener> friends = relationshipService.getCurrentUserRelationships(gardener.getId());
        MainPageLayout mainPageLayout = mainPageLayoutService.getLayoutByGardenerId(gardener.getId());

        model.addAttribute("gardener", gardener);
        model.addAttribute("friends", friends);
        model.addAttribute("newestPlants", newPlants);
        model.addAttribute("recentGardens", recentGardens);
        model.addAttribute("requestURI", requestService.getRequestURI(request));
        model.addAttribute("mainPageLayout", mainPageLayout);
        model.addAttribute("ordering", mainPageLayout.getFormat());

        // this returning value is like "1 1 1 1" each number indicates the widgets on main page.
        // The order is [recentlyAccessedGardens, newestPlantsList, myGardensList, friendsList]
        // If numbers are all 1, that means there will be all the widgets.
        // "1 0 0 0" means showing only recentlyAccessedGardens.
        String widgetsEnabled = mainPageLayout.getWidgetsEnabled();

        String[] values = widgetsEnabled.split(" ");

        model.addAttribute("recentlyAccessedGardens", values[0].equals("1"));
        model.addAttribute("newestPlantsList", values[1].equals("1"));
        model.addAttribute("myGardensList", values[2].equals("1"));
        model.addAttribute("friendsList", values[3].equals("1"));


        return "mainPageTemplate";
    }

    /**
     * Handles GET requests to the "/home/edit" URL and returns the main page view template
     *
     * @param model the model for passing attributes to the view
     * @param request the request used to find the current URI
     * @param response the response used to set headers
     * @return mainPageEditForm
     */
    @GetMapping("/home/edit")
    public String getMainPageEditForm(
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        logger.info("GET /home/edit");
        // Prevent caching of the page so that we always reload it when we reach it (mainly for when you
        // use the browser back button)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        MainPageLayout mainPageLayout = mainPageLayoutService.getLayoutByGardenerId(gardener.getId());

        model.addAttribute("gardener", gardener);
        model.addAttribute("requestURI", requestService.getRequestURI(request));
        model.addAttribute("mainPageLayout", mainPageLayout);

        return "mainPageEditForm";
    }

    /**
     * This endpoint changes and stores the updated homepage layout for a user
     * @param layout is the layout of the page (i.e. "1 2 3" represents the default layout)
     * @return a redirect to the user page
     */
    @PostMapping("/changeLayout")
    public String changeLayout(@RequestParam("layout") int layout,
                               @RequestParam("customiseSections") List<String> customiseSections) {
        logger.info(String.valueOf(layout));
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        MainPageLayout mainPageLayout = mainPageLayoutService.getLayoutByGardenerId(gardener.getId());
        String format = switch (layout) {
            case 1 -> "1 2 3";
            case 2 -> "2 1 3";
            case 3 -> "3 1 2";
            case 4 -> "3 2 1";
            default -> throw new IllegalArgumentException("Invalid format specified");
        };
        mainPageLayout.setFormat(format);

        List<Boolean> selectionList = new ArrayList<>();

        selectionList.add(customiseSections.contains("recentlyAccessedGardens"));
        selectionList.add(customiseSections.contains("newestPlants"));
        selectionList.add(customiseSections.contains("myGardensList"));
        selectionList.add(customiseSections.contains("friendsList"));

        String selectionString = selectionList.stream()
                .map(b -> b ? "1" : "0")
                .collect(Collectors.joining(" "));
        mainPageLayout.setWidgetsEnabled(selectionString);
        mainPageLayoutService.addMainPageLayout(mainPageLayout);

        return "redirect:/user";
    }

    @PostMapping("/customiseLayout")
    public String changeLayout(@RequestParam(name ="sections" , required = false) List<String> sections) {
        MainPageLayout mainPageLayout = mainPageLayoutService.getLayoutByGardenerId(gardener.getId());

        if (sections == null) {
            mainPageLayout.setWidgetsEnabled("0 0 0 0");
            mainPageLayoutService.addMainPageLayout(mainPageLayout);
            return "redirect:/user";
        }
        List<Boolean> selectionList = new ArrayList<>();

        selectionList.add(sections.contains("recentlyAccessedGardens"));
        selectionList.add(sections.contains("newestPlants"));
        selectionList.add(sections.contains("myGardensList"));
        selectionList.add(sections.contains("friendsList"));


        String selectionString = selectionList.stream()
                .map(b -> b ? "1" : "0")
                .collect(Collectors.joining(" "));

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        mainPageLayout.setWidgetsEnabled(selectionString);
        mainPageLayoutService.addMainPageLayout(mainPageLayout);

        return "redirect:/user";
    }
}
