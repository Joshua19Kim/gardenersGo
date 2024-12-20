package nz.ac.canterbury.seng302.gardenersgrove.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeType;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 *  Service class for the badge repository
 */
@Service
public class BadgeService {

    Logger logger = LoggerFactory.getLogger(BadgeService.class);

    private final HashMap<String, String> badgeInformation;
    private final BadgeRepository badgeRepository;

    /**
     * Constructs the badge service and literal used to compare which badges are yet to be unlocked.
     * @param badgeRepository the repository used by the service
     */
     @Autowired
        public BadgeService(BadgeRepository badgeRepository){
         this.badgeRepository= badgeRepository;
         this.badgeInformation = new HashMap();
         this.badgeInformation.put("1st Plant Found", "/images/badges/1PlantBadge.png");
         this.badgeInformation.put("10th Plant Found", "/images/badges/10PlantBadge.png");
         this.badgeInformation.put("25th Plant Found", "/images/badges/25PlantBadge.png");
         this.badgeInformation.put("50th Plant Found", "/images/badges/50PlantBadge.png");
         this.badgeInformation.put("100th Plant Found", "/images/badges/100PlantBadge.png");
         this.badgeInformation.put("1st Species Found", "/images/badges/1SpeciesBadge.png");
         this.badgeInformation.put("10th Species Found", "/images/badges/10SpeciesBadge.png");
         this.badgeInformation.put("25th Species Found", "/images/badges/25SpeciesBadge.png");
         this.badgeInformation.put("50th Species Found", "/images/badges/50SpeciesBadge.png");
         this.badgeInformation.put("100th Species Found", "/images/badges/100SpeciesBadge.png");
         this.badgeInformation.put("1st Region Found", "/images/badges/1RegionBadge.png");
         this.badgeInformation.put("5th Region Found", "/images/badges/5RegionBadge.png");
         this.badgeInformation.put("10th Region Found", "/images/badges/10RegionBadge.png");
         this.badgeInformation.put("17th Region Found", "/images/badges/17RegionBadge.png");
     }

    /**
     *  Add a badge to the repository
     * @param badge the badge being added
     */
     public Badge addBadge(Badge badge){
         return badgeRepository.save(badge);
     }

    /**
     *  Get all badges from the repository
     * @return all badges form the repository
     */
    public List<Badge> getAllBadges(){
         return badgeRepository.findAll();
     }

    /**
     *  gets the badges for a specific gardener
     * @param id the gardener's id
     * @return all of the gardener's badges
     */
     public List<Badge> getMyBadges(Long id) {
         return badgeRepository.findByGardenerId(id);
     }

    /**
     *  gets the unearned badges for a specific gardener
     * @param id the gardener's id
     * @return all of the gardener's unearned badges
     */
    public HashMap<String, String> getMyLockedBadgeNames(Long id) {
        List<String> unlockedNames = badgeRepository.findByGardenerId(id).stream()
                .map(badge -> badge.getName()) // Gets names of all unlocked badges
                .collect(Collectors.toList());
        HashMap<String, String> lockedBadges = new HashMap<>();
        for (String name : badgeInformation.keySet()) {
            if (!unlockedNames.contains(name)) {
                lockedBadges.put(name, badgeInformation.get(name));
            }
        }
        return lockedBadges;
    }

    /**
     * Retrieves the three most recently earned badges for a given gardener.
     *
     * @param gardenerId The id of the gardener to retrieve the most recently earned badges.
     * @return A list containing the three most recently earned badges.
     */
    public List<Badge> getMyRecentBadges(Long gardenerId) {
        List<Badge> recentBadges = badgeRepository.findByGardenerId(gardenerId, Sort.by(Sort.Direction.DESC, "dateEarned"));
        if (recentBadges.size() > 5) { // only want 5 most recent badges
            return recentBadges.subList(0, 5);
        }
        return recentBadges;
    }

    /**
     *  gets a badge by its name
     * @param name the name of a badge
     * @return a badge if the name matches
     */
     public Optional<Badge> getMyBadgeByName(String name, Long gardenerId){
         return badgeRepository.findByNameAndGardenerId(name, gardenerId);
     }

    /**
     * Finds the badge by its id
     * @param badgeId badge id
     * @return the badge
     */
    public Optional<Badge> getMyBadgeById(long badgeId, long gardenerId) {
        return badgeRepository.findByIdAndGardenerId(badgeId, gardenerId);
    }


