package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ManageFriendsController.class)

public class ManageFriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelationshipService relationshipService;

    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private RequestService requestService;

    @MockBean
    private GardenService gardenService; // is needed

    @MockBean
    private SearchService searchService; // is needed

    @MockBean
    private AuthenticationManager authenticationManager; // is needed


    @Test
    @WithMockUser
    public void testGetManageFriends() throws Exception {
        LocalDate date = LocalDate.of(2000, 1, 1);
        Gardener currentUser = new Gardener("test", "user", date, "test@test.com", "Password1!");
        currentUser.setId(999L);
        gardenerFormService.addGardener(currentUser);

        // Mock Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(currentUser.getEmail()); // Simulating user's email as principal

        List<Gardener> relationships = new ArrayList<>();
        List<Gardener> pending = new ArrayList<>();
        List<Gardener> incoming = new ArrayList<>();
        List<Gardener> declined = new ArrayList<>();

        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(currentUser));
        when(relationshipService.getCurrentUserRelationships(currentUser.getId())).thenReturn(relationships);
        when(relationshipService.getGardenerPending(currentUser.getId())).thenReturn(pending);
        when(relationshipService.getGardenerIncoming(currentUser.getId())).thenReturn(incoming);
        when(relationshipService.getGardenerDeclinedRequests(currentUser.getId())).thenReturn(declined);

        mockMvc.perform(MockMvcRequestBuilders.get("/manageFriends")
                        .with(csrf())
                        .principal(authentication)) // Pass the mock Authentication object
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("friends"))
                .andExpect(model().attribute("friends", relationships))
                .andExpect(model().attribute("pending", pending))
                .andExpect(model().attribute("incoming", incoming))
                .andExpect(model().attribute("declined", declined))
                .andExpect(view().name("manageFriends"));

    }

}
