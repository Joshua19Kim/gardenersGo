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

    private Gardener gardener;

    @Autowired
    public ManageFriendsController(GardenerFormService gardenerFormService, SearchService searchService, RelationshipService relationshipService, AuthenticationManager authenticationManager) {
        this.gardenerFormService = gardenerFormService;
        this.searchService = searchService;
        this.relationshipService = relationshipService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/manageFriends")
    public String getManageFriends(@RequestParam(name = "searchGardeners", required = false, defaultValue = "") String searchGardener,
                                   Model model) {

        logger.info("GET /manageFriends");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: " + authentication);

        String currentUserEmail = authentication.getPrincipal().toString();
        Optional<Gardener> currentUser = gardenerFormService.findByEmail(currentUserEmail);

        List<Gardener> currentUserRelationships = relationshipService.getCurrentUserRelationships(currentUser.get().getId());
        for (Gardener user : currentUserRelationships) {
            logger.info("+++ " + user);
        }

        model.addAttribute("currentUserRelationships", currentUserRelationships);


        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Optional<Gardener> searchResults = searchService.searchGardenersByEmail(searchGardener);
            model.addAttribute("searchResults", searchResults);
            return "manageFriends";
        }
        return "redirect:/login";
    }

    @PostMapping("/manageFriends")
    public String handleFormSubmission(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getPrincipal().toString();

        Authentication authenticationTest = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authenticationTest.getName();
        Gardener gardenerOptional = gardenerFormService.findByEmail(currentUserEmail).get();

        String searchQuery = request.getParameter("searchQuery");
        String email = request.getParameter("email");
        String friendIdStr = request.getParameter("friendId");

        String existingRelationshipErrorMessage = "test";
        if (email != null && !email.isEmpty() && friendIdStr != null && !friendIdStr.isEmpty()) {
            logger.info("Friend added email: " + email);
            long currentUserId = searchService.searchGardenersByEmail(currentUser).get().getId();
            long friendId = Long.parseLong(friendIdStr);

            Relationships relationships = new Relationships(currentUserId, friendId, "pending");
            if (!relationshipService.relationshipExists(currentUserId, friendId)) {
                relationshipService.addRelationship(relationships);
                logger.info(relationships.toString());
                existingRelationshipErrorMessage = "Relationship with user already exists";

            } else {
                logger.info("Relationship already in database");
                existingRelationshipErrorMessage = "Relationship with user already exists";
                logger.info(existingRelationshipErrorMessage);
            }

        }


        if (searchQuery != null && !searchQuery.isEmpty()) {
            logger.info("POST /manageFriends");
            logger.info("Search query is: " + searchQuery);

            InputValidationService inputValidator = new InputValidationService(gardenerFormService);
            List<Gardener> foundGardeners = searchService.searchGardenersByFullName(searchQuery);
            logger.info(foundGardeners.toString());
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
            model.addAttribute("existingRelationshipErrorMessage", existingRelationshipErrorMessage);
            model.addAttribute("foundGardeners", foundGardeners);

        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(gardenerOptional.getEmail(), gardenerOptional.getPassword(), gardenerOptional.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication((newAuth));



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