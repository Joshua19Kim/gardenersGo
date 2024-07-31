package nz.ac.canterbury.seng302.gardenersgrove.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.http.HttpClient;

@Configuration
public class AppConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public String stagingUrl(@Value("${staging.url}") String url) {
        System.out.println("Staging URL: " + url);
        return url;
    }
}