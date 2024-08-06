package nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Long.parseLong;

/** Controller class responsible for handling garden-related HTTP requests. */
@Controller
public class GardenEditController {
    Logger logger = LoggerFactory.getLogger(GardenEditController.class);
    private final GardenService gardenService;
    private final GardenerFormService gardenerFormService;
    private final RequestService requestService;
    private Gardener gardener;

    /**
     * Constructor used to create a new instance of the GardenEditController. Autowires a
     * various service objects
     *
     * @param gardenService the garden service used to interact with the database
     * @param gardenerFormService the gardener form service used to interact with the database
     * @param requestService the request service used to manage HTTP requests
     */
    @Autowired
    public GardenEditController(
            GardenService gardenService,
            GardenerFormService gardenerFormService,
            RequestService requestService) {
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.requestService = requestService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication We will always have to
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
     * Updates the details of a garden in the database based on the details provided in the form
     * If the location is changed the notifications are reset
     *
     * @param name The name of the garden
     * @param location the location of the garden
     * @param suburb The suburb of the garden.
     * @param city The city of the garden.
     * @param country The country of the garden.
     * @param postcode The postcode of the garden.
     * @param size the size of the garden
     * @param description the garden description
     * @param gardenId the id of the garden to edit
     * @param model the model
     * @return the garden details template
     */
    @PostMapping("gardens/edit")
    public String submitEditForm(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "location") String location,
            @RequestParam(name = "suburb") String suburb,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "country") String country,
            @RequestParam(name = "postcode") String postcode,
            @RequestParam(name = "size") String size,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "gardenId") String gardenId,
            Model model,
            HttpServletRequest request) {
        logger.info("POST gardens/edit");
        String validatedName = ValidityChecker.validateGardenName(name);
        String validatedAddress = ValidityChecker.validateGardenAddress(location);
        String validatedSuburb = ValidityChecker.validateGardenSuburb(suburb);
        String validatedCity = ValidityChecker.validateGardenCity(city);
        String validatedCountry = ValidityChecker.validateGardenCountry(country);
        String validatedPostcode = ValidityChecker.validateGardenPostcode(postcode);
        String validatedSize = ValidityChecker.validateGardenSize(size);
        String validatedDescription = ValidityChecker.validateGardenDescription(description);

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        List<Garden> gardens = new ArrayList<>();
        if (gardenerOptional.isPresent()) {
            gardener = gardenerOptional.get();
            gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
        }

        boolean isValid = true;
        String returnedTemplate = "redirect:/gardens/details?gardenId=" + gardenId;

        if (!Objects.equals(name, validatedName)) {
            model.addAttribute("nameError", validatedName);
            isValid = false;
        }
        if (!Objects.equals(size.replace(',', '.'), validatedSize)) {
            model.addAttribute("sizeError", validatedSize);
            isValid = false;
        }
        if(!Objects.equals(location, validatedAddress)) {
            model.addAttribute("locationError", validatedAddress);
            isValid = false;
        }
        if(!Objects.equals(suburb, validatedSuburb)) {
            model.addAttribute("suburbError", validatedSuburb);
            isValid = false;
        }
        if (!Objects.equals(description, validatedDescription) && !description.trim().isEmpty()) {
            model.addAttribute("descriptionError", validatedDescription);
            isValid = false;
        }
        if(description.trim().isEmpty()) {
            description = "";
        }
        if(!Objects.equals(city, validatedCity)) {
            model.addAttribute("cityError", validatedCity);
            isValid = false;
        }
        if(!Objects.equals(country, validatedCountry)) {
            model.addAttribute("countryError", validatedCountry);
            isValid = false;
        }
        if(!Objects.equals(postcode, validatedPostcode)) {
            model.addAttribute("postcodeError", validatedPostcode);
            isValid = false;
        }


        if (isValid) {
            Garden existingGarden = gardenService.getGarden(parseLong(gardenId)).get();
            if (!existingGarden.getLocation().equals(location)){
                existingGarden.setLastNotified(null);

            }
            existingGarden.setName(name);
            existingGarden.setLocation(location.trim());
            existingGarden.setSuburb(suburb.trim());
            existingGarden.setCity(city);
            existingGarden.setCountry(country);
            existingGarden.setPostcode(postcode.trim());
            existingGarden.setDescription(description);

            if (Objects.equals(size.trim(), "")) {
                existingGarden.setSize(null);
                gardenService.addGarden(existingGarden);
            } else {
                existingGarden.setSize(new BigDecimal(validatedSize).stripTrailingZeros().toPlainString());
                gardenService.addGarden(existingGarden);
            }
        } else {
            model.addAttribute("gardens", gardens);
            model.addAttribute("name", name);
            model.addAttribute("location", location);
            model.addAttribute("description", description);
            model.addAttribute("suburb", suburb);
            model.addAttribute("city", city);
            model.addAttribute("country", country);
            model.addAttribute("postcode", postcode);
            model.addAttribute("size", size.replace(',', '.'));
            model.addAttribute(gardenService.getGarden(parseLong(gardenId)).get());
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            returnedTemplate = "editGardensFormTemplate";
        }
        return returnedTemplate;
    }

    /**
     * Gets the garden to edit by the id and returns the edit garden form template
     *
     * @param gardenId the id of the garden to edit
     * @param model the model
     * @param request the request used to find the current uri
     * @return the edit garden form template
     */
    @GetMapping("gardens/edit")
    public String editGarden(
            @RequestParam(name = "gardenId") String gardenId, Model model, HttpServletRequest request) {
        logger.info("GET gardens/edit");

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        List<Garden> gardens = new ArrayList<>();
        if (gardenerOptional.isPresent()) {
            gardener = gardenerOptional.get();
            gardens = gardenService.getGardensByGardenerId(gardener.getId());
        }

        model.addAttribute("gardens", gardens);
        Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
        if (garden.isPresent()) {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("name", garden.get().getName());
            model.addAttribute("description", garden.get().getDescription());
            model.addAttribute("size", garden.get().getSize() != null ? garden.get().getSize().replace(',', '.') : "");
            model.addAttribute("location", garden.get().getLocation());
            model.addAttribute("suburb", garden.get().getSuburb());
            model.addAttribute("city", garden.get().getCity());
            model.addAttribute("country", garden.get().getCountry());
            model.addAttribute("postcode", garden.get().getPostcode());
            model.addAttribute("garden", garden.get());
            return "editGardensFormTemplate";
        } else {
            return "redirect:/gardens";
        }
    }
}
