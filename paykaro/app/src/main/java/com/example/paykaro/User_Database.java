

package com.example.paykaro;

public class User_Database {
    public String username, email, phoneNumber, pin;
    public String accountName, bankName, accountNumber, panNumber, aadharNumber;
    public int balance;
    public User_Database(String username, String email, String phoneNumber, String accountName, String bank, String accountNum, String pan, String aadhar,String pin) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.accountName = accountName;
        this.bankName = bank;
        this.accountNumber = accountNum;
        this.panNumber = pan;
        this.aadharNumber = aadhar;
        this.balance = 5000; // Set initial balance to 5000 for new users
    }
    public User_Database(){}
    // Getters
    public String getAccountHolderName() {
        return accountName;
    }

    public String getUsername() {
        return username;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public int getBalance() {
        return balance;
    }

    public String getPin() {
        return pin;
    }
}
