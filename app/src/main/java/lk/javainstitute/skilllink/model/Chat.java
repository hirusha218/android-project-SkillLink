package lk.javainstitute.skilllink.model;

public class Chat {
    private String userId;
    private String userName;
    private String lastMessage;
    private String status;
    private String avatarUrl;

    public Chat(String userId, String userName, String lastMessage, String status, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.status = status;
        this.avatarUrl = avatarUrl;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


}