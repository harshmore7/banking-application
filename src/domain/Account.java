package domain;

public class Account {
    private String accountNumber;
    private String customer;
    private Double balance;
    private String accountType;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Account(String accountNumber, String customer, Double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = balance;
        this.accountType = accountType;
    }
}
