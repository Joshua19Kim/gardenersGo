package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;


/**
 * Entity class containing the token for a user to reset their password and its expiry info
 * Inspired by https://www.baeldung.com/spring-security-registration-i-forgot-my-password
 */
@Entity
public class LostPasswordToken {
    // 10 minute expiry
    private static final int EXPIRATION = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Gardener.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "gardener_id")
    private Gardener gardener;

    private Date expiryDate;


    /**
     * JPA required no-args constructor
     */
    protected LostPasswordToken() {}

    /**
     * Creates a LostPasswordToken object
     * @param token the token to be saved
     * @param gardener the gardener being emailed
     */
    public LostPasswordToken(String token, Gardener gardener) {
        this.token = token;
        this.gardener = gardener;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION);
        this.expiryDate = new Date(calendar.getTime().getTime());
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Gardener getGardener() {
        return gardener;
    }
}