package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SearchService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Controller
public class ManageFriendsController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;
    private final RelationshipService relationshipService;
    private List<Gardener> noExistingRelationship = new ArrayList<>();

    @Autowired
    public ManageFriendsController(GardenerFormService gardenerFormService, SearchService searchService, RelationshipService relationshipService, AuthenticationManager authenticationManager) {
        this.gardenerFormService = gardenerFormService;
        this.relationshipService = relationshipService;
    }

    /**
     * Get lists of all types of relationships with the current logged-in user. This includes relationships with the status
     * accepted, pending, incoming and declined. These lists are also used to decrease the current available search pool
     * in order to prevent the user for searching and sending requests to other users they have an existing relationship
     * with
     * @param authentication
     * @param model
     * @return manage friend html
     */
    @GetMapping("/manageFriends")
    public String getManageFriends(Authentication authentication, Model model) {

        logger.info("GET /manageFriends");

        String currentUserEmail = authentication.getPrincipal().toString();
        Optional<Gardener> currentUserOptional = gardenerFormService.findByEmail(currentUserEmail);
        if (currentUserOptional.isPresent()) {
            Gardener currentUser = currentUserOptional.get();
            List<Gardener> allCurrentUserRelationships = relationshipService.getCurrentUserRelationships(currentUser.getId());
            List<Gardener> allCurrentUserPending = relationshipService.getGardenerPending(currentUser.getId());
            List<Gardener> allCurrentUserIncoming = relationshipService.getGardenerIncoming(currentUser.getId());
            List<Gardener> allCurrentUserDeclinedRequests = relationshipService.getGardenerDeclinedRequests(currentUser.getId());

            model.addAttribute("friends", allCurrentUserRelationships);
            model.addAttribute("pending", allCurrentUserPending);
            model.addAttribute("incoming", allCurrentUserIncoming);
            model.addAttribute("declined", allCurrentUserDeclinedRequests);

            List<Gardener> allRelationships = new ArrayList<>();
            allRelationships.addAll(allCurrentUserRelationships);
            allRelationships.addAll(allCurrentUserPending);
            allRelationships.addAll(allCurrentUserIncoming);
            allRelationships.addAll(allCurrentUserDeclinedRequests);

            noExistingRelationship = relationshipService.getGardenersWithNoRelationship(allRelationships, gardenerFormService.getGardeners());
            model.addAttribute("searchPool", noExistingRelationship);

        } else {
            logger.info("No user with that email");
        }
        return "/manageFriends";


    }

    /**
     * Upon post submission, the variable noExistingRelationship is looped through as this is a shorter list with the only
     * viable search results
     * @param searchQuery what user submits in search bar
     * @param model
     * @return manage friends html
     */
    @PostMapping("/manageFriends")
    public String handleFormSubmission(@RequestParam(name = "searchQuery", required = false) String searchQuery,
                                       Model model) {

        List<Gardener> searchResults = new ArrayList<>();
        for (Gardener gardener : noExistingRelationship) {
            String nameMatch = "";
            if (gardener.getLastName() == null) {
                nameMatch = gardener.getFirstName();
            } else {
                nameMatch = gardener.getFirstName() + " " + gardener.getLastName();
            }
            if (Objects.equals(nameMatch, searchQuery) || Objects.equals(gardener.getEmail(), searchQuery)) {
                searchResults.add(gardener);
            }
        }

        model.addAttribute("searchResults", searchResults);

        return "redirect:/manageFriends";
    }

    @PostMapping("/manageRequest")
    public String manageFriend(@RequestParam (name = "friendId") Long friendId,
                               @RequestParam (name = "status") String status) {
        String currentEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Gardener> currentGardener = gardenerFormService.findByEmail(currentEmail);
        if (Objects.equals(status, "pending")) {
            currentGardener.ifPresent(value -> relationshipService.updateRelationshipStatus(status, value.getId(), friendId));
        } else {
            currentGardener.ifPresent(value -> relationshipService.updateRelationshipStatus(status, friendId, value.getId()));
        }

        return "redirect:/manageFriends";
    }


}