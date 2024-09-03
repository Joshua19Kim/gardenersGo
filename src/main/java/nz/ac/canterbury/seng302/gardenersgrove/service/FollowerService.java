package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FollowerRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Service class for follower, defined by the @link{Service} annotation.
 * This class links automatically with @link{followerRepository}, see the @link{Autowired} annotation below
 */
@Service
public class FollowerService {
    Logger logger = LoggerFactory.getLogger(FollowerService.class);
    private final GardenRepository gardenRepository;

    private final FollowerRepository followerRepository;

    @Autowired
    public FollowerService(
            GardenRepository gardenRepository,
            FollowerRepository followerRepository) {
        this.gardenRepository = gardenRepository;
        this.followerRepository = followerRepository;
    }

    /**
     * Gets all Followers from persistence
     * @return all Followers currently saved in persistence
     */
    public List<Follower> getfollower() {
        return followerRepository.findAll();
    }

    /**
     * Adds a follower to persistence
     *
     * @param follower object to persist
     */
    public void addfollower(Follower follower) {
        followerRepository.save(follower);
    }

    public Optional<Follower> getfollower(long gardenerId, long gardenId) {
        return followerRepository.findByGardenerIdAndGardenId(gardenerId, gardenId);
    }

    /**
     * Deletes the follower if it exists
     * @param gardenerId the gardener that is currently following
     * @param gardenId the garden to unfollow
     */
    public void deleteFollower(long gardenerId, long gardenId) {
        Optional<Follower> potentialfollower = followerRepository.findByGardenerIdAndGardenId(gardenerId, gardenId);
        if (potentialfollower.isPresent()) {
            Follower follower = potentialfollower.get();
            followerRepository.deleteById(follower.getId());
        }
    }

    public List<Long> findAllGardens(Long id) {
        List<Follower> followers = followerRepository.findAllByGardenerId(id);
        if (!followers.isEmpty()) {
            List<Long> gardenIds = new ArrayList<Long>();
            for (Follower follower : followers) {
                gardenIds.add(follower.getGardenId());
            }
            return gardenIds;
        }
        return List.of();
    }

    public Optional<Follower> findFollower(long gardenerId, long gardenId) {
        return followerRepository.findByGardenerIdAndGardenId(gardenerId, gardenId);
    }
    public List<Garden> getGardensOwnedByPeopleFollowed(Long gardenerId) {
        List<Follower> followed = followerRepository.findAllByGardenerId(gardenerId);

        List<Garden> gardens = new ArrayList<>();
        for (Follower userIsFollowing : followed) {
            gardens.addAll(gardenRepository.findByGardenerId(userIsFollowing.getId()));
            logger.info("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
            logger.info(gardenRepository.findByGardenerId(userIsFollowing.getGardenId()).toString());
        }
        return gardens;
    }

}
