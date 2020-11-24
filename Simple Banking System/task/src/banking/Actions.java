package banking;

import java.math.BigInteger;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Actions {
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    public static void menu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");

        int input = scanner.nextInt();
        switch (input) {
            case(0):
                System.out.println("Bye");
                break;
            case(1):
                createAnAccount();
                menu();
                break;
            case(2):
                logIntoAccount();
                break;
            default:
                System.out.println("wrong choice");
                break;
        }
    }

    public static void accountMenu(Card card)  {
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
        int input = scanner.nextInt();
        switch (input) {
            case(0):
                System.out.println("Bye");
                break;
            case(1):
                System.out.println("Balance " + balance(card));
                accountMenu(card);
                break;
            case(2):
                addIncome(card);
                accountMenu(card);
                break;
            case(3):
                doTransfer(card);
                break;
            case(4):
                closeAccount(card);
                menu();
                break;
            case(5):
                menu();
                break;
            default:
                System.out.println("Wrong choice");
                break;
        }
    }

    public static void createAnAccount() {
        Card card = new Card(generateAccountNumber(), generatePin(), 0);
        CoolJDBC.insertNewCard(card.getCardNumber(), card.getPinCode());

        System.out.printf("Your card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                "Your card PIN:\n" +
                        "%s\n", card.getCardNumber(), card.getPinCode());
        System.out.println();
    }

    public static void logIntoAccount() {
        System.out.println("Enter your card number:");
        String cardNumberInput = scanner.next();

        System.out.println("Enter your PIN:");
        String pinCode = scanner.next();

        if (CoolJDBC.getAccountDetails(cardNumberInput).getCardNumber().equals("1")) {
            System.out.println("Wrong card number or PIN!\n");
            menu();
        } else {
            if (CoolJDBC.getAccountDetails(cardNumberInput).getPinCode().equals(pinCode)) {
                System.out.println("You have successfully logged in!\n");
                accountMenu(CoolJDBC.getAccountDetails(cardNumberInput));
            } else {
                System.out.println("Wrong card number or PIN!\n");
                menu();
            }
        }
    }

    public static String generateAccountNumber() {
        int identifierLength = 9;
        String accountIdentifier = "";

        for (int i = 0; i < identifierLength; i++) {
            accountIdentifier += random.nextInt(9);
        }
        String account = "400000" + accountIdentifier;

        return account  + generateCheckSum(account);
    }

    // counting checksum according to Luhn Algorithm
    private static int generateCheckSum(String account) {
        int checkSum = sumForLuhn(account) % 10 == 0 ? 0 : 10 - sumForLuhn(account) % 10;
        return checkSum;
    }

    private static int sumForLuhn(String account) {
        if (account.length() > 15) {
            account = account.substring(0,15);
        }
        int accountLength = account.length();
        int sumForLuhn = 0;

        int[] accountArrayForLuhn = new int[accountLength];

        for (int i = 0; i < accountLength; i++) {
            int temp = Integer.parseInt(String.valueOf(account.charAt(i)));
            if (i % 2 == 0) {
                accountArrayForLuhn[i] = temp * 2 > 9
                        ? temp * 2 - 9 : temp * 2;
            } else {
                accountArrayForLuhn[i] = temp;
            }
            sumForLuhn += accountArrayForLuhn[i];
        }
        return sumForLuhn;
    }

    public static boolean checkCardNumber(String cardNumber) {
        int sumForLuhn = sumForLuhn(cardNumber);
        int checkSum = Integer.parseInt(cardNumber.substring(15));
        return  (sumForLuhn + checkSum) % 10 == 0 ? true : false;
    }

    public static String generatePin() {
        int pinLength = 4;
        String pinCode = "";
        for (int i = 0; i < pinLength; i++) {
        pinCode += random.nextInt(9);
        }
        return pinCode;
    }

    public static double balance(Card card) {
        return CoolJDBC.getBalance(card.getCardNumber());
    }

    private static void addIncome(Card card) {
        System.out.println("Enter income:");
        int income = scanner.nextInt();
        CoolJDBC.addIncome(income, card.getCardNumber());
        System.out.println("Income was added!\n");
    }

    private static void doTransfer(Card card) {
        int amount = 0;
        System.out.println("Transfer\n" +
                "Enter card number:");
        scanner.nextLine();
        String beneficiaryCardNumber = scanner.nextLine();
        if (!checkCardNumber(beneficiaryCardNumber)) {
            System.out.println("Probably you made mistake in the card number. Please try again!\n");
        } else {
            if (CoolJDBC.getAccountDetails(beneficiaryCardNumber).getCardNumber().equals("1")) {
                System.out.println("Such a card does not exist.\n");
            } else {
                System.out.println("Enter how much money you want to transfer:");
                amount = scanner.nextInt();
                if (amount <= balance(card)) {
                    CoolJDBC.doTransfer(card.getCardNumber(), beneficiaryCardNumber, amount);
                    System.out.println("Success!\n");
                } else {
                    System.out.println("Not enough money!\n");
                }
            }
        }
        accountMenu(card);
    }

    private static void closeAccount(Card card) {
        CoolJDBC.deleteAccount(card.getCardNumber());
        System.out.println("The account has been closed!\n");
    }
}
