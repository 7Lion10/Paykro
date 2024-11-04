

package com.example.paykaro;

public class User_Database {
    public String username, email, phoneNumber, pin;
    public String accountName, bankName, accountNumber, panNumber, aadharNumber;
    public int amount;
    public User_Database(String username, String email, String phoneNumber, String accountName, String bank, String accountNum, String pan, String aadhar) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.accountName = accountName;
        this.bankName = bank;
        this.accountNumber = accountNum;
        this.panNumber = pan;
        this.aadharNumber = aadhar;
        this.amount = 5000; // Set initial balance to 5000 for new users
    }
}
