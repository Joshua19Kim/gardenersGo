package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class ManageFriendsController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;
    private final SearchService searchService;
    private Authentication authentication;
    private Gardener gardener;

    @Autowired
    public ManageFriendsController(GardenerFormService gardenerFormService, SearchService searchService) {
        this.gardenerFormService = gardenerFormService;
        this.searchService = searchService;
    }

    @GetMapping("/manageFriends")
    public String getManageFriends(@RequestParam(name = "searchGardeners", required = false, defaultValue = "") String searchGardener,
                                   Model model) {

        logger.info("GET /manageFriends");
        authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: " + authentication);
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Optional<Gardener> searchResults = searchService.searchGardenersByEmail(searchGardener);
            model.addAttribute("searchResults", searchResults);
            return "manageFriends";
        }
        return "redirect:/login";
    }

    @PostMapping("/manageFriends")
    public String searchGardeners(@RequestParam(name="searchGardeners", required = false, defaultValue = "") String searchQuery,
                                  Model model) {
        logger.info("POST /manageFriends");
        logger.info("Search query is: " + searchQuery);
        
        model.addAttribute("searchQuery", searchQuery);
        InputValidationService inputValidator = new InputValidationService(gardenerFormService);

        List<Gardener> gardenerList = searchService.searchGardenersByFullName(searchQuery);
        String searchQueryMessage = "";
        Optional<Gardener> foundGardener = null;
        if (inputValidator.checkValidEmail(searchQuery).isEmpty()) {
            searchQueryMessage = "Search results for: " + searchQuery;
            foundGardener = searchService.searchGardenersByEmail(searchQuery);
        } else if (!gardenerList.isEmpty()) {
            searchQueryMessage = "Search results for: " + searchQuery;
        } else {
            searchQueryMessage = "Nobody with that name or email in Gardener’s Grove";

        }
        model.addAttribute("userSearchQuery", searchQueryMessage);
        model.addAttribute("foundGardener", foundGardener);
        model.addAttribute("foundGardeners", gardenerList);


        return "/manageFriends";
    }



    @GetMapping("/redirectToManageFriendsPage")
    public RedirectView profileButton() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: " + authentication);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return new RedirectView("/manageFriends");
        }
        return new RedirectView("/login");
    }


}