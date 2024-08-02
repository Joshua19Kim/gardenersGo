package nz.ac.canterbury.seng302.gardenersgrove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Gardener's Grove entry-point
 * Note @link{SpringBootApplication} annotation
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class GardenersGroveApplication {
	/**
	 * Main entry point, runs the Spring application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(GardenersGroveApplication.class, args);
	}
}
