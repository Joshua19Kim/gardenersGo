package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service to handle the validation and naming of images, before writing to file
 */
@Service
public class ImageService {

    private final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final GardenerFormService gardenerFormService;

    private final PlantService plantService;
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/images/";
    private final int MAX_SIZE = 10*1024*1024;

    public List<String> validExtensions = new ArrayList<>(Arrays.asList("image/jpeg", "image/png", "image/svg+xml"));

    @Autowired
    public ImageService(GardenerFormService gardenerFormService, PlantService plantService) {
        this.gardenerFormService = gardenerFormService;
        this.plantService = plantService;
    }

    /**
     * Gets credentials to see user can make changes to a users profile picture. Retrieves gardener(user) and uses their
     * id to generate the appropriate filename for the image. Assuming image is a valid type, image gets written to
     * storage. If this chain of events fails then appropriate optional strings are returned
     *
     * @param file, the Image
     * @return Returns a variety of optional strings. These are strings that can be used to display various
     * warning/diagnosing/success messages.
     */
    public Optional<String> saveImage(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            String fileName = file.getOriginalFilename();
            if (gardenerOptional.isPresent()) {
                Gardener gardener = gardenerOptional.get();
                //NullPointerException shouldn't affect below line as HTML form prevents an empty upload, i.e. file will never be null
                String newFileName = gardener.getId() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
                Path filePath = Paths.get(UPLOAD_DIRECTORY, newFileName);
                logger.info("File location: " + filePath);

                if (checkValidImage(file).isEmpty()) {
                    Files.write(filePath, file.getBytes());
                    gardener.setProfilePicture(newFileName);
                    gardenerFormService.addGardener(gardener);
                    return Optional.empty();
                } else {
                    return checkValidImage(file);
                }

            } else {
                return Optional.of("I made a boo boo"); // Sam THE GOAT
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
        return Optional.empty();
    }

    /**
     * Gets credentials to see user can make changes to a plants profile picture. Uses the plants id to generate the
     * appropriate filename for the image. Assuming image is a valid type, image gets written to
     * storage. If this chain of events fails then appropriate optional strings are returned
     *
     * @param file, the Image
     * @return Returns a variety of optional strings. These are strings that can be used to display various
     * warning/diagnosing/success messages.
     */
    public Optional<String> savePlantImage(MultipartFile file, Plant plant) {

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            String fileName = file.getOriginalFilename();
            //NullPointerException shouldn't affect below line as HTML form prevents an empty upload, i.e. file will never be null
            String newFileName = "plant_" + plant.getId() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
            Path filePath = Paths.get(UPLOAD_DIRECTORY, newFileName);
            logger.info("File location: " + filePath);
            if (checkValidImage(file).isEmpty()) {
                Files.write(filePath, file.getBytes());
                plant.setImage(newFileName);
                plantService.addPlant(plant);
                return Optional.empty();
            } else {
                return checkValidImage(file);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return Optional.empty();
    }

    public boolean isFileSizeValid(MultipartFile file) {
        return file.getSize() <= MAX_SIZE;
    }


    public boolean checkValidExtension (MultipartFile file) {
        return validExtensions.contains(file.getContentType());
    }

    /**
     * Using helper functions, checks the image file is of an appropriate image extension as detailed in a global list,
     * and checks that the image is below the stipulated file size based on another global variable
     *
     * @param file the image
     * @return optional string detailing the results the image extension check
     */
    public Optional<String> checkValidImage(MultipartFile file) {
        if (checkValidExtension(file) && isFileSizeValid(file)) {
            return Optional.empty();
        } else if ((!checkValidExtension(file)) && isFileSizeValid(file)) {
            return Optional.of("Image must be of type png, jpg or svg");
        } else if (checkValidExtension(file) && (!isFileSizeValid(file))) {
            return Optional.of("Image must be less than 10MB");
        } else {
            return Optional.of("Image must be of type png, jpg or svg " + "\n" +
                    "Image must be less than 10MB");
        }
    }
}
