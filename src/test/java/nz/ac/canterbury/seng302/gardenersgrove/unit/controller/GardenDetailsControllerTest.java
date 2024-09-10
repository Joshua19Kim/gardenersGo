package nz.ac.canterbury.seng302.gardenersgrove.unit.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenDetailsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

public class GardenDetailsControllerTest {

    private GardenDetailsController gardenDetailsController;
    private GardenService mockGardenService;
    private GardenerFormService mockGardenerFormService;
    private RelationshipService mockRelationshipService;
    private RequestService requestService;
    private WeatherService mockWeatherService;
    private TagService mockTagService;
    private LocationService mockLocationService;
    private GardenVisitService mockGardenVisitService;
    private FollowerService mockFollowerService;
    private Model mockModel;
    private Gardener mockUser;
    private Gardener mockOwner;
    private Authentication authentication;
    private HttpServletRequest mockRequest;
    private Garden mockGarden;
    private List<Garden> mockGardens;


    @BeforeEach
    public void setUp() {
        // http mocks
        mockRequest = Mockito.mock(HttpServletRequest.class);
        requestService = Mockito.mock(RequestService.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("");

        // Service/ Controller mocks
        mockGardenerFormService = Mockito.mock(GardenerFormService.class);
        mockGardenService = Mockito.mock(GardenService.class);
        mockRelationshipService = Mockito.mock(RelationshipService.class);
        mockWeatherService = Mockito.mock(WeatherService.class);
        mockTagService = Mockito.mock(TagService.class);
        mockLocationService = Mockito.mock(LocationService.class);
        mockGardenVisitService = Mockito.mock(GardenVisitService.class);
        mockFollowerService = Mockito.mock(FollowerService.class);

        gardenDetailsController = new GardenDetailsController(mockGardenService, mockGardenerFormService,
                mockRelationshipService, requestService, mockWeatherService, mockTagService,
                mockLocationService, mockGardenVisitService, mockFollowerService);

        mockModel = Mockito.mock(Model.class);

        mockGarden = Mockito.mock(Garden.class);
        mockGardens = Mockito.mock(ArrayList.class);
        mockOwner = Mockito.mock(Gardener.class);

        // Gardener Security Mock
        mockUser = Mockito.mock(Gardener.class);
        mockUser.setEmail("testEmail@test.test");
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(authentication.getName()).thenReturn("testEmail@test.test");
        Mockito.when(mockGardenerFormService.findByEmail(any(String.class))).thenReturn(Optional.of(mockUser));
    }

    // Tests for gardens/details GET method

    @Test
    void GivenGardenDoesNotExist_WhenDetailsRequested_ControllerRedirects()
            throws IOException, URISyntaxException, InterruptedException {
        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.empty());
        String template = gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
        Mockito.verify(mockGardenService, times(0)).addGarden(any(Garden.class));
        Assertions.assertEquals("redirect:/gardens", template);
    }

    @Test
    void GivenGardenIDIsNull_WhenDetailsRequested_ControllerRedirects()
            throws IOException, URISyntaxException, InterruptedException {
        String template = gardenDetailsController.gardenDetails(null, null, null, null, mockModel, mockRequest);
        Mockito.verify(mockGardenService, times(0)).getGarden(any(long.class));
        Assertions.assertEquals("redirect:/gardens", template);
    }

    @Test
    void GivenGardenExistsButUserIsNotFriendsWithOwner_WhenDetailsRequested_ControllerRedirects()
            throws IOException, URISyntaxException, InterruptedException {
        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.of(mockGarden));
        // Garden owner
        Mockito.when(mockGarden.getGardener()).thenReturn(mockOwner);
        Mockito.when(mockOwner.getId()).thenReturn(1L);
        Mockito.when(mockUser.getId()).thenReturn(2L);
        // Friend check
        List<Gardener> mockFriends = Mockito.mock(ArrayList.class);
        Mockito.when(mockRelationshipService.getCurrentUserRelationships(anyLong()))
                .thenReturn(mockFriends);
        Mockito.when(mockFriends.contains(any(Gardener.class))).thenReturn(false);

