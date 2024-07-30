package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller class that handles all the logic for the browse gardens page
 */
@Controller
public class BrowseGardensController {

    private final GardenService gardenService;

    @Autowired
    public BrowseGardensController(GardenService gardenService) {
        this.gardenService = gardenService;
    }

    /**
     * Handles GET request for the browse gardens page. It gets a Page of Garden objects by
     * specifying the page number and page size.
     *
     * @param pageNo the page number
     * @param pageSize the page size
     * @param model the model
     * @return the browse gardens html template which contains the user interface for the browse gardens page
     */
    @RequestMapping("/browseGardens")
    public String browseGardens(
            @RequestParam(name="pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name="pageSize", defaultValue = "10") int pageSize,
            Model model
    ) {
        Page<Garden> gardensPage = gardenService.getGardensPaginated(pageNo, pageSize);
        model.addAttribute("gardensPage", gardensPage);
        int totalPages = gardensPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "browseGardensTemplate";
    }
}