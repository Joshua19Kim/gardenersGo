package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class PlantInfoService {

    Logger logger = LoggerFactory.getLogger(PlantInfoService.class);
    private String api_key;
    private HttpClient client;

    @Autowired
    public PlantInfoService(@Value("${plant.key}") String api_key, HttpClient client) {
        this.api_key = api_key;
        this.client = client;
    }

    public HttpResponse<String> sendRequest(String query) throws IOException, InterruptedException {
        logger.info("SEND Request");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://perenual.com/api/species-list?key=" + this.api_key + "&q=" + query))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();


        // Needs to be HttpClient type as it goes back to HTML.
        return this.client.send(request, HttpResponse.BodyHandlers.ofString());

    }
}
