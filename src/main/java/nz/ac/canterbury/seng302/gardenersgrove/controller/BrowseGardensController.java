package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Long.parseLong;

/**
 * Controller class that handles all the logic for the browse gardens page
 */
@Controller
public class BrowseGardensController {

    private final GardenService gardenService;

    private final TagService tagService;

    @Autowired
    public BrowseGardensController(GardenService gardenService, TagService tagService) {

        this.gardenService = gardenService;
        this.tagService = tagService;
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
            @RequestParam(name="tags", required = false) List<String> tags,
            Model model
    ) {
        if(model.containsAttribute("pageSize") && model.containsAttribute("pageNo")) {
            pageSize = (int) model.getAttribute("pageSize");
            pageNo = (int) model.getAttribute("pageNo");
        }
        Page<Garden> gardensPage = gardenService.getGardensPaginated(pageNo, pageSize);
        model.addAttribute("gardensPage", gardensPage);
        int totalPages = gardensPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        if(!model.containsAttribute("tags") && !model.containsAttribute("allTags")) {
            List<String> allTags = tagService.getAllTagNames();
            if(tags != null) {
                for(String selectedTag: tags) {
                    allTags.remove(selectedTag);
                }
            }
            model.addAttribute("allTags", allTags);
            model.addAttribute("tags", tags);
        }

        return "browseGardensTemplate";
    }

    @PostMapping("/browseGardens/addTag")
    public String addTag(
            @RequestParam(name="pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name="pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name="tag-input") String tag,
            @RequestParam(name="tags", required = false) List<String> tags,
            Model model, RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addAttribute("pageNo", pageNo);
        redirectAttributes.addAttribute("pageSize", pageSize);
        if(tags == null) {
            tags = new ArrayList<>();
        }
        List<String> allTags = tagService.getAllTagNames();
        if(allTags.contains(tag)) {
            tags.add(tag);
        } else {
            String errorMessage = "No tag matching " + tag;
            redirectAttributes.addFlashAttribute("tag", tag);
            redirectAttributes.addFlashAttribute("tagValid", errorMessage);
        }
        for(String selectedTag: tags) {
            allTags.remove(selectedTag);
        }

        redirectAttributes.addFlashAttribute("tags", tags);
        redirectAttributes.addFlashAttribute("allTags", allTags);

        return "redirect:/browseGardens";
    }
}
