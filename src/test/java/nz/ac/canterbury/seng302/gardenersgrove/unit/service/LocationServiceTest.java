package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import static org.mockito.Mockito.when;
//import static org.mockito.ArgumentMatchers.any;


import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LocationServiceTest {

//    private LocationService locationService;
//
//    private HttpClient httpClient;
//
//    @BeforeEach
//    public void setUp() {
//        httpClient = Mockito.mock(HttpClient.class);
//        locationService = new LocationService("");
//    }
//
//    @Test
//    public void getLocation_ValidLocation_LocationReturned() throws IOException, InterruptedException {
//        String location = "Colombo street, Christchurch";
//        HttpResponse<String> expectedSuggestions = Mockito.mock(HttpResponse.class);
//        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(expectedSuggestions);
//
//        HttpResponse<String> actualSuggestions = locationService.sendRequest("");
//
//        Assertions.assertEquals(expectedSuggestions, actualSuggestions);
//    }

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private LocationService locationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendRequest() throws IOException, InterruptedException {
        // Arrange
        String query = "1600 Amphitheatre Parkway";
        String apiKey = "test_api_key";
        String urlQuery = query.replace(" ", "%20");
        String expectedUrl = "https://us1.locationiq.com/v1/autocomplete?q=" + urlQuery + "&tag=place%3Ahouse&key=" + apiKey;
        String mockResponseBody = "{\"suggestions\": [\"1600 Amphitheatre Parkway, Mountain View, CA\"]}";

        HttpRequest mockRequest = HttpRequest.newBuilder()
                .uri(URI.create(expectedUrl))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(httpClient.send(eq(mockRequest), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Act
        locationService = new LocationService(apiKey); // Inject the API key
        HttpResponse<String> response = locationService.sendRequest(query);

        // Assert
        assertEquals(mockResponseBody, response.body());
        verify(httpClient).send(eq(mockRequest), any(HttpResponse.BodyHandler.class));
    }
}
