package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class reflecting an entry of firstName, optional lastName, date of birth, email, and password
 * Note the @link{Entity} annotation required for declaring this as a persistence entity
 */
@Entity
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

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "gardener_id")
    private List<Authority> userRoles;
    // Create an encoder with strength 16


    /**
     * JPA required no-args constructor
     */
    protected Gardener() {}

    /**
     * Creates a new Gardener object
     * @param firstName first name of user
     * @param DoB user's date of birth
     * @param email user's email
     * @param password user's password
     */
    public Gardener(String firstName, String lastName, LocalDate DoB, String email, String password, String profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.DoB = DoB;
        this.email = email;
        this.password = hashPasword(password);
        this.profilePicture = profilePicture;
    }

    public void grantAuthority(String authority) {
        if ( userRoles == null )
            userRoles = new ArrayList<>();
        userRoles.add(new Authority(authority));
    }
    public void grantAuthorities(List<String> roles) {
        if ( userRoles == null )
            userRoles = new ArrayList<>();
        for (String s: roles) {
            userRoles.add(new Authority(s));
        }
    }

    public List<GrantedAuthority> getAuthorities(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.userRoles.forEach(authority -> authorities.add(new SimpleGrantedAuthority(authority.getRole())));
        return authorities;
    }
    public void setUserRoles(List<Authority> userRoles) { this.userRoles = userRoles;}

    public String hashPasword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

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

    public LocalDate getDoB() {
        return DoB;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {return password; }

    public String getProfilePicture() { return this.profilePicture; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setDoB(LocalDate DoB) { this.DoB = DoB; }

    public void setEmail(String email) {this.email = email; }

    public void setProfilePicture(String imageLocation) {
        this.profilePicture = imageLocation;
    }

    public void updatePassword(String password) { this.password = hashPasword(password); }

    @Override
    public String toString() {
        String gardenerString = firstName;
        if (getLastName() != null) {
            gardenerString += " " + lastName;
        }
        gardenerString += " - " + email;

        return gardenerString;
    }

}
