package kovacs.chatlicenseapp.model;

public class ChatList {
    private String userID;
    private String userName;
    private String urlProfile;

    public ChatList() {
    }

    public ChatList(String userID, String userName, String urlProfile) {
        this.userID = userID;
        this.userName = userName;
        this.urlProfile = urlProfile;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }
}
