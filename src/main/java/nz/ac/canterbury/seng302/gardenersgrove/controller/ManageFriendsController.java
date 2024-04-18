package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SearchService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
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

import java.util.List;
import java.util.Optional;


@Controller
public class ManageFriendsController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;
    private final SearchService searchService;
    private final RelationshipService relationshipService;
    private Authentication authentication;
    private Gardener gardener;

    @Autowired
    public ManageFriendsController(GardenerFormService gardenerFormService, SearchService searchService, RelationshipService relationshipService) {
        this.gardenerFormService = gardenerFormService;
        this.searchService = searchService;
        this.relationshipService = relationshipService;
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
    public String handleFormSubmission(HttpServletRequest request,
                                       @RequestParam(name="searchGardeners", required = false, defaultValue = "") String searchQuery,
                                       @RequestParam(name="email", required = false) String email,
                                       @RequestParam(name="friendId", required = false) Integer friendId,
                                       Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getPrincipal().toString();

        logger.info("^^^" + email);

        if (email != null && !email.isEmpty()) {
            logger.info("Friend added email: " + email);
            long currentUserIdLong = searchService.searchGardenersByEmail(currentUser).get().getId();
            int currentUserId = (int) currentUserIdLong;
            Relationships relationships = new Relationships(currentUserId, friendId, "pending");
            relationshipService.addRelationship(relationships);
            logger.info(relationships.toString());
        }
        logger.info("POST /manageFriends");
        logger.info("Search query is: " + searchQuery);



        InputValidationService inputValidator = new InputValidationService(gardenerFormService);
        List<Gardener> foundGardeners = searchService.searchGardenersByFullName(searchQuery);
        String emptySearchQueryMessage = "";
        if (inputValidator.checkValidEmail(searchQuery).isEmpty()) {
            Optional<Gardener> foundGardener = searchService.searchGardenersByEmail(searchQuery);
            foundGardener.ifPresent(foundGardeners::add);
        }
        if (foundGardeners.isEmpty()) {
            emptySearchQueryMessage = "Nobody with that name or email in Gardener’s Grove";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("emptySearchQueryMessage", emptySearchQueryMessage);
        model.addAttribute("foundGardeners", foundGardeners);



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