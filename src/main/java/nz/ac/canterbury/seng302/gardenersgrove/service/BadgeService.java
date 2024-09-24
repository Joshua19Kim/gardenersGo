package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeType;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 *  Service class for the badge repository
 */
@Service
public class BadgeService {

    private List<String> badgeNames;
    private BadgeRepository badgeRepository;

    /**
     * Constructs the badge service
     * @param badgeRepository the repository used by the service
     */
     @Autowired
        public BadgeService(BadgeRepository badgeRepository){
         this.badgeRepository= badgeRepository;
         this.badgeNames = List.of("1st Plant Found", "10th Plant Found", "25th Plant Found", "50th Plant Found", "100th Plant Found",
                 "1st Species Found", "10th Species Found", "25th Species Found", "50th Species Found", "100th Species Found",
                 "1st Region Found", "5th Region Found", "10th Region Found", "17th Region Found");
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
     * Retrieves the three most recently earned badges for a given gardener.
     *
     * @param gardenerId The id of the gardener to retrieve the most recently earned badges.
     * @return A list containing the three most recently earned badges.
     */
    public List<Badge> getMyRecentBadges(Long gardenerId) {
        return badgeRepository.findRecentByGardenerId(gardenerId, PageRequest.of(0, 5));
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
     * Finds the badge by its id
     * @param badgeId badge id
     * @return the badge
     */
    public Optional<Badge> getMyBadgeById(long badgeId, long gardenerId) {
        return badgeRepository.findByIdAndGardenerId(badgeId, gardenerId);
    }
}
