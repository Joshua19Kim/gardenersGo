package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class reflecting an entry of firstName, optional lastName, date of birth, email, and password
 * Note the @link{Entity} annotation required for declaring this as a persistence entity
 */
@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relationship_id")
    private Long id;

    @Column(nullable = false)
    private int gardenerId;

    @Column(nullable = false)
    private int friendId;

    @Column(nullable = false)
    private String status;



    @Column()
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "gardener_id")
    private List<Authority> userRoles;

    /**
     * JPA required no-args constructor
     */
    protected Friends() {}

    /**
     * Creates a new Gardener object
     * @param gardenerId the id of the logged-in user
     * @param friendId the id of the gardener the relationship is with
     * @param status status of the relationship between gardeners e.g. "accepted" or "pending"
     */
    public Friends(int gardenerId, int friendId, String status) {
        this.gardenerId = gardenerId;
        this.friendId = friendId;
        this.status = status;
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

    public Long getId() {
        return id;
    }

    public int getGardenerId() {
        return gardenerId;
    }

    public int getFriendId() {
        return friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setGardenerId(int gardenerId) { this.gardenerId = gardenerId; }

    public void setFriendId(int friendId) { this.friendId = friendId; }

    public void setStatus(String status) { this.status = status; }


    @Override
    public String toString() {
        String relationshipString = "Relationship{" +
                "relationshipId = " + id +
                ": Gardener (id) = " + gardenerId + '\'' +
                "Friend (id) = " + friendId + '\'' +
                "Relationship Status = " + status;

        return relationshipString;
    }
}
