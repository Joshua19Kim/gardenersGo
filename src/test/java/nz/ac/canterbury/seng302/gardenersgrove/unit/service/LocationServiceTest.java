package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LocationServiceTest {

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
        String apiKey = null;
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
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HttpResponse<String> response = locationService.sendRequest(query);

        // Assert
        assertEquals(mockResponseBody, response.body());
        verify(httpClient).send(eq(mockRequest), any(HttpResponse.BodyHandler.class));
    }
}
