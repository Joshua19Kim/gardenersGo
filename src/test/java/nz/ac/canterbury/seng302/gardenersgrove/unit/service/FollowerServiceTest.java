package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FollowerRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FollowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
public class FollowerServiceTest {

    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private FollowerRepository followerRepository;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    private FollowerService followerService;
    private final Gardener testGardener = new Gardener("test", "test", LocalDate.of(2010, 10, 10), "jane@doe.com" , "Password1!");
    private final Gardener testGardener2 = new Gardener("test", "test", LocalDate.of(2010, 10, 10), "jane@doe.com" , "Password1!");
    private final Garden garden = new Garden("Test garden", "test location", "test suburb", "test city", "test country", "2025", testGardener, "test desc");
    private final Garden privateGarden = new Garden("Test garden", "test location", "test suburb", "test city", "test country", "2025", testGardener, "test desc");

    @BeforeEach
    public void setUp() {
        gardenerFormRepository.save(testGardener);
        gardenerFormRepository.save(testGardener2);
        followerService = new FollowerService(followerRepository, gardenRepository);
        garden.setIsGardenPublic(true);
        gardenRepository.save(garden);
        gardenRepository.save(privateGarden);
    }


    @Test
    void AddFollower_ValidValues_FollowerCreated() {
        Follower follower = new Follower(testGardener2.getId(), garden.getId());
        followerService.addfollower(follower);

        Optional<Follower> followerOptional = followerService.findFollower(testGardener2.getId(), garden.getId());
        assertTrue(followerOptional.isPresent());
    }

    @Test
    void AddFollower_FollowingOwnGarden_ErrorThrown() throws IllegalArgumentException {
        Follower follower = new Follower(testGardener.getId(), garden.getId());
        assertThrows(IllegalArgumentException.class, () -> followerService.addfollower(follower));
    }

    @Test
    void AddFollower_FollowingPrivateGarden_ErrorThrown() throws IllegalArgumentException {
        Follower follower = new Follower(testGardener2.getId(), privateGarden.getId());
        assertThrows(IllegalArgumentException.class, () -> followerService.addfollower(follower));
    }


}
