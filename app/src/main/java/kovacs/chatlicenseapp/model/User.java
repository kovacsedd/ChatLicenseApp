package kovacs.chatlicenseapp.model;

public class User {
    private String userID;
    private String username;
    private String email;
    private String imageProfile;
    private String bio;
    private String status;

    public User() {
    }

    public User(String userID, String username, String email, String imageProfile, String bio, String status) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.imageProfile = imageProfile;
        this.bio = bio;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}