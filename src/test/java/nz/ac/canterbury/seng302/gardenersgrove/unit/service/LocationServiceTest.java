package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.service.LocationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        String expectedUrl = "https://us1.locationiq.com/v1/autocomplete?q=" + urlQuery + "&tag=place%3Ahouse,building%3Ahouse,building%3Ayes&key=" + apiKey;
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

    @Test
    public void getLocationFromLatLonTest() throws IOException, InterruptedException {
        String latitude = "50";
        String longitude = "20";
        String expectedLocation = "1600 Amphitheatre Parkway, Mountain View, CA";
        String apiKey = null;
        String expectedUrl = "https://us1.locationiq.com/v1/reverse?key=" + apiKey + "&lat=" + latitude + "&lon=" + longitude + "&format=json";
        String mockResponseBody = "{\"display_name\": \"1600 Amphitheatre Parkway, Mountain View, CA\"}";

        HttpRequest mockRequest = HttpRequest.newBuilder()
                .uri(URI.create(expectedUrl))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String response = locationService.getLocationfromLatLong(latitude, longitude);

        verify(httpClient).send(eq(mockRequest), any(HttpResponse.BodyHandler.class));
        Assertions.assertEquals(expectedLocation, response);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-43.457578, 172.636001, Southland, Southland",
            "-43.457578, 172.636001, Southland Region, Southland",
            "-43.457578, 172.636001, Otago, Otago",
            "-43.457578, 172.636001, Otago Region, Otago",
            "-43.457578, 172.636001, Canterbury, Canterbury",
            "-43.457578, 172.636001, Canterbury Region, Canterbury",
            "-43.457578, 172.636001, West Coast, West Coast",
            "-43.457578, 172.636001, West Coast Region, West Coast",
            "-43.457578, 172.636001, Northland, Northland",
            "-43.457578, 172.636001, Northland Region, Northland",
            "-43.457578, 172.636001, Tasman, Tasman",
            "-43.457578, 172.636001, Tasman Region, Tasman",
            "-43.457578, 172.636001, Waikato, Waikato",
            "-43.457578, 172.636001, Waikato Region, Waikato",
            "-43.457578, 172.636001, Wellington, Wellington",
            "-43.457578, 172.636001, Wellington Region, Wellington",
            "-43.457578, 172.636001, Taranaki, Taranaki",
            "-43.457578, 172.636001, Taranaki Region, Taranaki",
            "-43.457578, 172.636001, Manawatu-Wanganui, Manawatu-Wanganui",
            "-43.457578, 172.636001, Manawatu-Wanganui Region, Manawatu-Wanganui",
            "-43.457578, 172.636001, Marlborough, Marlborough",
            "-43.457578, 172.636001, Marlborough Region, Marlborough",
            "-43.457578, 172.636001, Hawke's Bay, Hawke's Bay",
            "-43.457578, 172.636001, Hawke's Bay Region, Hawke's Bay",
            "-43.457578, 172.636001, Gisborne, Gisborne",
            "-43.457578, 172.636001, Gisborne Region, Gisborne",
            "-43.457578, 172.636001, Bay of Plenty, Bay of Plenty",
            "-43.457578, 172.636001, Bay of Plenty Region, Bay of Plenty",
            "-43.457578, 172.636001, Auckland, Auckland",
            "-43.457578, 172.636001, Auckland Region, Auckland",
            "-43.457578, 172.636001, Nelson, Nelson",
            "-43.457578, 172.636001, Nelson Region, Nelson",
            "-43.457578, 172.636001, Chatham Islands, Chatham Islands",
            "-43.457578, 172.636001, Chatham Islands Region, Chatham Islands",
    })
    void getRegionFromLatLonTest(String latitude, String longitude, String inputRegion, String expectedRegion) throws IOException, InterruptedException {
        String apiKey = null;
        String expectedUrl = "https://us1.locationiq.com/v1/reverse?key=" + apiKey + "&lat=" + latitude + "&lon=" + longitude + "&format=json";
        String mockResponseBody = "{\"address\": {\"state\": \"" + inputRegion + "\", \"country\": \"New Zealand\"}}";

        HttpRequest mockRequest = HttpRequest.newBuilder()
                .uri(URI.create(expectedUrl))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String region = locationService.sendReverseGeocodingRequest(latitude, longitude);

        verify(httpClient).send(eq(mockRequest), any(HttpResponse.BodyHandler.class));
        Assertions.assertEquals(expectedRegion, region);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "-43.457578, 172.636001, Invalid, New Zealand",
            "-43.457578, 172.636001, Invalid Region, New Zealand",
            "-43.457578, 172.636001, Southland, Not New Zealand",
            "-43.457578, 172.636001, Southland Region, Not New Zealand",
            "-43.457578, 172.636001, Southland Invalid, New Zealand",
            "-43.457578, 172.636001, Southland Invalid Region, New Zealand",
            "-43.457578, 172.636001, Southland Southland, New Zealand",
            "-43.457578, 172.636001, Southland Southland Region, New Zealand",
            "-43.457578, 172.636001, Region, New Zealand",
    })
    void getRegionFromInvalidLatLonTest(String latitude, String longitude, String inputRegion, String country) throws IOException, InterruptedException {
        String apiKey = null;
        String expectedUrl = "https://us1.locationiq.com/v1/reverse?key=" + apiKey + "&lat=" + latitude + "&lon=" + longitude + "&format=json";
        String mockResponseBody = "{\"address\": {\"state\": \"" + inputRegion + "\", \"country\": \"" + country + "\"}}";

        HttpRequest mockRequest = HttpRequest.newBuilder()
                .uri(URI.create(expectedUrl))
                .header("accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        String region = locationService.sendReverseGeocodingRequest(latitude, longitude);

        verify(httpClient).send(eq(mockRequest), any(HttpResponse.BodyHandler.class));
        Assertions.assertNull(region);
    }
}
