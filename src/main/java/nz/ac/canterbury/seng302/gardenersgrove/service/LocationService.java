package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class LocationService {
    Logger logger = LoggerFactory.getLogger(LocationService.class);
    private String api_key;

    @Autowired
    public LocationService(@Value("${locationIq.password}") String api_key) {this.api_key = api_key;
    }

    public HttpResponse<String> sendRequest(String query) throws IOException, InterruptedException {

            String urlQuery = query.replace(" ", "%20");
            logger.info(urlQuery);
            String url = "https://us1.locationiq.com/v1/autocomplete?q=" + urlQuery + "&key=" + this.api_key;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("accept", "application/json")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        }
}
