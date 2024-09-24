package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
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
    private final IdentifiedPlantService identifiedPlantService;
    private static final int MAX_SIZE = 10*1024*1024;
    private final List<String> validExtensions = new ArrayList<>(Arrays.asList("image/jpeg", "image/png", "image/svg+xml"));
    private final List<String> validPlantImageExtensions = new ArrayList<>(Arrays.asList("image/jpeg", "image/png"));
    private static final String UPLOADS_DIR = "/uploads/";
    private static final String UPLOAD_DIRECTORY = Path.of(System.getProperty("user.dir")).resolve("uploads").toString();

    private final String destinationErrorMessage = "Entry is outside of the target directory";
    @Autowired
    public ImageService(GardenerFormService gardenerFormService, PlantService plantService, IdentifiedPlantService identifiedPlantService) {
        this.gardenerFormService = gardenerFormService;
        this.plantService = plantService;
        this.identifiedPlantService = identifiedPlantService;
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
                assert fileName != null;
                String newFileName = gardener.getId() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
                Path filePath = Paths.get(UPLOAD_DIRECTORY, newFileName);

                if (checkValidImage(file).isEmpty()) {
                    File checkFile = new File(filePath.toString());
                    String canonicalDestinationPath = checkFile.getCanonicalPath();

                    if (!canonicalDestinationPath.startsWith(UPLOAD_DIRECTORY)) {
                        throw new IOException(destinationErrorMessage);
                    }
                    Files.write(filePath, file.getBytes());
                    gardener.setProfilePicture(UPLOADS_DIR + newFileName);
                    gardenerFormService.addGardener(gardener);
                    return Optional.empty();
                } else {
                    return checkValidImage(file);
                }

            } else {
                return Optional.of("I made a boo boo"); // Sam THE GOAT
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
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
            assert fileName != null;
            String newFileName = "plant_" + plant.getId() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
            Path filePath = Paths.get(UPLOAD_DIRECTORY, newFileName);
            if (checkValidImage(file).isEmpty()) {
                File checkFile = new File(filePath.toString());
                String canonicalDestinationPath = checkFile.getCanonicalPath();

                if (!canonicalDestinationPath.startsWith(UPLOAD_DIRECTORY)) {
                    throw new IOException(destinationErrorMessage);
                }
                Files.write(filePath, file.getBytes());
                plant.setImage(UPLOADS_DIR + newFileName);
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
    public boolean checkValidPlantImageExtension (MultipartFile file) {
        return validPlantImageExtensions.contains(file.getContentType());
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

    public Optional<String> checkValidPlantImage(MultipartFile file) {
        if (checkValidPlantImageExtension(file) && isFileSizeValid(file)) {
            return Optional.empty();
        } else if ((!checkValidPlantImageExtension(file)) && isFileSizeValid(file)) {
            return Optional.of("Image must be of type png or jpg.");
        } else if (checkValidPlantImageExtension(file) && (!isFileSizeValid(file))) {
            return Optional.of("Image must be less than 10MB");
        } else {
            return Optional.of("Image must be of type png or jpg " + "\n" +
                    "Image must be less than 10MB");
        }
    }


    /**
     * Saves the image locally and sets the image of the identified plant
     * @param file the image file
     * @param identifiedPlant the identified plant
     */
    public void saveCollectionPlantImage(MultipartFile file, IdentifiedPlant identifiedPlant) {

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            String fileName = file.getOriginalFilename();
            //NullPointerException shouldn't affect below line as HTML form prevents an empty upload, i.e. file will never be null
            assert fileName != null;
            String newFileName = "identifiedPlant_" + identifiedPlant.getId() + "." + fileName.substring(fileName.lastIndexOf(".")+1);
            Path filePath = Paths.get(UPLOAD_DIRECTORY, newFileName);
                File checkFile = new File(filePath.toString());
                String canonicalDestinationPath = checkFile.getCanonicalPath();

                if (!canonicalDestinationPath.startsWith(UPLOAD_DIRECTORY)) {
                    throw new IOException(destinationErrorMessage);
                }
                Files.write(filePath, file.getBytes());
                identifiedPlant.setUploadedImage(UPLOADS_DIR + newFileName);
                identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }


    /**
     * Helper function to download the image from the api
     * @param imageUrl location of image
     * @param id id of the plant you want image for
     * @return the location of the upload to store in the database
     */
    public String downloadImage(String imageUrl, Long id) {
        String newFileName = "plant_" + id.toString() + ".png";

        String destinationFile = Paths.get(UPLOAD_DIRECTORY, "plants", newFileName).toString();
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY, "plants"));

            try (InputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
                 FileOutputStream out = new FileOutputStream(destinationFile)) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    out.write(dataBuffer, 0, bytesRead);
                }
                logger.info("Image downloaded successfully to " + destinationFile);
            }
        } catch (IOException e) {
            logger.error("Failed to download the image from {} to {}: {}", imageUrl, destinationFile, e.getMessage());
            return null;
        }
        String keyword = "/uploads/";
        String uploadLocation;
        int startIndex = destinationFile.indexOf(keyword);

        if (startIndex != -1) {
            uploadLocation = destinationFile.substring(startIndex);
        } else {
            uploadLocation = "/images/placeholder.jpg";
        }
        return uploadLocation;
    }










}
