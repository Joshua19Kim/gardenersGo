package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FollowerRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Service class for follower, defined by the @link{Service} annotation.
 * This class links automatically with @link{followerRepository}, see the @link{Autowired} annotation below
 */
@Service
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final GardenRepository gardenRepository;

    @Autowired
    public FollowerService(
            FollowerRepository followerRepository, GardenRepository gardenRepository) {
        this.followerRepository = followerRepository;
        this.gardenRepository = gardenRepository;
    }

    /**
     * Adds a follower to persistence
     *
     * @param follower object to persist
     */
    public void addfollower(Follower follower) throws IllegalArgumentException {
        // Check follower garden is public
        long gardenId = follower.getGardenId();
        Optional<Garden> garden = gardenRepository.findById(gardenId);
        if (garden.isPresent()) {
            if (!garden.get().getIsGardenPublic()) {
                throw new IllegalArgumentException("Cannot follow this garden");
            } else if (Objects.equals(garden.get().getGardener().getId(), follower.getGardenerId())) {
                throw new IllegalArgumentException("Cannot follow your own garden");
            }
        } else {
            throw new IllegalArgumentException("Cannot follow this garden");
        }
        followerRepository.save(follower);
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
            List<Long> gardenIds = new ArrayList<>();
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

    public List<Follower> findFollowing(Long id) {
        return followerRepository.findAllByGardenId(id);

    }
}
