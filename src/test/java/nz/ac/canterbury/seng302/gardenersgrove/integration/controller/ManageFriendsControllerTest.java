package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ManageFriendsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ManageFriendsController.class)

public class ManageFriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RelationshipService relationshipService;

    private static List<Gardener> testGardeners = new ArrayList<>();
    private static List<Relationships> testRelationships = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        Gardener gardener1 = new Gardener("test", "one", null, "test1@test.nz", "P@ssword1", "default.png");
        Gardener gardener2 = new Gardener("test", "two", null, "test2@test.nz", "P@ssword1", "default.png");
        Gardener gardener3 = new Gardener("test", "three", null, "test3@test.nz", "P@ssword1", "default.png");
        Gardener gardener4 = new Gardener("test", "four", null, "test4@test.nz", "P@ssword1", "default.png");
        Gardener gardener5 = new Gardener("test", "five", null, "test5@test.nz", "P@ssword1", "default.png");
        testGardeners.add(gardener1);
        testGardeners.add(gardener2);
        testGardeners.add(gardener3);
        testGardeners.add(gardener4);
        testGardeners.add(gardener5);

        Relationships relationship1 = new Relationships(1L, 2L, "accepted");
        Relationships relationship2 = new Relationships(1L, 3L, "incoming");
        Relationships relationship3 = new Relationships(1L, 4L, "pending");
        Relationships relationship4 = new Relationships(5L, 1L, "declined");
        testRelationships.add(relationship1);
        testRelationships.add(relationship2);
        testRelationships.add(relationship3);
        testRelationships.add(relationship4);
    }
//
//    @Test
//    @WithMockUser
//    public void


}
