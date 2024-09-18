package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeType;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
         this.badgeNames = List.of("1st Plant Found", "10th Plant Found", "25th Plant Found", "50th Plant Found", "100th Plant Found");
     }

    /**
     *  Add a badge to the repository
     * @param badge the badge being added
     */
     public void addBadge(Badge badge){
         badgeRepository.save(badge);
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
    public void checkPlantBadgeToBeAdded(Gardener gardener, Integer plantCount) {
         Badge badge;
         switch (plantCount) {
             case 1:
                 badge = new Badge("1st Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener );
                 addBadge(badge);
                 break;
             case 10:
                 badge = new Badge("10th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener );
                 addBadge(badge);
                 break;
             case 25:
                 badge = new Badge("25th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener );
                 addBadge(badge);
                 break;
             case 50:
                 badge = new Badge("50th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener );
                 addBadge(badge);
                 break;
             case 100:
                 badge = new Badge("100th Plant Found", LocalDate.now(), BadgeType.PLANTS, gardener );
                 addBadge(badge);
                 break;
         }

    }
}
