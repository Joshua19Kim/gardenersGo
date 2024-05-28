package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "authority")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gardener_id")
    private Gardener gardener;

    @Column()
    private String role;

    protected Authority() {
        // JPA empty constructor
    }

    public Authority(String role) {
        this.role = role;
    }

    public Authority(Gardener gardener, String role) {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

