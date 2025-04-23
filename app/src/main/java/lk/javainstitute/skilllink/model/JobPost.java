package lk.javainstitute.skilllink.model;

import java.util.ArrayList;

public class JobPost {
    //    private String userId;
    private String category;
    private String jobType;
    private ArrayList<String> mediaUrls;
    private long timestamp;
    private String payment;
    private String city;
    private String workExperience;
    private String jobDescription;
    private String workTime;
    private String jobId;
    private String userId;
//    private String newId;

    // Constructor
    public JobPost(String userId, String category, String jobType, ArrayList<String> mediaUrls, long timestamp, String payment, String city, String workExperience, String jobDescription, String workTime, String newId) {
        this.userId = userId;
        this.category = category;
        this.jobType = jobType;
        this.mediaUrls = mediaUrls;
        this.timestamp = timestamp;
        this.payment = payment;
        this.city = city;
        this.workExperience = workExperience;
        this.jobDescription = jobDescription;
        this.workTime = workTime;
        this.jobId = newId;

    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public ArrayList<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(ArrayList<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

//    public String getNewId() {
//        return newId;
//    }
//
//    public void setNewId(String newId) {
//        this.newId = newId;
//    }
}