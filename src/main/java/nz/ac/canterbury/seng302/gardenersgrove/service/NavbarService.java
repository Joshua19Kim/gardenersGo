package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
public class NavbarService {

    private final GardenerFormService gardenerFormService;
    private final GardenService gardenService;

    public NavbarService(GardenerFormService gardenerFormService, GardenService gardenService) {
        this.gardenerFormService = gardenerFormService;
        this.gardenService = gardenService;
    }

    /**
     * Retrieves the gardener based on the current authentication.
     * @return an optional of the gardener.
     */
    public Optional<Gardener> getGardenerFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return gardenerFormService.findByEmail(currentUserEmail);
    }

    public void populateNavbar(Model model) {
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        if (gardenerOptional.isPresent()) {
            Gardener gardener = gardenerOptional.get();
            List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
            model.addAttribute("gardens", gardens);
        }
    }
}
