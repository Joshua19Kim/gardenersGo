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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    private final RelationshipService relationshipService;
    private Authentication authentication;
    private final AuthenticationManager authenticationManager;

    private List<Gardener> noExistingRelationship;

    private Gardener gardener;

    @Autowired
    public ManageFriendsController(GardenerFormService gardenerFormService, SearchService searchService, RelationshipService relationshipService, AuthenticationManager authenticationManager) {
        this.gardenerFormService = gardenerFormService;
        this.searchService = searchService;
        this.relationshipService = relationshipService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Get lists of all types of relationships with the current logged-in user. This includes relationships with the status
     * accepted, pending, incoming and declined. These lists are also used to decrease the current available search pool
     * in order to prevent the user for searching and sending requests to other users they have an existing relationship
     * with
     * @param searchGardener
     * @param model
     * @return
     */
    @GetMapping("/manageFriends")
    public String getManageFriends(@RequestParam(name = "searchGardeners", required = false, defaultValue = "") String searchGardener,
                                   Model model) {

        logger.info("GET /manageFriends");

        String currentUserEmail = authentication.getPrincipal().toString();
        Optional<Gardener> currentUser = gardenerFormService.findByEmail(currentUserEmail);

        List<Gardener> allCurrentUserRelationships = relationshipService.getCurrentUserRelationships(currentUser.get().getId());
        List<Gardener> allCurrentUserPending = relationshipService.getGardenerPending(currentUser.get().getId());
        List<Gardener> allCurrentUserIncoming= relationshipService.getGardenerIncoming(currentUser.get().getId());
        List<Gardener> allCurrentUserDeclinedRequests = relationshipService.getGardenerDeclinedRequests(currentUser.get().getId());

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

        return "/manageFriends";
    }

    /**
     * Upon post submission, the variable noExistingRelationship is looped through as this is a shorter list with the only
     * viable search results
     * @param searchQuery what user submits in search bar
     * @param friends the current friends that user has
     * @param pending requests sent by the user
     * @param incoming requests received by the user
     * @param declined requests, that were declined by either user or the "friend"
     * @param model
     * @return
     */
    @PostMapping("/manageFriends")
    public String handleFormSubmission(@RequestParam(name = "searchQuery") String searchQuery,
                                       @RequestParam(name = "friends") String friends,
                                       @RequestParam(name = "pending") String pending,
                                       @RequestParam(name = "incoming") String incoming,
                                       @RequestParam(name = "declined") String declined,
                                       Model model) {

        List<Gardener> searchResults = new ArrayList<>();
        for (Gardener gardener : noExistingRelationship) {
            String nameMatch = "";
            if (gardener.getLastName() == null) {
                nameMatch = gardener.getFirstName();
            } else {
                nameMatch = gardener.getFirstName() + " " + gardener.getLastName();
            }
            if (nameMatch == searchQuery || gardener.getEmail() == searchQuery) {
                searchResults.add(gardener);
            }
        }

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("friends", friends);
        model.addAttribute("pending", pending);
        model.addAttribute("incoming", incoming);
        model.addAttribute("declined", declined);

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