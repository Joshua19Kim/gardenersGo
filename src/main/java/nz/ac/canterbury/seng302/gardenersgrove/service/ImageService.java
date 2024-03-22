package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
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

@Service
public class ImageService {

    private final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final GardenerFormService gardenerFormService;
    private final String IMAGE_PATH = "/images/";
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
    private static int MAX_SIZE = 10*1024*1024;

    public List<String> validExtensions = new ArrayList<>(Arrays.asList("image/jpeg", "image/png", "image/svg+xml"));
    @Autowired
    public ImageService(GardenerFormService gardenerFormService) {
        this.gardenerFormService = gardenerFormService;
    }

    public Optional<String> saveImage(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
            String fileName = file.getOriginalFilename();
            if (gardenerOptional.isPresent()) {
                Gardener gardener = gardenerOptional.get();
                String newFileName = gardener.getId() + "." + fileName.substring(fileName.lastIndexOf(".")+1); // mess around with better version of this
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
