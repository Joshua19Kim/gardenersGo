//package nz.ac.canterbury.seng302.gardenersgrove.integration.service;
//
//import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
//import nz.ac.canterbury.seng302.gardenersgrove.entity.Relationships;
//import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
//import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
//import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
//import nz.ac.canterbury.seng302.gardenersgrove.repository.RelationshipRepository;
//import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
//import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@DataJpaTest
//@Import({RelationshipService.class, GardenerFormService.class})
//public class RelationshipServiceTest {
//
//    @Autowired
//    private RelationshipRepository relationshipRepository;
//
//    @Autowired
//    private GardenerFormRepository gardenerFormRepository;
//
//    private GardenerFormService gardenerFormService;
//
//    private RelationshipService relationshipService;
//
//    @Autowired
//    private LostPasswordTokenRepository lostPasswordTokenRepository;
//
//    private List<Long> gardenerIds;
//
//    private List<Gardener> gardeners;
//
//    @BeforeEach
//    public void setUp() {
//        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
//        relationshipService = new RelationshipService(relationshipRepository, gardenerFormService);
//        gardenerIds = new ArrayList<>();
//        gardeners = new ArrayList<>();
//        for(int i = 0; i < 5; i++) {
//            Gardener gardener = gardenerFormService.addGardener(new Gardener("Jeff" + i, "Ryan", LocalDate.now(), "test@gmail.com", "-1009294837"));
//            gardenerIds.add(gardener.getId());
//            gardeners.add(gardener);
//
//        }
//    }
//
//    @AfterEach
//    public void tearDown() {
//        gardenerFormRepository.deleteAll();
//        relationshipRepository.deleteAll();
//    }
//
//    @Test
//    public void GetFriends_ValidUser_FriendsReturned() {
//        Relationships relationship = new Relationships(gardenerIds.get(0), gardenerIds.get(1), "accepted");
//        Relationships relationship2 = new Relationships(gardenerIds.get(0),  gardenerIds.get(2), "accepted");
//        relationshipService.addRelationship(relationship);
//        relationshipService.addRelationship(relationship2);
//        List<Gardener> friends = relationshipService.getCurrentUserRelationships(relationship.getGardenerId());
//        Assertions.assertEquals(friends.get(0).getId(), relationship.getFriendId());
//        Assertions.assertEquals(friends.get(1).getId(), relationship2.getFriendId());
//
//    }
//
//    @Test
//    public void GetOutgoingRequests_ValidUser_OutgoingRequestsReturned() {
//        Relationships relationship = new Relationships(gardenerIds.get(0), gardenerIds.get(1),"pending");
//        Relationships relationship2 = new Relationships(gardenerIds.get(0), gardenerIds.get(2), "pending");
//        relationshipService.addRelationship(relationship);
//        relationshipService.addRelationship(relationship2);
//        List<Gardener> outgoingFriends = relationshipService.getGardenerPending(relationship.getGardenerId());
//        Assertions.assertEquals(outgoingFriends.get(0).getId(), relationship.getFriendId());
//        Assertions.assertEquals(outgoingFriends.get(1).getId(), relationship2.getFriendId());
//
//    }
//
//    @Test
//    public void GetIncomingRequests_ValidUser_IncomingRequestsReturned() {
//        Relationships relationship = new Relationships(gardenerIds.get(1), gardenerIds.get(0),"pending");
//        Relationships relationship2 = new Relationships(gardenerIds.get(2), gardenerIds.get(0), "pending");
//        relationshipService.addRelationship(relationship);
//        relationshipService.addRelationship(relationship2);
//        List<Gardener> incomingFriends = relationshipService.getGardenerIncoming(relationship.getFriendId());
//        Assertions.assertEquals(incomingFriends.get(0).getId(), relationship.getGardenerId());
//        Assertions.assertEquals(incomingFriends.get(1).getId(), relationship2.getGardenerId());
//
//    }
//
//    @Test
//    public void GetDeclinedRequests_ValidUser_DeclinedRequestsReturned() {
//        Relationships relationship = new Relationships(gardenerIds.get(0), gardenerIds.get(1),"declined");
//        Relationships relationship2 = new Relationships(gardenerIds.get(0), gardenerIds.get(2), "declined");
//        relationshipService.addRelationship(relationship);
//        relationshipService.addRelationship(relationship2);
//        List<Gardener> declinedFriends = relationshipService.getGardenerDeclinedRequests(relationship.getGardenerId());
//        Assertions.assertEquals(declinedFriends.get(0).getId(), relationship.getFriendId());
//        Assertions.assertEquals(declinedFriends.get(1).getId(), relationship2.getFriendId());
//
//    }
//
//    @Test
//    public void GetUsersWithNoRelationships_ValidUsers_UsersWithNoRelationshipsReturned() {
//        List<Gardener> gardenersInRelationShips = new ArrayList<>();
//        Relationships relationship = new Relationships(gardenerIds.get(0), gardenerIds.get(1),"declined");
//        Relationships relationship2 = new Relationships(gardenerIds.get(0), gardenerIds.get(2), "declined");
//        gardenersInRelationShips.add(gardeners.get(0));
//        gardenersInRelationShips.add(gardeners.get(1));
//        gardenersInRelationShips.add(gardeners.get(2));
//        relationshipService.addRelationship(relationship);
//        relationshipService.addRelationship(relationship2);
//        List<Gardener> gardenersWithNoRelationship = relationshipService.getGardenersWithNoRelationship(gardenersInRelationShips, gardeners);
//        Assertions.assertEquals(gardenersWithNoRelationship.get(0), gardeners.get(3));
//        Assertions.assertEquals(gardenersWithNoRelationship.get(1), gardeners.get(4));
//
//    }
//
//    @Test
//    public void UpdateRelationshipStatusToAccepted_StatusPending_StatusUpdatedToAccepted() {
//        Relationships relationship = new Relationships(gardenerIds.get(0), gardenerIds.get(1),"pending");
//        relationshipService.addRelationship(relationship);
//        relationshipService.updateRelationshipStatus("accepted", gardenerIds.get(0), gardenerIds.get(1));
//        relationship = relationshipService.getRelationShip(gardenerIds.get(0), gardenerIds.get(1)).get();
//        Assertions.assertEquals("accepted", relationship.getStatus());
//
//    }
//}
