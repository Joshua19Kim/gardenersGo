package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import java.time.LocalDate;
import java.util.Optional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FollowerRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FollowerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({FollowerService.class})
public class FollowerServiceTest {
  @Autowired private FollowerRepository followerRepository;
  @Autowired private GardenRepository gardenRepository;
  @Autowired private GardenerFormRepository gardenerFormRepository;
  private FollowerService followerService;
  private Garden testGarden;
  private Gardener testGardener1;
  private Gardener testGardener2;

  @BeforeEach
  public void setUp() {
    followerService = new FollowerService(followerRepository, gardenRepository);
    testGardener1 =
        new Gardener(
            "Test", "Gardener", LocalDate.of(2024, 4, 1), "testgardener@gmail.com", "Password1!");
    testGardener2 =
        new Gardener(
            "Test", "Gardener", LocalDate.of(2024, 4, 1), "testgardener@gmail.com", "Password1!");
    gardenerFormRepository.save(testGardener1);
    gardenerFormRepository.save(testGardener2);
    testGarden =
        new Garden(
            "Botanical",
            "Homestead Lane",
            null,
            "Christchurch",
            "New Zealand",
            null,
            "100",
            testGardener2,
            "");
    testGarden.setIsGardenPublic(true);
    gardenRepository.save(testGarden);
  }

  @AfterEach
  public void tearDown() {
    gardenRepository.deleteAll();
    followerRepository.deleteAll();
    gardenerFormRepository.deleteAll();
  }

  @Test
  void FollowerAdded_ValidInputs_FollowerSaved() {
    Follower newFollower = new Follower(testGardener1.getId(), testGarden.getId());
    followerService.addFollower(newFollower);
    Assertions.assertEquals(
        followerRepository.findByGardenerIdAndGardenId(testGardener1.getId(), testGarden.getId()),
        Optional.of(newFollower));
  }

  @Test
  void AddFollower_ThrowsException_WhenFollowingOwnGarden() {
    Follower newFollower = new Follower(testGardener2.getId(), testGarden.getId());
    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> followerService.addFollower(newFollower));

    Assertions.assertEquals("Cannot follow your own garden", exception.getMessage());
  }

  @Test
  void addFollower_ThrowsException_WhenGardenIsNotPublic() {
    Follower newFollower = new Follower(testGardener1.getId(), testGarden.getId());
    testGarden.setIsGardenPublic(false);
    gardenRepository.save(testGarden);

    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> followerService.addFollower(newFollower));

    Assertions.assertEquals("Cannot follow this garden", exception.getMessage());
  }

  @Test
  void addFollower_ThrowsException_WhenGardenDoesNotExist() {
    Follower newFollower = new Follower(testGardener1.getId(), 99L);
    IllegalArgumentException exception =
        Assertions.assertThrows(
            IllegalArgumentException.class, () -> followerService.addFollower(newFollower));

    Assertions.assertEquals("Cannot follow this garden", exception.getMessage());
  }
}
