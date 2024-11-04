package com.example.paykaro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class confirm_bank_trans extends AppCompatActivity {
    private TextView amountPaidText, payeeInfoText, dateTimeText, transactionIdText;
    private Button doneButton; // Declare the Done button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_bank_trans);

        amountPaidText = findViewById(R.id.amount_paid);
        payeeInfoText = findViewById(R.id.payee_info);
        dateTimeText = findViewById(R.id.transaction_date); // Make sure this ID matches your layout
        transactionIdText = findViewById(R.id.transaction_id); // Make sure this ID matches your layout
        doneButton = findViewById(R.id.done_button); // Initialize the Done button

        // Get data from the Intent
        String holderName = getIntent().getStringExtra("contact_name");
        String amount = getIntent().getStringExtra("amount");

        // Set the retrieved data in TextViews
        amountPaidText.setText("₹" + amount);
        payeeInfoText.setText("Paid to " + holderName);

        // Get the current date and time
        String currentDateTime = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        dateTimeText.setText(currentDateTime);

        // Generate a random transaction ID
        String transactionId = generateTransactionId();
        transactionIdText.setText("Transaction ID: " + transactionId);

        // Set OnClickListener for Done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to MainActivity
                Intent intent = new Intent(confirm_bank_trans.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    // Method to generate a random transaction ID
    private String generateTransactionId() {
        Random random = new Random();
        return "TXN" + String.format("%04d", random.nextInt(10000)); // Generates a random 4-digit number
    }
}
