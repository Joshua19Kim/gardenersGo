package nz.ac.canterbury.seng302.gardenersgrove.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class is used to configure spring security to allow adding of images into the uploads folder
 */
@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS =
            {
                    "classpath:/META-INF/resources/", "classpath:/resources/",
                    "classpath:/static/", "classpath:/public/", "classpath:/static/vendor/"
            };

    /**
     * Function to add resource handlers - which means tell spring security to allow these paths
     * and also tells the app where to look for the images (i.e. the uploads' folder)
     * @param registry - ResourceHandlerRegistry object that controls the adding of the paths
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/src/main/resources/images/");
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }
}
