package lk.javainstitute.skilllink.model;

public class CardDetailsModel {
    private final String cardNumber;
    private final String validThru;
    private final String cvv;

    public CardDetailsModel(String cardNumber, String validThru, String cvv) {
        this.cardNumber = cardNumber;
        this.validThru = validThru;
        this.cvv = cvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getValidThru() {
        return validThru;
    }

    public String getCvv() {
        return cvv;
    }
}
