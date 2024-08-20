package nz.ac.canterbury.seng302.gardenersgrove.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Weather;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class PlantWikiService {

    Logger logger = LoggerFactory.getLogger(PlantWikiService.class);
    private String api_key;

    private String PERENUAL_API_URL = "https://perenual.com/api/species-list";
    private final ObjectMapper objectMapper;

    @Autowired
    public PlantWikiService(@Value("${plant.key}") String api_key, ObjectMapper objectMapper) {
        this.api_key = api_key;
        this.objectMapper = objectMapper;

    }


    public WikiPlant getPlants(String query) throws IOException, URISyntaxException {
        logger.info("SEND Request");
        String uri = PERENUAL_API_URL +"?key="+ this.api_key + "&q=" + query;
        URL url = new URI(uri).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            WikiPlant wikiPlant = objectMapper.readValue(url, WikiPlant.class);
            logger.info("Plant info about: " + wikiPlant.getName());
            return wikiPlant;
        } catch (IOException ex) {
            // this occurs when no plant matches the search
            return null;
        }

    }

}
