package lk.javainstitute.skilllink.model;

public class PaymentDetails {
    private String paymentId;
    private String workerMobile;
    private double amount;
    private String paymentMethod;
    private long timestamp;
    private String orderId;
    private String paymentNo;

    // Empty constructor required for Firebase
    public PaymentDetails() {
    }

    public PaymentDetails(String paymentId, String workerMobile, double amount,
                          String paymentMethod, long timestamp, String orderId, String paymentNo) {
        this.paymentId = paymentId;
        this.workerMobile = workerMobile;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.paymentNo = paymentNo;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getWorkerMobile() {
        return workerMobile;
    }

    public void setWorkerMobile(String workerMobile) {
        this.workerMobile = workerMobile;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }
} 