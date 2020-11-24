package banking;

public class Card {

    private String cardNumber;
    String pinCode;
    private int accountBalance;

    public Card () {
    }

    public Card (String cardNumber, String pinCode, int accountBalance) {
        this.cardNumber = cardNumber;
        this.pinCode = pinCode;
        this.accountBalance = accountBalance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPinCode() {
        return pinCode;
    }

    public int getAccountBalance() {
        return accountBalance;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void setAccountBalance (int accountBalance) {
        this.accountBalance = accountBalance;
    }


}
