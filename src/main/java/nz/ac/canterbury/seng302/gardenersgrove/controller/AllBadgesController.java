package nz.ac.canterbury.seng302.gardenersgrove.controller;

import java.util.List;
import java.util.Optional;
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

/**
 * The controller for the view all badges page. It handles all requests for going to the page
 */
@Controller
public class AllBadgesController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final BadgeService badgeService;
    private final GardenerFormService gardenerFormService;
    private Gardener gardener;

    /**
     * Constructor for the AllBadgesController. Initializes the required service classes for badge retrieval
     *
     * @param badgeService     The service used to get all badges
     * @param gardenerFormService  The service used to manage gardener information
     */
    public AllBadgesController(BadgeService badgeService, GardenerFormService gardenerFormService) {
        this.badgeService = badgeService;
        this.gardenerFormService = gardenerFormService;
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
     * Retrieves the plant wiki page. If the API call is successful, it displays the plant information.
     * If the API is down, an error message is shown.
     *
     * @param model The model containing attributes to display on the page.
     * @return The plant wiki HTML template.
     */
    @GetMapping("/badges")
    public String viewAllBadges(Model model) {
        logger.info("GET /badges");
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        List<String> lockedBadgeNames = badgeService.getMyLockedBadgeNames(gardener.getId());
        List<Badge> earnedBadges = badgeService.getMyBadges(gardener.getId());

        model.addAttribute("lockedBadgeNames", lockedBadgeNames);
        model.addAttribute("earnedBadges", earnedBadges);

        return "allBadges";
    }
}



