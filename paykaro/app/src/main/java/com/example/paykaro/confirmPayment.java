package com.example.paykaro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class confirmPayment extends AppCompatActivity {

    private TextView amountPaid;
    private TextView payeeInfo;
    private TextView upiId;
    private TextView dateTextView;
    private Button doneButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        amountPaid = findViewById(R.id.amount_paid);
        payeeInfo = findViewById(R.id.payee_info);
        upiId = findViewById(R.id.upi_id);
        dateTextView = findViewById(R.id.transaction_date);
        doneButton = findViewById(R.id.done_button);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String contactName = intent.getStringExtra("contact_name");
        String phoneNumber = intent.getStringExtra("phone_number");
        String bankName = intent.getStringExtra("bank_name");

        // Display the payment amount and payee info
        amountPaid.setText("₹ " + amount);
        payeeInfo.setText("Paid to " + contactName + " (" + phoneNumber + ")");
        upiId.setText("UPI ID: " + generateRandomUpiId(bankName));

        // Set the current date and time
        String currentDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        dateTextView.setText("Date: " + currentDate);


        // Button to go back to the MainActivity
        doneButton.setOnClickListener(v -> {
            Intent mainActivityIntent = new Intent(confirmPayment.this, MainActivity.class);
            startActivity(mainActivityIntent);
        });
    }

    // Generate a random UPI ID based on the bank name
    private String generateRandomUpiId(String bankName) {
        Random random = new Random();
        int randomNum = random.nextInt(900000) + 100000;  // Generates a random 6-digit number
        return "UPI" + randomNum + "@" + bankName.toLowerCase().replaceAll("\\s", "");
    }
}