    /**
     * Checks if there is a badge to be added based on the number of identified plants
     * @param gardener the gardener
     * @param plantCount the plant count
     */
    public Optional<Badge> checkPlantBadgeToBeAdded(Gardener gardener, Integer plantCount) {
         Badge badge;
         switch (plantCount) {
             case 1:
                 badge = new Badge("1st Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener, "/images/badges/1PlantBadge.png");
                 return Optional.of(addBadge(badge));
             case 10:
                 badge = new Badge("10th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener, "/images/badges/10PlantBadge.png" );
                 return Optional.of(addBadge(badge));
             case 25:
                 badge = new Badge("25th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener, "/images/badges/25PlantBadge.png" );
                 return Optional.of(addBadge(badge));
             case 50:
                 badge = new Badge("50th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener, "/images/badges/50PlantBadge.png" );
                 return Optional.of(addBadge(badge));
             case 100:
                 badge = new Badge("100th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener, "/images/badges/100PlantBadge.png" );
                 return Optional.of(addBadge(badge));
             default:
                 return Optional.empty();
         }

    }

    /**
     * Checks if there is a badge to be added based on the number of identified species
     * @param gardener the gardener
     * @param speciesCount the species count
     */
    public Optional<Badge> checkSpeciesBadgeToBeAdded(Gardener gardener, Integer speciesCount) {
        Badge badge;
        switch (speciesCount) {
            case 1:
                badge = new Badge("1st Species Found", LocalDate.now(), BadgeType.SPECIES, gardener, "/images/badges/1SpeciesBadge.png");
                return Optional.of(addBadge(badge));
            case 10:
                badge = new Badge("10th Species Found", LocalDate.now(), BadgeType.SPECIES, gardener, "/images/badges/10SpeciesBadge.png" );
                return Optional.of(addBadge(badge));
            case 25:
                badge = new Badge("25th Species Found", LocalDate.now(), BadgeType.SPECIES, gardener, "/images/badges/25SpeciesBadge.png" );
                return Optional.of(addBadge(badge));
            case 50:
                badge = new Badge("50th Species Found", LocalDate.now(), BadgeType.SPECIES, gardener, "/images/badges/50SpeciesBadge.png" );
                return Optional.of(addBadge(badge));
            case 100:
                badge = new Badge("100th Species Found", LocalDate.now(), BadgeType.SPECIES, gardener, "/images/badges/100SpeciesBadge.png" );
                return Optional.of(addBadge(badge));
            default:
                return Optional.empty();
        }

    }

    /**
     * Checks if there is a badge to be added based on the number of regions
     * @param gardener the gardener
     * @param regionCount the species count
     */
    public Optional<Badge> checkRegionBadgeToBeAdded(Gardener gardener, Integer regionCount) {
        Badge badge;
        switch (regionCount) {
            case 1:
                badge = new Badge("1st Region Found", LocalDate.now(), BadgeType.REGION, gardener, "/images/badges/1RegionBadge.png");
                return Optional.of(addBadge(badge));
            case 5:
                badge = new Badge("5th Region Found", LocalDate.now(), BadgeType.REGION, gardener, "/images/badges/5RegionBadge.png" );
                return Optional.of(addBadge(badge));
            case 10:
                badge = new Badge("10th Region Found", LocalDate.now(), BadgeType.REGION, gardener, "/images/badges/10RegionBadge.png" );
                return Optional.of(addBadge(badge));
            case 17:
                badge = new Badge("17th Region Found", LocalDate.now(), BadgeType.REGION, gardener, "/images/badges/17RegionBadge.png" );
                return Optional.of(addBadge(badge));
            default:
                return Optional.empty();
        }
    }

    /**
     * Deletes badge if location is removed
     * @param orginalCount the original count
     * @param currentCount the current count
     * @param gardener the gardener
     */
    public void checkIfBadgeShouldBeRemoved(int orginalCount, int currentCount, Gardener gardener) {
        if(orginalCount == 1 && currentCount == 0) {
            Optional<Badge> badge = badgeRepository.findByNameAndGardenerId("1st Region Found", gardener.getId());
            badge.ifPresent(value -> badgeRepository.delete(value));
        }
        if(orginalCount == 5 && currentCount == 4) {
            Optional<Badge> badge = badgeRepository.findByNameAndGardenerId("5th Region Found", gardener.getId());
            badge.ifPresent(value -> badgeRepository.delete(value));
        }
        if(orginalCount == 10 && currentCount == 9) {
            Optional<Badge> badge = badgeRepository.findByNameAndGardenerId("10th Region Found", gardener.getId());
            badge.ifPresent(value -> badgeRepository.delete(value));
        }
        if(orginalCount == 17 && currentCount == 16) {
            Optional<Badge> badge = badgeRepository.findByNameAndGardenerId("17th Region Found", gardener.getId());
            badge.ifPresent(value -> badgeRepository.delete(value));
        }

    }
}
