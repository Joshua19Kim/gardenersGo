package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Entity class reflecting an entry of firstName, optional lastName, date of birth, email, and password
 * Note the @link{Entity} annotation required for declaring this as a persistence entity
 */
@Entity
@Table(name = "gardener")
public class Gardener {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gardener_id")
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column()
    private String lastName;

    @Column()
    private LocalDate DoB;

    @Column(length = 320, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "profile_picture")
    private String profilePicture;

    /**
     * A counter for how many bad words the gardener tries to use
     */
    @Column(name = "bad_word_count")
    private Integer badWordCount;

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "gardener_id")
    private List<Authority> userRoles;
    // Create an encoder with strength 16

    /**
     * The list of gardens belonging to the gardener.
     */
    @OneToMany(mappedBy = "gardener")
    private List<Garden> gardens;

    @Column(name = "ban_expiry_date")
    private Date banExpiryDate;

    /**
     * The list of badges belonging to the gardener.
     */
    @OneToMany(mappedBy = "gardener")
    private List<Badge> badges;


    /**
     * JPA required no-args constructor
     */
    protected Gardener() {
    }

    /**
     * Creates a new Gardener object
     *
     * @param firstName first name of user
     * @param lastName  last name of user
     * @param DoB       user's date of birth
     * @param email     user's email
     * @param password  user's password
     */
    public Gardener(String firstName, String lastName, LocalDate DoB, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.DoB = DoB;
        this.email = email;
        this.password = hashPasword(password);
        this.profilePicture = "/images/defaultProfilePic.png";
        this.badWordCount = 0;
        gardens = new ArrayList<>();
    }

    public void grantAuthority(String authority) {
        if (userRoles == null)
            userRoles = new ArrayList<>();
        userRoles.add(new Authority(authority));
    }

    public void grantAuthorities(List<String> roles) {
        if (userRoles == null)
            userRoles = new ArrayList<>();
        for (String s : roles) {
            userRoles.add(new Authority(s));
        }
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        return authorities;
    }

    public void setUserRoles(List<Authority> userRoles) {
        this.userRoles = userRoles;
    }

    public String hashPasword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    /**
     * Compares the given password with the stored password using bcrypt
     *
     * @param password the password to compare
     * @return true if the passwords match, false otherwise
     */
    public boolean comparePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, this.password);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + (lastName != null ? " " : "") + lastName;
    }

    public LocalDate getDoB() {
        return DoB;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public int getBadWordCount() {
        return badWordCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Garden> getGardens() {
        return gardens;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDoB(LocalDate DoB) {
        this.DoB = DoB;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfilePicture(String imageLocation) {
        this.profilePicture = imageLocation;
    }

    public void updatePassword(String password) {
        this.password = hashPasword(password);
    }

    public void setGardens(List<Garden> gardens) {
        this.gardens = gardens;
    }

    public void setBadWordCount(int badWordCount) {
        this.badWordCount = badWordCount;
    }

    @Override
    public String toString() {
        String gardenerString = firstName;
        if (getLastName() != null) {
            gardenerString += " " + lastName;
        }
        gardenerString += " - " + email;

        return gardenerString;
    }

    /**
     * Bans the gardener for 7 days
     */
    public void banGardener() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.DATE, 7);
        this.banExpiryDate = new Date(calendar.getTime().getTime());
    }


    /**
     * @return true if the gardener is banned, false otherwise
     * if the ban date has expired, the ban is lifted
     */
    public boolean isBanned() {
        if (banExpiryDate != null)
            if (banExpiryDate.after(new Date())) {
                return true;
            } else {
                this.banExpiryDate = null;
                this.badWordCount = 0;
            }
        return false;
    }

    public Date getBanExpiryDate() {
        return banExpiryDate;
    }
}
