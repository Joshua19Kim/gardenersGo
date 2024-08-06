package nz.ac.canterbury.seng302.gardenersgrove.unit.entity;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GardenerEntityTest {

    @Autowired
    private GardenerFormRepository gardenerFormRepository;

    @Test
    void testBanGardener() {
        // Test banning a gardener
        Gardener gardener = new Gardener("Kush", "Desai", null, "a@gmail.com", "Password1!");
        gardenerFormRepository.save(gardener);
        gardener.banGardener();
        gardenerFormRepository.save(gardener);
        Date bannedDate = gardener.getBanExpiryDate();
        // assert that bannedDate is 1 week from now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.DATE, 7);
        Date expectedDate = new Date(calendar.getTime().getTime());
        assertEquals(expectedDate.toString(), bannedDate.toString());
    }

    @Test
    void checkUnbanGardener() {
        // Test unbanning a gardener
        Gardener gardener = new Gardener("Kush", "Desai", null, "a@gmail.com", "Password1!");
        gardenerFormRepository.save(gardener);
        gardenerFormRepository.addBanExpiryDateByEmail("a@gmail.com", new Date());

        assertFalse(gardener.isBanned());
    }

    @Test
    void checkBanningGardener() {
        // Test unbanning a gardener
        Gardener gardener = new Gardener("Kush", "Desai", null, "a@gmail.com", "Password1!");
        gardenerFormRepository.save(gardener);
        gardener.banGardener();
        gardenerFormRepository.save(gardener);
        assertTrue(gardener.isBanned());
    }

}
