package lk.javainstitute.skilllink.model;

public class feedback {

    private String userId;
    private float rating;
    private String jobDescription;
    private String C_email;

    private String C_ProfileImgUrl;
    private String Up_date;
    private String Up_time;

    public feedback() {
    }

    public feedback(String userId, float rating, String jobDescription, String C_email, String Up_date, String Up_time, String C_profileImgUrl) {
        this.userId = userId;
        this.rating = rating;
        this.jobDescription = jobDescription;
        this.C_email = C_email;
        this.C_ProfileImgUrl = C_profileImgUrl;
        this.Up_date = Up_date;
        this.Up_time = Up_time;

    }

    public String getC_ProfileImgUrl() {
        return C_ProfileImgUrl;
    }

    public void setC_ProfileImgUrl(String C_ProfileImgUrl) {
        this.C_ProfileImgUrl = C_ProfileImgUrl;
    }

    public String getUp_date() {
        return Up_date;
    }

    public void setUp_date(String Up_date) {
        this.Up_date = Up_date;
    }

    public String getUp_time() {
        return Up_time;
    }

    public void setUp_time(String Up_time) {
        this.Up_time = Up_time;
    }

    public String getC_email() {
        return C_email;
    }

    public void setC_email(String C_email) {
        this.C_email = C_email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "userId='" + userId + '\'' +
                ", rating=" + rating +
                ", jobDescription='" + jobDescription + '\'' +
                ", C_email='" + C_email + '\'' +
                '}';
    }
}
