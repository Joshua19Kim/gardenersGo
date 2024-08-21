package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PlantWikiServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PlantWikiService plantWikiService;

    @Value("${plantWiki.key}")
    private String apiKey;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        plantWikiService = new PlantWikiService(apiKey, objectMapper);
    }

//    @Test
//    void testSendRequest() throws IOException, InterruptedException {
//        String query = "monstera";
//        String expectedResponse = "{ \"data\": [ { \"id\": 5257, \"common_name\": \"Swiss cheese plant\", \"scientific_name\": [ \"Monstera deliciosa\" ], \"other_name\": [], \"cycle\": \"Upgrade Plans To Premium/Supreme - https://perenual.com/subscription-api-pricing. I'm sorry\", \"watering\": \"Upgrade Plans To Premium/Supreme - https://perenual.com/subscription-api-pricing. I'm sorry\", \"sunlight\": \"Upgrade Plans To Premium/Supreme - https://perenual.com/subscription-api-pricing. I'm sorry\", \"default_image\": { \"license\": 451, \"license_name\": \"CC0 1.0 Universal (CC0 1.0) Public Domain Dedication\", \"license_url\": \"https://creativecommons.org/publicdomain/zero/1.0/\", \"original_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"regular_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"medium_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"small_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"thumbnail\": \"https://perenual.com/storage/image/upgrade_access.jpg\" } }, { \"id\": 5258, \"common_name\": \"variegated Swiss cheese plant\", \"scientific_name\": [ \"Monstera deliciosa 'Variegata'\" ], \"other_name\": [], \"cycle\": \"Upgrade Plans To Premium/Supreme - https://perenual.com/subscription-api-pricing. I'm sorry\", \"watering\": \"Upgrade Plans To Premium/Supreme - https://perenual.com/subscription-api-pricing. I'm sorry\", \"sunlight\": \"Upgrade Plans To Premium/Supreme - https://perenual.com/subscription-api-pricing. I'm sorry\", \"default_image\": { \"license\": 451, \"license_name\": \"CC0 1.0 Universal (CC0 1.0) Public Domain Dedication\", \"license_url\": \"https://creativecommons.org/publicdomain/zero/1.0/\", \"original_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"regular_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"medium_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"small_url\": \"https://perenual.com/storage/image/upgrade_access.jpg\", \"thumbnail\": \"https://perenual.com/storage/image/upgrade_access.jpg\" } } ], \"to\": 2, \"per_page\": 30, \"current_page\": 1, \"from\": 1, \"last_page\": 1, \"total\": 2 }";
//
//        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
//                .thenReturn(mockHttpResponse);
//        when(mockHttpResponse.body()).thenReturn(expectedResponse);
//        when(mockHttpResponse.statusCode()).thenReturn(200);
//
//        HttpResponse<String> response = plantWikiService.sendRequest(query);
//
//        assertEquals(200, response.statusCode());
//        assertEquals(expectedResponse, response.body());
//        System.out.println(response.body());
//        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
//    }
//
//    @Test
//    void testSendRequestThrowsIOException() throws IOException, InterruptedException {
//        String query = "monstera";
//
//        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
//                .thenThrow(new IOException("Network error"));
//
//        IOException exception = assertThrows(IOException.class, () -> {
//            plantWikiService.sendRequest(query);
//        });
//
//        assertEquals("Network error", exception.getMessage());
//        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
//    }
//
//    @Test
//    void testSendRequestThrowsInterruptedException() throws IOException, InterruptedException {
//        String query = "monstera";
//
//        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
//                .thenThrow(new InterruptedException("Request interrupted"));
//
//        InterruptedException exception = assertThrows(InterruptedException.class, () -> {
//            plantWikiService.sendRequest(query);
//        });
//
//        assertEquals("Request interrupted", exception.getMessage());
//        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
//    }
}
