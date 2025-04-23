package lk.javainstitute.skilllink.model;

public class BankDetailsModel {
    private final String bankName;
    private final String accountNumber;
    private final String userName;
    private final String branch;
    private final int bankLogo;

    public BankDetailsModel(String bankName, String accountNumber, String userName, String branch, int bankLogo) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.userName = userName;
        this.branch = branch;
        this.bankLogo = bankLogo;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getUserName() {
        return userName;
    }

    public String getBranch() {
        return branch;
    }

    public int getBankLogo() {
        return bankLogo;
    }
}
