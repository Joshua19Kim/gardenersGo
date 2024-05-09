package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
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

import java.util.*;


@Controller
public class ManageFriendsController {
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final GardenerFormService gardenerFormService;
    private final GardenService gardenService;
    private final RelationshipService relationshipService;
    private List<Gardener> noExistingRelationship = new ArrayList<>();

    @Autowired
    public ManageFriendsController(GardenerFormService gardenerFormService, GardenService gardenService, SearchService searchService, RelationshipService relationshipService, AuthenticationManager authenticationManager) {
        this.gardenerFormService = gardenerFormService;
        this.gardenService = gardenService;
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
        List<Garden> gardens = new ArrayList<>();

        if (currentUserOptional.isPresent()) {
            Gardener currentUser = currentUserOptional.get();
            gardens = gardenService.getGardensByGardenerId(currentUser.getId());
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

            List<Gardener> allGardeners = gardenerFormService.getGardeners();
            noExistingRelationship = relationshipService.getGardenersWithNoRelationship(allRelationships, allGardeners);

            noExistingRelationship.remove(currentUser); // remove if contains current user so that cannot add themselves

            model.addAttribute("searchPool", noExistingRelationship);

        } else {
            logger.info("No user with that email");
        }
        model.addAttribute("gardens", gardens);
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

    /**
     * Handles all the changes of statuses in relationships such as accepting, declining and sending friend requests
     * @param friendId the id of the pending friend
     * @param status the status to change the relationship to
     * @return Redirects to the manage friends page
     */
    @PostMapping("/manageRequest")
    public String manageFriend(@RequestParam (name = "friendId") Long friendId,
                               @RequestParam (name = "status") String status) {
        String currentEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Gardener> currentGardener = gardenerFormService.findByEmail(currentEmail);
        if (Objects.equals(status, "pending")) {
            if (currentGardener.isPresent()) {
                Relationships relationship = new Relationships(currentGardener.get().getId(), friendId, "pending");
                relationshipService.addRelationship(relationship);
            }
        } else {
            currentGardener.ifPresent(value -> relationshipService.updateRelationshipStatus(status, friendId, value.getId()));
        }

        return "redirect:/manageFriends";
    }

    /**
     * Removes the instance of the relationship between the friend and the currently logged in user
     * @param friendId the id of a friend of the currently logged in user
     * @param model
     * @return Redirects to the manage friends page
     */
    @PostMapping("/removeRelationship")
    public String removeRelationship(@RequestParam (name = "friendId") Long friendId, Model model) {
        String currentEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Gardener> currentGardener = gardenerFormService.findByEmail(currentEmail);
        if(currentGardener.isPresent()) {
            relationshipService.deleteRelationShip(currentGardener.get().getId(), friendId);
        }
        return "redirect:/manageFriends";
    }


}