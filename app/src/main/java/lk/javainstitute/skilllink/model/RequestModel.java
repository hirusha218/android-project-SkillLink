package lk.javainstitute.skilllink.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestModel implements Serializable {

    //    private String category;
    private String jobType;
    private ArrayList<String> mediaUrls;
    private String city;
    private String workExperience;
    private String payment;
    private String workTime;

    private String JobId;

    private String userid;

    private String category;

    private String jobDescription;

    private long timestamp;

    private String status;


    public RequestModel() {

    }

    public RequestModel(String jobType, ArrayList<String> mediaUrls,
                        String city, String workExperience, String payment,
                        String workTime, String JobId, String userid,
                        String category, String jobDescription, String status) {
        this.jobType = jobType;
        this.mediaUrls = mediaUrls;
        this.city = city;
        this.workExperience = workExperience;
        this.payment = payment;
        this.workTime = workTime;
        this.JobId = JobId;
        this.userid = userid;
        this.category = category;
        this.jobDescription = jobDescription;
        this.timestamp = System.currentTimeMillis();
        this.status = status;


    }

    public RequestModel(String imageUrl, String name) {


    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
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

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }


}



