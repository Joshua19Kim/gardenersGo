package nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.parseLong;

/** Controller class responsible for handling garden-related HTTP requests. */
@Controller
public class GardensController {
    Logger logger = LoggerFactory.getLogger(GardensController.class);
    private final GardenService gardenService;
    private final FollowerService followerService;
    private final GardenerFormService gardenerFormService;
    private final RelationshipService relationshipService;
    private final RequestService requestService;
    private Gardener gardener;

    /**
     * Constructor used to create a new instance of the GardensController. Autowires a
     * various service objects
     *
     * @param gardenService the garden service used to interact with the database
     * @param gardenerFormService the gardener form service used to interact with the database
     * @param relationshipService the relationship service used to manage gardener relationships
     * @param requestService the request service used to manage HTTP requests
     */
    @Autowired
    public GardensController(
            GardenService gardenService,
            FollowerService followerService,
            GardenerFormService gardenerFormService,
            RelationshipService relationshipService,
            RequestService requestService) {
        this.gardenService = gardenService;
        this.followerService = followerService;
        this.gardenerFormService = gardenerFormService;
        this.relationshipService = relationshipService;
        this.requestService = requestService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication We will always have to
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
     * Gets the home page that displays the list of gardens
     *
     * @param model the model for passing attributes to the view
     * @param request the request used to find the current URI
     * @param response the response used to set headers
     * @return the gardens template which defines the user interface for the 'my gardens' page
     */
    @GetMapping("/gardens")
    public String getGardenHome(
            @RequestParam(name = "user", required = false) String user,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        logger.info("GET /gardens/main");
        // Prevent caching of the page so that we always reload it when we reach it (mainly for when you
        // use the browser back button)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setHeader("Expires", "0"); // Proxies

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        // Getting the gardens that users followed to show on the gardens page
        List<Long> followedGardens = followerService.findAllGardens(gardener.getId());
        List<Garden> followedGardenList = new ArrayList<>();
        for (Long gardenId : followedGardens) {
            Optional<Garden> garden = gardenService.getGarden(gardenId);
            garden.ifPresent(followedGardenList::add);
        }

        model.addAttribute("followedGardenList", followedGardenList);
        if (followedGardenList.isEmpty()) {
            model.addAttribute("errorMessage", "You are not following any gardens yet.");
        }

        List<Garden> gardens;
        if (user == null) {
            gardens = gardenService.getGardensByGardenerId(gardener.getId());
            model.addAttribute("gardener", gardener);
        } else {
            Optional<Gardener> friend = gardenerFormService.findById(parseLong(user, 10));
            if (friend.isPresent()
                    && relationshipService
                    .getCurrentUserRelationships(gardener.getId())
                    .contains(friend.get())) {
                gardens = gardenService.getGardensByGardenerId(parseLong(user, 10));
                model.addAttribute("gardener", friend.get());
                List<Garden> userGardens;
                userGardens = gardenService.getGardensByGardenerId(gardener.getId());
                model.addAttribute("userGardens", userGardens);
            } else {
                return "redirect:/gardens";
            }
        }
        model.addAttribute("gardens", gardens);
        model.addAttribute("requestURI", requestService.getRequestURI(request));
        return "gardensTemplate";
    }
}
