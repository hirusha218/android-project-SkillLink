package lk.javainstitute.skilllink.model;

import com.google.firebase.Timestamp;

public class Payment {
    private String paymentId;
    private String userId;
    private String jobId;
    private double amount;
    private String status; // "pending", "completed", "failed"
    private Timestamp timestamp;
    private String paymentMethod;
    private String description;

    public Payment() {
        // Empty constructor needed for Firestore
    }

    public Payment(String paymentId, String userId, String jobId, double amount,
                   String status, Timestamp timestamp, String paymentMethod, String description) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.jobId = jobId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
        this.paymentMethod = paymentMethod;
        this.description = description;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}