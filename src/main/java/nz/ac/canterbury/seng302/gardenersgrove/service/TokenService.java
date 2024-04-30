package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
public class TokenService {

    private LostPasswordTokenRepository lostPasswordTokenRepository;

    @Autowired
    public TokenService(LostPasswordTokenRepository lostPasswordTokenRepository) {
        this.lostPasswordTokenRepository = lostPasswordTokenRepository;
    }

    public Optional<LostPasswordToken> getTokenFromString(String token){
        return lostPasswordTokenRepository.findByToken(token);
    }

    public void removeToken(LostPasswordToken token) {
        lostPasswordTokenRepository.delete(token);
    }

    public String validateLostPasswordToken(String token) {
        final Optional<LostPasswordToken> passToken = lostPasswordTokenRepository.findByToken(token);
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

    public void createLostPasswordTokenForGardener(Gardener gardener, String token) {
        Optional<LostPasswordToken> checkToken = lostPasswordTokenRepository.findByGardener(gardener);
        if (checkToken.isPresent()) { // Deletes value if present
            lostPasswordTokenRepository.delete(checkToken.get());
        }
        LostPasswordToken generatedToken = new LostPasswordToken(token, gardener);
        lostPasswordTokenRepository.save(generatedToken);
    }
}
