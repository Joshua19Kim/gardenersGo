package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class SecurityService {

    private LostPasswordTokenRepository lostPasswordTokenRepository;

    @Autowired
    public SecurityService(LostPasswordTokenRepository lostPasswordTokenRepository) {
        this.lostPasswordTokenRepository = lostPasswordTokenRepository;
    }

    public String validateLostPasswordToken(String token) {
        final Optional<LostPasswordToken> passToken = lostPasswordTokenRepository.findByToken(token);
//        System.out.println(passToken.get());
        return passToken.isEmpty() ? "invalidToken"
                : isTokenExpired(passToken.get()) ? "expired"
                : null;
    }


    private boolean isTokenExpired(LostPasswordToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public Optional<Gardener> findGardenerbyToken(String token){
        return lostPasswordTokenRepository.findByToken(token).map(LostPasswordToken::getGardener);
    }
}