        String template = gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
        Mockito.verify(mockRelationshipService, times(1)).getCurrentUserRelationships(any(long.class));
        Assertions.assertEquals("redirect:/gardens", template);
    }

    @Test
    void GivenGardenExistsAndUserIsFriendsWithOwner_WhenDetailsRequested_ControllerReturnsUnauthorizedTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.of(mockGarden));
        // Garden owner
        Mockito.when(mockGarden.getGardener()).thenReturn(mockOwner);
        Mockito.when(mockOwner.getId()).thenReturn(1L);
        Mockito.when(mockUser.getId()).thenReturn(2L);
        // Friend check
        List<Gardener> mockFriends = Mockito.mock(ArrayList.class);
        Mockito.when(mockRelationshipService.getCurrentUserRelationships(anyLong()))
                .thenReturn(mockFriends);
        Mockito.when(mockFriends.contains(any(Gardener.class))).thenReturn(true);

        String template = gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
        Assertions.assertEquals("unauthorizedGardenDetailsTemplate", template);
    }

    @Test
    void GivenUserOwnsGardenAndThereIsALocationError_WhenDetailsRequested_ControllerReturnsDetailsTemplate()
                throws IOException, URISyntaxException, InterruptedException {
        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.of(mockGarden));
        // Garden owner
        Mockito.when(mockGarden.getGardener()).thenReturn(mockOwner);
        Mockito.when(mockOwner.getId()).thenReturn(1L);
        Mockito.when(mockUser.getId()).thenReturn(1L);
        // Location
        HttpResponse<String> mockLocation = Mockito.mock(HttpResponse.class);
        Mockito.when(mockLocationService.sendRequest(anyString())).thenReturn(mockLocation);
        Mockito.when(mockLocation.body()).thenReturn("error");

        String template = gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
        Mockito.verify(mockWeatherService, times(0)).getWeather(anyString());
        Mockito.verify(mockWeatherService, times(0)).getPrevWeather(anyString());
        Assertions.assertEquals("gardenDetailsTemplate", template);
    }

    @Test
    void GivenGardenExistsAndHasAFollower_WhenDetailsRequested_ControllerReturnsDetailsTemplate() throws IOException, URISyntaxException, InterruptedException {
        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.of(mockGarden));
        Mockito.when(mockGarden.getGardener()).thenReturn(mockOwner);
        Mockito.when(mockOwner.getId()).thenReturn(1L);
        Mockito.when(mockUser.getId()).thenReturn(1L);
        HttpResponse<String> mockLocation = Mockito.mock(HttpResponse.class);
        Mockito.when(mockLocationService.sendRequest(anyString())).thenReturn(mockLocation);
        Mockito.when(mockLocation.body()).thenReturn("error");
        Follower mockFollower = new Follower(2L, 1L);
        List<Follower> mockFollowerList = List.of(mockFollower);
        Mockito.when(mockFollowerService.findFollowing(mockUser.getId())).thenReturn(mockFollowerList);
        String template = gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
        Assertions.assertEquals("gardenDetailsTemplate", template);
        Mockito.verify(mockFollowerService, times(1)).findFollowing(anyLong());
    }

    // Tests for gardens/addTag POST method -- These tests are not bad as the controller is not failsafe secure
    // todo: Fix this test and add others after refactoring GardenDetails

//    @Test
//    void GivenUserOwnsGarden_WhenDetailsRequested_ControllerFindsDetails()
//            throws IOException, URISyntaxException, InterruptedException {
//        String tag = "MyNewTag";
//        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.of(mockGarden));
//        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
//        // tag validation -- NEED TO INJECT TAG VALIDATION
////        Mockito.when(mockTagValidation.validate)
//        // Location (bad location)
//        HttpResponse<String> mockLocation = Mockito.mock(HttpResponse.class);
//        Mockito.when(mockLocationService.sendRequest(anyString())).thenReturn(mockLocation);
//        Mockito.when(mockLocation.body()).thenReturn(null);
//
//        String template = gardenDetailsController.addTag(tag, 1l, mockModel);
//        Assertions.assertEquals("gardenDetailsTemplate", template);
//    }

}
