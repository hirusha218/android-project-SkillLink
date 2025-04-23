package lk.javainstitute.skilllink.model;

public class UserProfile {
    private String fName;
    private String lName;
    private String email;
    private String mobile;
    private String address1;
    private String address2;
    private String imageUrl;
    private String gender;
    private String nic;


    // Default constructor (required for Firebase)
    public UserProfile() {
    }

    public UserProfile(String fName, String lName, String email, String mobile, String address1, String address2, String imageUrl, String gender, String nic) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.mobile = mobile;
        this.address1 = address1;
        this.address2 = address2;
        this.imageUrl = imageUrl;
        this.gender = gender;
        this.nic = nic;


    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }
}