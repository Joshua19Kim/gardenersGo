package nz.ac.canterbury.seng302.gardenersgrove.entity;


import jakarta.persistence.Id;


public class Following {
    @Id
    private Long owner;
    private int followerCount;
    private Long follower;
    private String followerFirstName;
    private String followerLastName;

    /**
     * JPA required no-args constructor
     */
    protected Following() {}

    public Following(Long owner, int count, Long follower, String followerFirstName, String followerLastName) {
        this.owner = owner;
        this.followerCount = count;
        this.follower= follower;
        this.followerFirstName= followerFirstName;
        this.followerLastName=followerLastName;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public Long getFollower() {
        return follower;
    }

    public void setFollower(Long follower) {
        this.follower = follower;
    }

    public String getFollowerFirstName() {
        return followerFirstName;
    }

    public void setFollowerFirstName(String followerFirstName) {
        this.followerFirstName = followerFirstName;
    }

    public String getFollowerLastName() {
        return followerLastName;
    }

    public void setFollowerLastName(String followerLastName) {
        this.followerLastName = followerLastName;
    }
}
