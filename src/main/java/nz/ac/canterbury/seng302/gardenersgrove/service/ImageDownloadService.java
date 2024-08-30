package nz.ac.canterbury.seng302.gardenersgrove.service;

import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
public class ImageDownloadService {

    public String downloadImage(String imageUrl) {
        String uniqueFilename = UUID.randomUUID().toString() + ".jpg"; // Generate a unique filename
        String destinationFile = "./src/main/resources/static/images/tempImageStorage/" + uniqueFilename;
        try (InputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream out = new FileOutputStream(destinationFile)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
            System.out.println("Image downloaded successfully to " + destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to download the image from " + imageUrl);
        }
        return uniqueFilename; // Return the unique filename
    }
}
