package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

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
}
