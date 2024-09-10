package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.util.TagValidation;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller class that handles all the logic for the browse gardens page
 */
@Controller
public class BrowseGardensController {

    private final GardenService gardenService;

    private final GardenerFormService gardenerFormService;

    Logger logger = LoggerFactory.getLogger(BrowseGardensController.class);

    private final TagService tagService;

    private final int pageSize;

    private String searchTerm;


    private List<String> searchTags;


    /**
     * Constructor for the BrowseGardensController that intializes all the properties of the class
     * @param gardenService used to perform business logic related to gardens
     * @param tagService used to perform business logic related to tags
     * @param gardenerFormService used to get the gardens to populate the navbar
     */
    @Autowired
    public BrowseGardensController(GardenService gardenService, GardenerFormService gardenerFormService, TagService tagService) {

        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.tagService = tagService;
        this.pageSize = 10;
        this.searchTerm = "";
        this.searchTags = new ArrayList<>();

    }

    /**
     * Sets the search term
     * @param searchTerm stores the current search term so that it can be persistent across requests
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Sets the search tags
     * @param tags stores the tags that the user has searched/filtered for so that it is persistent across requests.
     */
    public void setSearchTags(List<String> tags) {
        this.searchTags = tags;
    }


    /**
     * Handles GET request for the browse gardens page. It gets a Page of Garden objects by
     * specifying the page number and page size.
     *
     * @param pageNoString the page number
     * @param model the model
     * @return the browse gardens html template which contains the user interface for the browse gardens page
     */
    @GetMapping("/browseGardens")
    public String browseGardens(
            @RequestParam(name="pageNo", defaultValue = "0") String pageNoString,
            @RequestParam(name="pageRequest", defaultValue = "hehe") String pageRequest,
            Model model
    ) {
        // this is used to distinguish between a fresh get request and one used to paginate or add tag
        if(Objects.equals(pageRequest, "hehe") && !model.containsAttribute("pageRequest")) {
            setSearchTags(new ArrayList<>());
            setSearchTerm("");
        }
        long tagCount;
        if (searchTags == null) {
            tagCount = 0L;
        } else {
            tagCount = (long) searchTags.size();
        }
        if(model.containsAttribute("pageNo")) {
            pageNoString = (String) model.getAttribute("pageNo");
        }
        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<Garden> gardensPage;
        if((searchTerm == null || searchTerm.isEmpty()) && (searchTags == null || searchTags.isEmpty()) ){
            gardensPage = gardenService.getGardensPaginated(pageNo, pageSize);
        } else {
            gardensPage = gardenService.getSearchResultsPaginated(pageNo, pageSize, searchTerm, searchTags, tagCount);
        }

        model.addAttribute("gardensPage", gardensPage);
        int totalPages = gardensPage.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = gardensPage.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute("paginationMessage", paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute("paginationMessage", paginationMessage);
        }

        if(!model.containsAttribute("tags") && !model.containsAttribute("allTags")) {
            List<String> allTags = tagService.getAllTagNames();
            if(searchTags != null) {
                for(String selectedTag: searchTags) {
                    allTags.remove(selectedTag);
                }
                model.addAttribute("tags", searchTags);
            }
            model.addAttribute("allTags", allTags);
        }
        model.addAttribute("searchTerm", searchTerm);

        return "browseGardensTemplate";
    }

    /**
     * Posts a form response with the search term to search for gardens
     * @param pageNo the page number
     * @param pageSize the page size
     * @param model the model
     *
     * @return the browse gardens html template filled with gardens matching the search term
     */
    @PostMapping("/browseGardens")
    public String sendSearchTerm(
            @RequestParam(name="pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name="pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name="searchTerm") String searchTerm,
            @RequestParam(name="tags", required = false) List<String> tags,
            Model model) {
        logger.info("POST /browseGardens");
        setSearchTerm(searchTerm);
        long tagCount;
        if (tags == null) {
            tagCount = 0L;
            setSearchTags(new ArrayList<>());
        } else {
            tagCount = (long) tags.size();
            setSearchTags(tags);
        }
        List<String> allTags = tagService.getAllTagNames();
        if(searchTags != null) {
            for(String selectedTag: searchTags) {
                allTags.remove(selectedTag);
            }
            model.addAttribute("tags", searchTags);
        }
        model.addAttribute("allTags", allTags);
        model.addAttribute("searchTerm", searchTerm);

        Page<Garden> gardensPage = gardenService.getSearchResultsPaginated(pageNo, pageSize, searchTerm, tags, tagCount);
        if (gardensPage.getContent().isEmpty()) {
            model.addAttribute("noSearchResults", "No gardens match your search.");
        }
        model.addAttribute("gardensPage", gardensPage);
        int totalPages = gardensPage.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = gardensPage.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute("paginationMessage", paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute("paginationMessage", paginationMessage);
        }

        return "browseGardensTemplate";
    }

    /**
     * POST request for add tag which adds a tag to the list of tags that the user is wanting to filter
     * their browse gardens search by
     *
     * @param pageNo the page number
     * @param tag the tag the user typed in or selected
     * @param tags all the tags the user is filtering by
     * @param redirectAttributes attributes used to add to the model of the url it is redirected to
     * @return redirects to the browse gardens page
     */
    @PostMapping("/browseGardens/addTag")
    public String addTag(
            @RequestParam(name="pageNo", defaultValue = "0") String pageNo,
            @RequestParam(name="tag-input") String tag,
            @RequestParam(name="tags", required = false) List<String> tags,
            RedirectAttributes redirectAttributes
    ) {
        String tagValid ="";
        tag = tag.strip();

        // Validate the tag


        if (tags == null) {
            tags = new ArrayList<>();
        }

        List<String> allTags = tagService.getAllTagNames();

        if (allTags.contains(tag)) {
            // if the tag has already been selected
            if (tags.contains(tag)) {
                String errorMessage = "You have already selected " + tag + " <br/>";
                redirectAttributes.addFlashAttribute("tag", tag);
                tagValid += errorMessage;
            } else {
                tags.add(tag);
            }
        } else {
            // if tag doesn't exist
            String errorMessage = "No tag matching " + tag + " <br/>";
            redirectAttributes.addFlashAttribute("tag", tag);
            tagValid += errorMessage;
        }

        TagValidation tagValidation = new TagValidation(tagService);
        Optional<String> validTagError = tagValidation.validateTag(tag);
        if (validTagError.isPresent()) {
            redirectAttributes.addFlashAttribute("tag", tag);
            tagValid += validTagError.get();
        }

        // remove selected tags from the autocomplete options
        allTags.removeAll(tags);

        redirectAttributes.addFlashAttribute("tags", tags);
        redirectAttributes.addFlashAttribute("allTags", allTags);
        redirectAttributes.addFlashAttribute("pageNo", pageNo);
        redirectAttributes.addFlashAttribute("pageRequest", true);
        redirectAttributes.addFlashAttribute("tagValid", tagValid);

        return "redirect:/browseGardens";
    }
}
