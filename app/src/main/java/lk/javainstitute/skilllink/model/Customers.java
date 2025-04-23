package lk.javainstitute.skilllink.model;

public class Customers {

    private String firstName;
    private String lastName;
    private String email;
    private String ImageUrl;
    private String userId;

    public Customers() {
        // Default constructor for Firebase
    }

    public Customers(String firstName, String lastName, String email, String ImageUrl, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.ImageUrl = ImageUrl;
//        this.userId = userId;
    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setProfileImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }
}
