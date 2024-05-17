package nz.ac.canterbury.seng302.gardenersgrove.util;

import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;

import java.util.Optional;

public class TagValidation {
    private final TagService tagService;

    public TagValidation(TagService tagService) {
        this.tagService = tagService;
    }

    public Optional<String> validateTag(String tagName) {
        String tagRegex = "[\\p{L}]+((?:[-' ]?\\p{L}+)?)*";
        if (tagName.length() >25) {
            return Optional.of("A tag cannot exceed 25 characters");
        } else {
            return tagName.matches(tagRegex) ? Optional.empty() : Optional.of("The tag name must only contain alphanumeric characters, spaces, -, _, ', or " + '"');
        }
    }

    public Optional<String> checkTagInUse(String tagName) {
        return (tagService.findTagByName(tagName).isPresent() ? Optional.of("Used") : Optional.empty());
    }
}
