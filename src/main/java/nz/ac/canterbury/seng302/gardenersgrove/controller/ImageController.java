package nz.ac.canterbury.seng302.gardenersgrove.controller;


import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;

import java.util.Optional;

@Controller
public class ImageController {

    @Autowired
    private ImageService imageService;

    private final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("/upload")
    public String getUploadForm(Model model) {
        model.addAttribute("image", new Image());
        logger.info("GET /upload");
        return "user";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file")MultipartFile file, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("POST /upload");

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Optional<String> uploadMessage =  imageService.saveImage(file);
            if (uploadMessage.isEmpty()) {
                return "redirect:/user";
            } else {
                model.addAttribute("uploadMessage", uploadMessage.get());
                return "/user";
            }
        }
        return "/login";
    }
}
