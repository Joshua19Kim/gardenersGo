package nz.ac.canterbury.seng302.gardenersgrove.config;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    GardenService gardenService;

    @Autowired
    GardenerFormService gardenerFormService;

    @ModelAttribute("gardens")
    public List<Garden> getGardens() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
        return gardenerOptional.map(gardener -> gardenService.getGardensByGardenerId(gardener.getId())).orElse(null);
    }
}
