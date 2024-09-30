package nz.ac.canterbury.seng302.gardenersgrove.controller;

import java.util.List;
import java.util.Optional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for displaying Open Street Maps through JavaFX webview
 * @author Morgan English
 */
@Controller
public class LeafletOSMController {
    private final GardenerFormService gardenerFormService;
    private final IdentifiedPlantService identifiedPlantService;
    private Gardener gardener;

    public LeafletOSMController(GardenerFormService gardenerFormService, IdentifiedPlantService identifiedPlantService) {
        this.gardenerFormService = gardenerFormService;
        this.identifiedPlantService = identifiedPlantService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication. We will always have to
     * check whether the gardener was retrieved in the calling method, so the return type was left as
     * an optional
     *
     * @return An optional of the requested gardener
     */
    public Optional<Gardener> getGardenerFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return gardenerFormService.findByEmail(currentUserEmail);
    }


    /**
     * Get method for /map to show map with relevant pins
     *
     * @param model the model that will contain all the scanned plants
     **/
    @GetMapping("/map")
    public String map(Model model) {
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        List<IdentifiedPlant> scannedPlants = identifiedPlantService.getGardenerPlantsWithLocations(gardener.getId());
        model.addAttribute("scannedPlants", scannedPlants);
        return "mapTemplate";
    }
}
