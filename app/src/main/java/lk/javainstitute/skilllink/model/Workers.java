package lk.javainstitute.skilllink.model;

public class Workers {

    private String fname;
    private String lname;
    private String email;
    private String skill;
    private String password;
    private String imageUrl;
    //    private String userId;
    private String city;

    public Workers() {

    }

    public Workers(String fname, String lname, String mobile, String skill, String password, String imageUrl) {
        this.fname = fname;
        this.lname = lname;
        this.email = mobile;
        this.skill = skill;
        this.password = password;
        this.imageUrl = imageUrl;
//        this.userId = userId;


    }

    public Workers(String firstName, String lastName, String email, String skill, String imageUr) {
    }


//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }


    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
